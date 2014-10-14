package rainbownlp.core;

import java.util.List;

import rainbownlp.analyzer.evaluation.ICrossfoldValidator;
import rainbownlp.analyzer.evaluation.IEvaluationResult;
import rainbownlp.analyzer.evaluation.classification.Evaluator;
import rainbownlp.machinelearning.IMLExampleBuilder;
import rainbownlp.machinelearning.LearnerEngine;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.preprocess.DocumentAnalyzer.InputType;
import rainbownlp.preprocess.SimpleDocumentLoader;

/**
 * This includes several sugar methods to make using RNLP easier 
 * @author eemadzadeh
 *
 */
public class RainbowEngine {
	List<Artifact> documentsInPipe = null;
	List<MLExample> trainExamplesInPipe = null;
	List<MLExample> testExamplesInPipe = null;
	public static enum DatasetType{
		TRAIN_SET,
		TEST_SET
	}
	/**
	 * Load the input data
	 * @param inputRootPath
	 * @param inputType
	 * @return
	 */
	public RainbowEngine readInput(String inputRootPath, InputType inputType, DatasetType datasetType){
		switch (inputType) {
			case TextFiles:
				SimpleDocumentLoader loader = new SimpleDocumentLoader();
				loader.setDatasetType(datasetType);
				documentsInPipe = loader.processDocuments(inputRootPath);
				break;
			default:
				break;
		}
		return this;
	}
	/**
	 * Create example on the latest documents loaded by readInput and train the model
	 * @param exampleBuilder
	 * @param learner
	 * @return
	 * @throws Exception
	 */
	public RainbowEngine train(IMLExampleBuilder exampleBuilder, LearnerEngine learner) throws Exception{
		trainExamplesInPipe = exampleBuilder.getExamples(DatasetType.TRAIN_SET.name());
		learner.train(trainExamplesInPipe);
		return this;
	}
	
	/**
	 * Create example on the latest documents loaded by readInput and apply trained model
	 * @param exampleBuilder
	 * @param learner
	 * @return
	 * @throws Exception
	 */
	public IEvaluationResult test(IMLExampleBuilder exampleBuilder, LearnerEngine learner) throws Exception{
		testExamplesInPipe = exampleBuilder.getExamples(DatasetType.TEST_SET.name());
		learner.test(testExamplesInPipe);
		return Evaluator.getEvaluationResult(testExamplesInPipe);
	}
	
	/**
	 * Perform classfold validation on the trainset. trainset must be loaded before calling this method with readInput
	 * @param cfValidator
	 * @param exampleBuilder
	 * @param folds
	 * @return
	 * @throws Exception
	 */
	public IEvaluationResult crossValidate(ICrossfoldValidator cfValidator, IMLExampleBuilder exampleBuilder, int folds) throws Exception{
		if(trainExamplesInPipe == null)
			trainExamplesInPipe = exampleBuilder.getExamples(DatasetType.TRAIN_SET.name());
		return cfValidator.crossValidation(trainExamplesInPipe, folds);
	}
	
	
	
}
