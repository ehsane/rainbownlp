package rainbownlp.analyzer.evaluation.classification;

import java.util.ArrayList;
import java.util.List;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.SystemUtil;


public class Evaluator {
	public static boolean saveResult = false;
	public static String evaluation_mode = "HybridTest";

	public static ResultRow evaluateByClass(List<MLExample> pExamplesToTest, 
			String exampleClassToEvaluate)
	{
		ResultRow rr = new ResultRow();
		
		for(MLExample example : pExamplesToTest)
		{
			String expected_class = example.getExpectedClass();
			String predicted_class = example.getPredictedClass();
			if(exampleClassToEvaluate.equals(expected_class))
			{
				if(expected_class.equals(predicted_class))
					rr.TP++;
				else
					rr.FN++;
			}
			else
			{
				if(exampleClassToEvaluate.equals(predicted_class))
					rr.FP++;
				else
					rr.TN++;
			}
		}
		
		return rr;
	}

	public static void evaluateDevelopementResult(String resultsRoot)
	{
		SystemUtil.runShellCommand("a2-evaluate.pl -g gold-devel "+resultsRoot+"/*.a2");
	}
	public static EvaluationResult getEvaluationResult(List<MLExample> pExamplesToTest, 
			String[] class_titles) {
		EvaluationResult result = new EvaluationResult();
		
		for(Integer i=1;i<=class_titles.length;i++)
		{
			result.evaluationResultByClass.put(class_titles[i-1], 
					evaluateByClass(pExamplesToTest, i.toString()));
		}
		return result;
	}
	
	public static EvaluationResult getEvaluationResult(List<MLExample> pExamplesToTest) 
	{
		ArrayList<String> exampleClasses = new ArrayList<String>();
	
		for(MLExample example : pExamplesToTest)
		{
			if(!exampleClasses.contains(example.getExpectedClass()))
				exampleClasses.add(example.getExpectedClass());
//			if(example.getPredictedClass()!=null && !exampleClasses.contains(example.getPredictedClass()))
//				exampleClasses.add(example.getPredictedClass());
		}
		
		EvaluationResult er = new EvaluationResult();
		for(String exampleClass : exampleClasses)
		{
			er.evaluationResultByClass.put(exampleClass.toString(), 
					evaluateByClass(pExamplesToTest, exampleClass));
		}
		return er;
	}
	
	public static ResultRow getNoClassEvaluationResult(List<MLExample> pExamplesToTest) 
	{
		ResultRow rr = new ResultRow();
		
		for(MLExample example : pExamplesToTest)
		{
			String expected_class = example.getExpectedClass();
			String predicted_class = example.getPredictedClass();
			if(expected_class.equals(predicted_class))
				rr.TP++;
			else
				rr.FN++;
			
		}
		
		return rr;
	}
}
