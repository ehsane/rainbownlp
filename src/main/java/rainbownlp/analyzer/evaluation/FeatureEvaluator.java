package rainbownlp.analyzer.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rainbownlp.core.FeatureValuePair;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.convertor.SVMLightFormatConvertor;

public class FeatureEvaluator {
	public void evaluateFeatures(ICrossfoldValidator cfValidator, List<MLExample> examples) throws Exception{
		SVMLightFormatConvertor.onlyIncludeAttributes = new ArrayList<String>();
		List<String> features = FeatureValuePair.getAllFeatureNames();
		String featuresIncluded = "";
		HashMap<String, IEvaluationResult> attributeResult = new HashMap<String, IEvaluationResult>();
		for(String feature : features){
			SVMLightFormatConvertor.onlyIncludeAttributes.add(feature);
			featuresIncluded+="/"+feature;
			attributeResult.put(featuresIncluded, cfValidator.crossValidation(examples, 2));
		}
		
		for(String attributesIncluded : attributeResult.keySet()){
			System.out.println("Result for these included features: "+attributesIncluded);
			attributeResult.get(attributesIncluded).printResult();
			System.out.println("----------------------------");
		}
		
		System.out.println("*************Integrated metric*************");
		for(String attributesIncluded : attributeResult.keySet()){
			System.out.println(attributesIncluded+"\t"+attributeResult.get(attributesIncluded).getIntegratedMetric());
		}
		System.out.println("*******************************************");
		
	}
}
