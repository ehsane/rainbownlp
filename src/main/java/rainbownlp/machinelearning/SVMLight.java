package rainbownlp.machinelearning;

import java.util.List;

import rainbownlp.util.ConfigurationUtil;

public class SVMLight extends SVMLightBasedLearnerEngine {
	private SVMLight()
	{
		
	}

	@Override
	public void test(List<MLExample> pTestExamples) throws Exception {

		
	}

	public static LearnerEngine getLearnerEngine(String pTaskName) {
		SVMLight learnerEngine = new SVMLight();
		learnerEngine.setTaskName(pTaskName);
		
		return learnerEngine;
	}

	@Override
	protected String getTrainCommand() {
		String trainCommand = ConfigurationUtil.getValue("SVMLightLearnerPath")
				+ " -j " +ConfigurationUtil.getValue("SVMCostParameter")
				+ " -t " +ConfigurationUtil.getValue("SVMKernel")
				+ " -c " +ConfigurationUtil.getValue("SVMLightC")
				+" " + trainFile + " " + getModelFilePath();
		return trainCommand;
	}

	
	@Override
	protected String getTestCommand(String resultFile) {
	
		String myShellScript = 
			ConfigurationUtil.getValue("SVMLightClassifierPath") + " "
					+ testFile + " " + modelFile +
					" " + resultFile;
		return myShellScript;
	}
	@Override
	protected boolean isBinaryClassification() {
		return true;
	}

}
