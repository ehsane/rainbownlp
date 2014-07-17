package rainbownlp.machinelearning;

import rainbownlp.util.ConfigurationUtil;

public class SVMMultiClass extends SVMLightBasedLearnerEngine {
	String modelFile;
	String taskName;
	String trainFile;
	String testFile;
	
	private SVMMultiClass()
	{
		
	}

	public static LearnerEngine getLearnerEngine(String pTaskName) {
		SVMMultiClass learnerEngine = new SVMMultiClass();
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
				ConfigurationUtil.getValue("SVMMulticlassLearnerPath")
					+ " -t " +ConfigurationUtil.getValue("SVMKernel")
					+ " -c " +ConfigurationUtil.getValue("SVMMultiC")
					+" " + trainFile + " " + getModelFilePath();
		return myShellScript;
	}

	@Override
	protected String getTestCommand(String resultFile) {
		String myShellScript = 
				ConfigurationUtil.getValue("SVMMulticlassClassifierPath") + " "
						+ testFile + " " + modelFile +
						" " + resultFile;
			
		return myShellScript;
	}

}
