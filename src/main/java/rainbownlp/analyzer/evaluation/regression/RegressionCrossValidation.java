package rainbownlp.analyzer.evaluation.regression;

import java.util.ArrayList;
import java.util.List;

import rainbownlp.analyzer.evaluation.ICrossfoldValidator;
import rainbownlp.machinelearning.LearnerEngine;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.ConfigurationUtil;


public class RegressionCrossValidation implements ICrossfoldValidator {
	
	LearnerEngine mlModel;
	public RegressionCrossValidation(LearnerEngine learningEngine)
	{
		mlModel = learningEngine;
	}
	public RegressionEvaluationResult crossValidation(List<MLExample> examples, int folds) throws Exception
	{

		int foldCount = examples.size()/folds;
		ArrayList<RegressionEvaluationResult> results = 
			new ArrayList<RegressionEvaluationResult>();
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
			results.add(RegressionEvaluator.getEvaluationResult(test_set));
		}
		

		
		RegressionEvaluationResult averageRes = new RegressionEvaluationResult();
		for(RegressionEvaluationResult fold_result : results)
		{
			averageRes.add(fold_result);
		}
		
		ConfigurationUtil.crossFoldCurrent = 0;
		
		return averageRes;
	}
	@Override
	public LearnerEngine getLearnerEngine() {
		return mlModel;
	}
}
