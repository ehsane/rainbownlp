package rainbownlp.machinelearning;

import java.util.ArrayList;
import java.util.List;

import rainbownlp.machinelearning.convertor.WekaFormatConvertor;
import rainbownlp.util.ConfigurationUtil;
import rainbownlp.util.HibernateUtil;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaClassifier extends LearnerEngine {
	String modelFile;
	String taskName;
	String trainFile;
	String testFile;
	
	int reinforcedCount = 0;
	String[] reinforcedModels = new String[reinforcedCount];
	public Classifier wekaAlgorithm = new NaiveBayes();
	public String[] options = null;
	public String wekaAlgorithmName = "NaiveBayes";
	private WekaClassifier()
	{
		
	}

	@Override
	public void train(List<MLExample> pTrainExamples) throws Exception {
		ConfigurationUtil.TrainingMode = true;
		setPaths();

		//This part added since the session was so slow
		List<Integer> train_example_ids = new ArrayList<Integer>();
		for(MLExample example : pTrainExamples)
		{
			train_example_ids.add(example.getExampleId());
		}
		WekaFormatConvertor.writeToFile(train_example_ids, trainFile,taskName, new String[]{"1", "2"});

		DataSource source = new DataSource(trainFile);
		Instances data = source.getDataSet();
		// setting class attribute if the data format does not provide this information
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);

		if(options!=null)
			wekaAlgorithm.setOptions(options);     // set the options
		wekaAlgorithm.buildClassifier(data);   // build classifier
		
		 // serialize model
		 SerializationHelper.write(modelFile, wekaAlgorithm);
	}
	@Override
	public void test(List<MLExample> pTestExamples) throws Exception {
		ConfigurationUtil.TrainingMode = false;
		List<Integer> test_example_ids = new ArrayList<Integer>();
		String exampleids = "";
		for(MLExample example : pTestExamples)
		{
			exampleids = exampleids.concat(","+example.getExampleId());
			test_example_ids.add(example.getExampleId());
		}
			
		exampleids = exampleids.replaceFirst(",", "");
		String resetQuery = "update MLExample set predictedClass = -1 where exampleId in ("+ exampleids +")";
		HibernateUtil.executeNonReader(resetQuery);
					
		WekaFormatConvertor.writeToFile(test_example_ids, testFile,taskName, new String[]{"1", "2"});
		
		// deserialize model
		wekaAlgorithm = (Classifier) SerializationHelper.read(modelFile);
		 
		// classify new examples and update database
		DataSource source = new DataSource(testFile);
		Instances testData = source.getDataSet();
		// setting class attribute if the data format does not provide this information
		if (testData.classIndex() == -1)
			testData.setClassIndex(testData.numAttributes() - 1);
	
		System.out.println(pTestExamples.size() +"=="+ testData.numInstances());
		int counter = 0;
		while (counter<pTestExamples.size() && counter<testData.numInstances()) {
			Double clsLabel = wekaAlgorithm.classifyInstance(testData.instance(counter));
			   
			pTestExamples.get(counter).setPredictedClass(clsLabel.toString());
			MLExample test = pTestExamples.get(counter);
			String savePredictedQuery = "update MLExample set predictedClass ="+test.getPredictedClass()+
					" where exampleId="+test.getExampleId();
			HibernateUtil.executeNonReader(savePredictedQuery);
			
			counter++;
			System.out.println("Processed :"+counter+"/"+pTestExamples.size());
		}
		
		 Evaluation eval = new Evaluation(testData);
		 eval.evaluateModel(wekaAlgorithm, testData);
		 System.out.println("\n====\n"+eval.toSummaryString()+"\n"+eval.toClassDetailsString()+"\n====\n");
	}

	public static LearnerEngine getLearnerEngine(String pTaskName) {
		WekaClassifier learnerEngine = new WekaClassifier();
		learnerEngine.setTaskName(pTaskName);
		
		learnerEngine.setPaths();
		return learnerEngine;
	}

	private void setPaths() {
		String fold = (ConfigurationUtil.crossFoldCurrent>0)?("Fold"+ConfigurationUtil.crossFoldCurrent):"";
		setModelFilePath(ConfigurationUtil.getValue("TempFolder")+
				fold+"-"+wekaAlgorithmName+
				taskName+".weka");
		setTrainFilePath(ConfigurationUtil.getValue("TempFolder")+
				fold+"-"+wekaAlgorithmName+
				"-train-" + taskName + ".arff");
		setTestFilePath(ConfigurationUtil.getValue("TempFolder")+
				fold+"-" + wekaAlgorithmName+
				"-test-" + taskName + ".arff");	
	}

	private void setTestFilePath(String pTestFile) {
		testFile = pTestFile;
	}

	private void setTrainFilePath(String pTrainFile) {
		trainFile = pTrainFile;		
	}

	private void setModelFilePath(String pModelFile) {
		modelFile = pModelFile;
	}


}
