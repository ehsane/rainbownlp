package rainbownlp.analyzer.evaluation.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rainbownlp.analyzer.evaluation.ICrossfoldValidator;
import rainbownlp.machinelearning.LearnerEngine;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.FileUtil;
import rainbownlp.util.ConfigurationUtil;


public class CrossValidation implements ICrossfoldValidator {
	
	LearnerEngine mlModel;
	public CrossValidation(LearnerEngine learningEngine)
	{
		mlModel = learningEngine;
	}
	public EvaluationResult crossValidation(List<MLExample> examples, int folds) throws Exception
	{

		int foldCount = examples.size()/folds;
		ArrayList<EvaluationResult> results = 
			new ArrayList<EvaluationResult>();
		for(int foldIndex = 0;foldIndex<folds;foldIndex++)
		{
			ConfigurationUtil.crossFoldCurrent = foldIndex+1;
			int start_index = foldIndex*foldCount;
			int end_index = (foldIndex+1)*foldCount;
			if(end_index>=examples.size()) 
				end_index = examples.size();
			
//			HibernateUtil.startTransaction();
			
			List<MLExample> train_set = new ArrayList<MLExample>();
			for(int i=0;i<start_index;i++)
				train_set.add(examples.get(i).clone());
			for(int i=end_index;i<examples.size();i++)
				train_set.add(examples.get(i).clone());
				
			
			mlModel.train(train_set);
			train_set = null;
			System.gc();
			
			List<MLExample> test_set = new ArrayList<MLExample>();
			for(int i=start_index;i<end_index;i++)
				test_set.add(examples.get(i).clone());
			
			mlModel.test(test_set);
			
//			HibernateUtil.endTransaction();
			results.add(Evaluator.getEvaluationResult(test_set));
		}
		

		HashMap<String, ResultRow> 
			evaluationAverageResult = 
			new HashMap<String, 
				ResultRow>();
		for(EvaluationResult fold_result : results)
		{
			for(String evaluated_class: fold_result.evaluationResultByClass.keySet())
			{
				ResultRow row =
					fold_result.evaluationResultByClass.get(evaluated_class);
				ResultRow averageRow =
					evaluationAverageResult.get(evaluated_class);
				if(averageRow==null)
					evaluationAverageResult.put(evaluated_class,row);
				else{
					averageRow.FN += row.FN;
					averageRow.FP += row.FP;
					averageRow.TN += row.TN;
					averageRow.TP += row.TP;
				}
			}
		}
		for(String evaluated_class: evaluationAverageResult.keySet())
		{
			ResultRow averageRow =
				evaluationAverageResult.get(evaluated_class);
			FileUtil.logLine("Class: "+evaluated_class);
			averageRow.print();
		}
		EvaluationResult er = new EvaluationResult();
		er.evaluationResultByClass = evaluationAverageResult;
		
		ConfigurationUtil.crossFoldCurrent = 0;
		
		return er;
	}
	@Override
	public LearnerEngine getLearnerEngine() {
		return mlModel;
	}
}
