package rainbownlp.analyzer.evaluation;

import java.util.List;

import rainbownlp.machinelearning.LearnerEngine;
import rainbownlp.machinelearning.MLExample;

public interface ICrossfoldValidator {
	public IEvaluationResult crossValidation(List<MLExample> examples, int folds) throws Exception;
	public LearnerEngine getLearnerEngine();
}
