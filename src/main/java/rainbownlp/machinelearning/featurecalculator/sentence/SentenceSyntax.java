package rainbownlp.machinelearning.featurecalculator.sentence;

import rainbownlp.core.Artifact;
import rainbownlp.core.FeatureValuePair;
import rainbownlp.core.Artifact.Type;
import rainbownlp.machinelearning.IFeatureCalculator;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.MLExampleFeature;

public class SentenceSyntax implements IFeatureCalculator {

	@Override
	public void calculateFeatures(MLExample exampleToProcess) {
		Artifact sentence = exampleToProcess.getRelatedPhrase().getStartArtifact();
		if(sentence.getArtifactType() !=  Type.Sentence)
			return;
		FeatureValuePair wordCountFeature = FeatureValuePair.getInstance("WordCount", 
				((Integer)sentence.getContent().split(" ").length).toString());
		
		MLExampleFeature.setFeatureExample(exampleToProcess, wordCountFeature);
		
		
		FeatureValuePair lineIndexFeature = FeatureValuePair.getInstance("LineIndex", 
				(sentence.getLineIndex()).toString());
		
		MLExampleFeature.setFeatureExample(exampleToProcess, lineIndexFeature);
	}

}
