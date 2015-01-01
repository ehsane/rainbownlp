package rainbownlp.machinelearning.convertor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import rainbownlp.core.FeatureValuePair;
import rainbownlp.core.TimeStamp;
import rainbownlp.machinelearning.ExpectedClassEnum;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.MLExampleFeature;
import rainbownlp.util.ConfigurationUtil;
import rainbownlp.util.FileUtil;
import rainbownlp.util.HibernateUtil;

public class SVMLightFormatConvertor {
	static int numClassRatio = ConfigurationUtil.getValueInteger("numClassesRatio");
	public final static String OUTPUT_FILE_EXTENSION = "txt";
	public static String writeToFile(List<Integer> exampleIdsToWrite,String taskName) 
			throws IOException
	{
		String filePath = getFilePath(taskName);
//		if(new File(filePath).exists()) return filePath;


		FileWriter file_writer = new FileWriter(filePath);

		int counter = 0;

		for(Integer example_id : exampleIdsToWrite) {
			counter++;
			FileUtil.logLine(null, "example_id: "+example_id);
			MLExample example = MLExample.getExampleById(example_id);
			String SVMLightFormatLine = getArtifactAttributes(example,taskName).trim();
			if(example.getExpectedClass() == null){
				FileUtil.logLine(FileUtil.DEBUG_FILE, "expected class is null!");
				continue;
			}

			Double expectedClass = example.getNumericExpectedClass()+1; //convert to 1-base (e.g. 0 -> 1)
			FileUtil.logLine(null, "expected class: "+expectedClass);

			SVMLightFormatLine = expectedClass + " "
					+ SVMLightFormatLine;

			file_writer.write(SVMLightFormatLine + "\n");
			file_writer.flush();
			FileUtil.logLine(FileUtil.DEBUG_FILE, "SVMLightFormatLine: "+SVMLightFormatLine);
			FileUtil.logLine(null, "example wrote: "+counter+"/"+exampleIdsToWrite.size());

			//			HibernateUtil.clearLoaderSession();
		}

		file_writer.flush();
		file_writer.close();
		return filePath;
	}

	private static String getFilePath(String taskName) {
		String fold = (ConfigurationUtil.crossFoldCurrent>0)?("Fold"+ConfigurationUtil.crossFoldCurrent):"";
		String usedFeatures = "";

		List<FeatureValuePair> features = FeatureValuePair.getAllFeatures();
		for(FeatureValuePair fvp : features){
			if(excludeAttributeIds.contains(fvp.getFeatureName())) 
				continue;
			if(onlyIncludeAttributes.size()>0 && !onlyIncludeAttributes.contains(fvp.getFeatureName()))
				continue;
			usedFeatures += "-" + fvp.getTempFeatureIndex();
		}
		String fileName = ConfigurationUtil.getValue("TempFolder")+
				fold+
				"SVMMultiClass-"+(ConfigurationUtil.TrainingMode?"train":"test")+"-"+
				taskName+usedFeatures+"."+OUTPUT_FILE_EXTENSION;
		return fileName;
	}

	public static String writeToFileBinary(List<Integer> exampleIdsToWrite, String taskName) 
			throws IOException
			{
		String filePath = getFilePath(taskName);

//		if(new File(filePath).exists()) return filePath;

		//			if(Setting.TrainingMode)
		//				FeatureValuePair.resetIndexes();

		FileWriter file_writer = new FileWriter(filePath);

		int[] positiveNegativeCount = new int[]{0,0};
		int counter = 0;
		for(Integer example_id : exampleIdsToWrite) {
			counter++;
			TimeStamp.setStart("Writing example : "+example_id);
			MLExample example = MLExample.getExampleById(example_id);

			TimeStamp.setStart("Getting features for "+example_id);
			String SVMLightFormatLine = getArtifactAttributes(example,taskName).trim();
			TimeStamp.setEnd("Getting features for "+example_id);
			if(example.getExpectedClass() == null){
				FileUtil.logLine(FileUtil.DEBUG_FILE, "expected class is null!");
				continue;
			}
			Double expectedClass = example.getNumericExpectedClass()+1; //convert to 1-base (e.g. 0 -> 1)
			FileUtil.logLine(FileUtil.DEBUG_FILE, "expected class: "+expectedClass);

			if(ConfigurationUtil.TrainingMode &&
					expectedClass == ExpectedClassEnum.BOOLEAN_NO.ordinal() &&
					positiveNegativeCount[0] > 
			(positiveNegativeCount[1]+1)*numClassRatio)
				continue;


			if(expectedClass==ExpectedClassEnum.BOOLEAN_NO.ordinal()){
				expectedClass = -1D;
				positiveNegativeCount[0]++;
			}else{
				expectedClass = 1D;
				positiveNegativeCount[1]++;
			}


			SVMLightFormatLine = expectedClass + " "
					+ SVMLightFormatLine;

			file_writer.write(SVMLightFormatLine + "\n");
			file_writer.flush();
			FileUtil.logLine(FileUtil.DEBUG_FILE, "SVMLightFormatLine: "+SVMLightFormatLine);

			//				HibernateUtil.clearLoaderSession();
			TimeStamp.setEnd("Writing example : "+example_id);
			FileUtil.logLine(null, "example wrote: "+counter+"/"+exampleIdsToWrite.size());
		}

		file_writer.flush();
		file_writer.close();
		return filePath;
	}

