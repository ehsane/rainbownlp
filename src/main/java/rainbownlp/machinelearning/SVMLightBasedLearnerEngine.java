package rainbownlp.machinelearning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import rainbownlp.machinelearning.convertor.SVMLightFormatConvertor;
import rainbownlp.util.ConfigurationUtil;
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.SystemUtil;


public abstract class SVMLightBasedLearnerEngine extends LearnerEngine {

	
	public void train(List<MLExample> pTrainExamples) throws Exception {
		ConfigurationUtil.TrainingMode = true;
		HibernateUtil.clearLoaderSession();
		
		//This part added since the session was so slow
		List<Integer> train_example_ids = new ArrayList<Integer>();
		for(MLExample example : pTrainExamples)
		{
			train_example_ids.add(example.getExampleId());
		}
		if(isBinaryClassification())
			trainFile = SVMLightFormatConvertor.writeToFileBinary(train_example_ids, taskName);
		else
			trainFile = SVMLightFormatConvertor.writeToFile(train_example_ids, taskName);

		String myShellScript = getTrainCommand();

		SystemUtil.runShellCommand(myShellScript);
	}
	
	public void test(List<MLExample> pTestExamples) throws Exception{
		File model = new File(getModelFilePath());
		if(!model.exists()) {
			throw(new Exception("Model file is missing, train before test: "+modelFile));
		}
		
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
					
		String resultFile = modelFile+"_result.txt";
		
		if(isBinaryClassification())
			testFile = SVMLightFormatConvertor.writeToFileBinary(test_example_ids, taskName);
		else 
			testFile = SVMLightFormatConvertor.writeToFile(test_example_ids, taskName);
		
		
		SystemUtil.runShellCommand(getTestCommand(resultFile));
				
		File f=new File(resultFile);
		if (!f.exists()) {
			throw(new Exception("SVM result not generated!"));
		}
	
		// 2. read classification output and update database
		FileReader fileR = new FileReader(resultFile);
		BufferedReader reader = new BufferedReader(fileR);
	
		int counter = 0;
		while (counter<pTestExamples.size() && reader.ready()) {
			String line = reader.readLine();
			Double classNum = -1D;
			Double weight = 0D;
			String[] lineParts = line.split(" ");
			if(isBinaryClassification()){
				weight = Double.parseDouble(lineParts[0]);
				classNum = 1D;
				if(weight>0)
					classNum=2D;
			}else{
				classNum = Double.parseDouble(lineParts[0]) - 1;//convert to index (e.g. 1 -> 0)
				if(lineParts.length>classNum+1)
					weight = Double.parseDouble(lineParts[classNum.intValue()]);
			}
			
			pTestExamples.get(counter).setPredictedClass(classNum);
			pTestExamples.get(counter).setPredictionWeight(weight);
			
			MLExample test = pTestExamples.get(counter);
			String savePredictedQuery = "update MLExample set predictedClass ="+test.getPredictedClass()+" , predictionWeight = " +
					weight+" where exampleId="+test.getExampleId();
			HibernateUtil.executeNonReader(savePredictedQuery);
			
			counter++;
		}

		assert !reader.ready() : "Something wrong file remained, updated rows:"+counter;
		assert counter==pTestExamples.size() : "Something wrong resultset remained, updated rows:"+counter;
		
		reader.close();
		
	}
	protected abstract boolean isBinaryClassification();
	protected abstract String getTrainCommand();

	protected abstract String getTestCommand(String resultFile);
}
