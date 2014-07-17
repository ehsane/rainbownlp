package rainbownlp.machinelearning.convertor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import rainbownlp.core.FeatureValuePair;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.MLExampleFeature;
import rainbownlp.util.FileUtil;
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.ConfigurationUtil;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;

public class WekaFormatConvertor {
	static int numClassRatio = ConfigurationUtil.getValueInteger("numClassesRatio");
	
	public static void writeToFile(List<Integer> exampleIdsToWrite, String filePath,String taskName
			,String[] possibleClasses) 
		throws Exception
	{
		if(new File(filePath).exists()) return;
		
		
		int counter = 0;
	    // set attributes
	    List<FeatureValuePair> fvps = (List<FeatureValuePair>) 
	    		HibernateUtil.executeReader("from FeatureValuePair where tempFeatureIndex<>"+Integer.MAX_VALUE+
	    				" group by tempFeatureIndex");
	    // 1. set up attributes
	    FastVector atts = new FastVector();
    	for(int i=0;i<fvps.size();i++){
    		FeatureValuePair fvp = fvps.get(i);
	    	if(fvp.getFeatureValueAuxiliary()==null)
	    		atts.addElement(new Attribute(fvp.getFeatureName()));
	    	else
	    		atts.addElement(new Attribute(fvp.getFeatureName()+fvp.getFeatureValue()));
	    }
    	
    	 FastVector classVals = new FastVector();
    	 for (int i = 0; i < possibleClasses.length; i++)
    	      classVals.addElement(possibleClasses[i]);
    	 atts.addElement(new Attribute("class", classVals));
    	
	    Instances data = new Instances(taskName, atts, 0);
	    FileOutputStream file_writer = new FileOutputStream(filePath);
		ArffSaver saver = new ArffSaver();
	    saver.setDestination(file_writer);
	    saver.setRetrieval(Saver.INCREMENTAL);
	    saver.setStructure(data);
	    for(Integer example_id : exampleIdsToWrite) {
	    	counter++;
	    	MLExample example = MLExample.getExampleById(example_id);
	    	if(example.getExpectedClass() == null){
	    		FileUtil.logLine(FileUtil.DEBUG_FILE, "expected class is null!");
	    		continue;
	    	}
	    	Double expectedClass = example.getExpectedClass()+1;
	    	// create instance
	    	double[] vals = new double[fvps.size()+1];
    		List<MLExampleFeature> features = example.getExampleFeatures();
	    	
	    	for(int i=0;i<fvps.size();i++){
	    		FeatureValuePair fvp = fvps.get(i);
	    		vals[i]=0;
	    		for(MLExampleFeature feature:features){
	    			FeatureValuePair featureFVP = feature.getFeatureValuePair();
	    			if(featureFVP.getTempFeatureIndex() != fvp.getTempFeatureIndex()) continue;
	    			
		        	if(fvp.getFeatureValueAuxiliary()==null){//single value
		        		vals[i] = Double.parseDouble(featureFVP.getFeatureValue());
		        	}else
		        		vals[i] = Double.parseDouble(featureFVP.getFeatureValueAuxiliary());
		        	break;
	    		}
	    	}
	    	vals[vals.length-1] = classVals.indexOf(String.valueOf(expectedClass.intValue()));
	    	if(vals[vals.length-1]==-1)
	    		throw(new Exception("Expected class not found in possible class values: "+expectedClass));
	    	SparseInstance instance = new SparseInstance(1.0, vals);
	    	instance.setDataset(data);
//	    	data.add(instance);
	    	saver.writeIncremental(instance);
	    	FileUtil.logLine(null, "example processed: "+counter+"/"+exampleIdsToWrite.size());
	    	if(counter%100==0)
	    		file_writer.flush();
	    }
		file_writer.flush();
		file_writer.close();
	}

}