	public static List<String> excludeAttributeIds = new ArrayList<String>();
	public static List<String> onlyIncludeAttributes = new ArrayList<String>();
	public static ArrayList<String> usedFeatureNames = new ArrayList<String>();
	private static String getArtifactAttributes(MLExample example, String taskName) {
		FileUtil.logLine(FileUtil.DEBUG_FILE, "getArtifactAttributes(): creating feature string for example: "+example.getExampleId());


		String SVMLightFormatLine = "";
		Session old_session = MLExample.hibernateSession;
		MLExample.hibernateSession = HibernateUtil.sessionFactory.openSession();
		List<MLExampleFeature> features = example.getExampleFeatures();
		for(int i=0;i<features.size();i++) {
			MLExampleFeature feature = features.get(i);
			FeatureValuePair fvp = feature.getFeatureValuePair();

			if(excludeAttributeIds.contains(fvp.getFeatureName())) 
			{
				FileUtil.logLine(FileUtil.DEBUG_FILE, "getArtifactAttributes(): skipping the feature, excludeAttributeIds includes feature: "+fvp.getFeatureName());
				continue;
			}
			if(onlyIncludeAttributes.size()>0 && !onlyIncludeAttributes.contains(fvp.getFeatureName())) {
				FileUtil.logLine(FileUtil.DEBUG_FILE, "getArtifactAttributes(): skipping the feature, onlyIncludeAttributes is not empty and it doesn't include feature: "+fvp.getFeatureName());
				continue;
			}

			int featureIndex = 
					fvp.getTempFeatureIndex();
			if(ConfigurationUtil.TrainingMode && featureIndex==Integer.MAX_VALUE)
			{
				featureIndex = FeatureValuePair.getMaxIndex() + 1;
				if(featureIndex==0) featureIndex++;
				fvp.setTempFeatureIndex(featureIndex);
				HibernateUtil.save(fvp);
			}
			if(featureIndex==Integer.MAX_VALUE || featureIndex==-1){
				FileUtil.logLine(FileUtil.DEBUG_FILE, "getArtifactAttributes(): skipping the feature, feature index is not set for feature: "+fvp.getFeatureName());
				continue;
			}



			Double numericValue = 0.0;
			if(fvp.getFeatureValueAuxiliary()!=null)
				numericValue = Double.parseDouble(fvp.getFeatureValueAuxiliary());
			else
				numericValue = Double.parseDouble(fvp.getFeatureValue());

			if (numericValue!=null && 
					numericValue != 0 &&
					!Double.isNaN(numericValue) && 
					!Double.isInfinite(numericValue)) {
				//				double maxVal = getAttributeMaxValue(attribute_id);
				//				numericValue = numericValue/maxVal;

				//				FileUtil.logLine(FileUtil.DEBUG_FILE, fvp.getFeatureName());
				if (!usedFeatureNames.contains(fvp.getFeatureName()))
				{
					FileUtil.logLine("/tmp/featuresUsed", fvp.getFeatureName());
					usedFeatureNames.add(fvp.getFeatureName());
				}


				SVMLightFormatLine += featureIndex + ":" + numericValue
						+ " ";
			}
			HibernateUtil.clearLoaderSession();

		}
		MLExample.hibernateSession.clear();
		MLExample.hibernateSession.close();
		MLExample.hibernateSession = old_session;
		FileUtil.logLine(FileUtil.DEBUG_FILE, "getArtifactAttributes(): feature string for example: "+example.getExampleId()+" -> "+SVMLightFormatLine);


		return SVMLightFormatLine;
	}

}
