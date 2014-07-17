package rainbownlp.machinelearning;

import rainbownlp.util.ConfigurationUtil;

public class SVMLightRegression extends SVMLightBasedLearnerEngine {
	private SVMLightRegression()
	{
		
	}

	public static LearnerEngine getLearnerEngine(String pTaskName) {
		SVMLightRegression learnerEngine = new SVMLightRegression();
		learnerEngine.setTaskName(pTaskName);
		
		return learnerEngine;
	}

	@Override
	protected boolean isBinaryClassification() {
		return false;
	}

	@Override
	protected String getTrainCommand() {
		String myShellScript = 
				ConfigurationUtil.getValue("SVMLightLearnerPath")
					+ " -z r -t " +ConfigurationUtil.getValue("SVMKernel")
//					+ " -c " +ConfigurationUtil.getValue("SVMMultiC")
					+" " + trainFile + " " + getModelFilePath();
		return myShellScript;
	}

	@Override
	protected String getTestCommand(String resultFile) {
		String myShellScript = 
				ConfigurationUtil.getValue("SVMLightClassifierPath") + " "
						+ testFile + " " + modelFile +
						" " + resultFile;

		return myShellScript;
	}



}
