package rainbownlp.machinelearning;

import java.util.List;

import rainbownlp.machinelearning.convertor.SVMLightFormatConvertor;
import rainbownlp.util.StringUtil;


public abstract class LearnerEngine {
	String modelFile;
	String taskName = "unknown";
	String trainFile;
	String testFile;
	
	public abstract void train(List<MLExample> pTrainExamples) throws Exception ;
	public abstract void test(List<MLExample> pTestExamples) throws Exception ;
	
	protected String getModelFilePath(){
		if(!StringUtil.isEmpty(trainFile))
			modelFile = trainFile.replace("."+SVMLightFormatConvertor.OUTPUT_FILE_EXTENSION, ".model");
		else
			modelFile = getTaskName()+".model";
		return modelFile;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
}
