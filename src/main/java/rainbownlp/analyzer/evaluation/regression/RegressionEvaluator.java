package rainbownlp.analyzer.evaluation.regression;

import java.util.ArrayList;
import java.util.List;

import rainbownlp.analyzer.evaluation.classification.EvaluationResult;
import rainbownlp.analyzer.evaluation.classification.ResultRow;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.SystemUtil;


public class RegressionEvaluator {
	public static boolean saveResult = false;
	public static String evaluation_mode = "HybridTest";


	
	public static RegressionEvaluationResult getEvaluationResult(List<MLExample> pExamplesToTest) 
	{
		RegressionEvaluationResult er = new RegressionEvaluationResult();
		
		for(MLExample example : pExamplesToTest)
		{
			Double expected = example.getNumericExpectedClass();
			Double predicted = example.getNumericPredictedClass();
			er.add(expected, predicted);
		}
		
		
		return er;
	}
}
