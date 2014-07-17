package rainbownlp.machinelearning.featurecalculator.phrase;

import java.util.List;

import rainbownlp.core.Artifact;
import rainbownlp.core.Artifact.Type;
import rainbownlp.core.FeatureValuePair;
import rainbownlp.core.Phrase;
import rainbownlp.core.PhraseLink;
import rainbownlp.machinelearning.IFeatureCalculator;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.MLExampleFeature;
import rainbownlp.machinelearning.featurecalculator.FeatureCalculatorUtil;
import rainbownlp.util.StringUtil;

public class PhraseNGram implements IFeatureCalculator {

	@Override
	public void calculateFeatures(MLExample exampleToProcess) {
		Phrase p = exampleToProcess.getRelatedPhrase();
		if(p == null) {
			return;
		}
		
		
		calculateInPhraseNGram(1, p, exampleToProcess, "InPhrase1Gram");
		calculateInPhraseNGram(2, p, exampleToProcess, "InPhrase2Gram");
		
		List<String> beforePhraseNGrams = FeatureCalculatorUtil.getNGramBefore(p.getStartArtifact(), 3);
		List<String> afterPhraseNGrams = FeatureCalculatorUtil.getNGramAfter(p.getEndArtifact(), 3);
		
		for(String beforeNGram : beforePhraseNGrams)
		{
			FeatureValuePair value_pair = FeatureValuePair.getInstance(
					"BeforePhraseNGram", beforeNGram, "1");
			MLExampleFeature.setFeatureExample(exampleToProcess,value_pair);
		}
		for(String afterNGram : afterPhraseNGrams)
		{
			FeatureValuePair value_pair = FeatureValuePair.getInstance(
					"AfterPhraseNGram", afterNGram, "1");
			MLExampleFeature.setFeatureExample(exampleToProcess,value_pair);
		}
	}
	
	void calculateInPhraseNGram(int n, Phrase phrase, MLExample example, String featureName)
	{
		String[] word_text = 
				StringUtil.getTermByTermWordnet(phrase.getPhraseContent().toLowerCase()).split(" ");
			
		for(int i=0;i<word_text.length-n;i++)
		{
			String cur_content = "";
			for(int j=0;j<n;j++)
			{
				int new_part_index = i+j;
				if(!word_text[new_part_index].trim().equals(""))
				{
					cur_content = 
						cur_content.concat("_"+word_text[new_part_index].trim());
				}
			}
			cur_content = cur_content.replaceAll("^_", "");
			FeatureValuePair value_pair = FeatureValuePair.getInstance(
					featureName, cur_content, "1");
			MLExampleFeature.setFeatureExample(example,value_pair);
		}
	}

}
