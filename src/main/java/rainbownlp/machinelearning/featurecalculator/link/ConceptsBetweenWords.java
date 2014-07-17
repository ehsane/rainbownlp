/**
 * 
 */
package rainbownlp.machinelearning.featurecalculator.link;

import java.util.ArrayList;
import java.util.List;

import rainbownlp.core.Artifact;
import rainbownlp.core.FeatureValuePair;
import rainbownlp.core.Phrase;
import rainbownlp.core.PhraseLink;
import rainbownlp.core.FeatureValuePair.FeatureName;
import rainbownlp.machinelearning.IFeatureCalculator;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.MLExampleFeature;

/**
 * @author ehsan
 * 
 */
public class ConceptsBetweenWords implements IFeatureCalculator {
	public static void main(String[] args) throws Exception
	{
//		List<MLExample> trainExamples = 
//		MLExample.getAllExamples(LinkExampleBuilder.ExperimentGroupTimexEvent, true);
//		List<MLExample> trainExamples2 = 
//			MLExample.getAllExamples(LinkExampleBuilder.ExperimentGroupEventEvent, true);
//		List<MLExample> all_train_examples = new ArrayList<MLExample>();
//		all_train_examples.addAll(trainExamples);
//		all_train_examples.addAll(trainExamples2);
//		
//		for ( MLExample example_to_process: all_train_examples )
//		{
//			ConceptsBetweenWords n_grams =  new ConceptsBetweenWords();
//			
//			n_grams.calculateFeatures(example_to_process);
//		}
	}
	
		@Override
	public void calculateFeatures(MLExample exampleToProcess) {
			
			PhraseLink phraseLink = exampleToProcess.getRelatedPhraseLink();
			Phrase phrase1 = phraseLink.getFirstPhrase();
			Phrase phrase2 = phraseLink.getSecondPhrase();
			
			Artifact curArtifact = phrase1.getEndArtifact().getNextArtifact();
			Artifact toArtifact = phrase2.getStartArtifact();
			Integer count_words_between=0;
			
			while(curArtifact!=null && 
					!curArtifact.equals(toArtifact))
			{
				String curContent = curArtifact.getContent();
				FeatureValuePair wordBetweenFeature = FeatureValuePair.getInstance(
						FeatureName.LinkWordBetween, 
						curContent, "1");
				
				MLExampleFeature.setFeatureExample(exampleToProcess, wordBetweenFeature);

				curArtifact = curArtifact.getNextArtifact();
				count_words_between++;
//				if(curArtifact!=null)
//				{
//					FeatureValuePair ngramBetweenFeature = FeatureValuePair.getInstance(
//							FeatureName.Link2GramBetween, 
//							curContent+"_"+curArtifact.getContent(), "1");
//					
//					MLExampleFeature.setFeatureExample(exampleToProcess, ngramBetweenFeature);
//				}
			}
		//get number of werds between
			
			FeatureValuePair count_word_between = FeatureValuePair.getInstance(
					FeatureName.LinkBetweenWordCount, 
					count_words_between.toString());
			MLExampleFeature.setFeatureExample(exampleToProcess, count_word_between);

//			String phrase1_content = phrase1.getPhraseContent().replace(" ", "_");
//			if(phrase1.getEndArtifact().getNextArtifact()!=null)
//			{
//				FeatureValuePair ngramFeature = FeatureValuePair.getInstance(
//						FeatureName.Link2GramFrom, 
//						phrase1_content+"_"+phrase1.getEndArtifact().getNextArtifact().getContent(), "1");
//				
//				MLExampleFeature.setFeatureExample(exampleToProcess, ngramFeature);
//			}
	
//			if(phrase1.getStartArtifact().getPreviousArtifact()!=null)
//			{
//				FeatureValuePair ngramFeature = FeatureValuePair.getInstance(
//						FeatureName.Link2GramFrom, 
//						phrase1.getStartArtifact().getPreviousArtifact().getContent()
//						+"_"+phrase1_content, "1");
//				
//				MLExampleFeature.setFeatureExample(exampleToProcess, ngramFeature);
//			}
				
			
//			String phrase2_content = phrase2.getPhraseContent().replace(" ", "_");
//			if(phrase2.getEndArtifact().getNextArtifact()!=null)
//			{
//				FeatureValuePair ngramFeature = FeatureValuePair.getInstance(
//						FeatureName.Link2GramTo, 
//						phrase2_content+"_"+phrase2.getEndArtifact().getNextArtifact().getContent(), "1");
//				
//				MLExampleFeature.setFeatureExample(exampleToProcess, ngramFeature);
//			}
//	
//			if(phrase2.getStartArtifact().getPreviousArtifact()!=null)
//			{
//				FeatureValuePair ngramFeature = FeatureValuePair.getInstance(
//						FeatureName.Link2GramTo, 
//						phrase2.getStartArtifact().getPreviousArtifact().getContent()
//						+"_"+phrase2_content, "1");
//				
//				MLExampleFeature.setFeatureExample(exampleToProcess, ngramFeature);
//			}
			
	
			
			//number of phrases between
			List<Phrase> phrases = Phrase.getPhrasesBetweenPhrases(phrase1, phrase2, exampleToProcess.getAssociatedFilePath());
			
			FeatureValuePair count_phrase_between = FeatureValuePair.getInstance(
					FeatureName.LinkBetweenPhraseCount, 
					String.valueOf(phrases.size()));
			
			MLExampleFeature.setFeatureExample(exampleToProcess, count_phrase_between);
	}

	
}
