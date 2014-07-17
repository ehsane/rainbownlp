package rainbownlp.machinelearning.examplebuilder.link;

import java.util.ArrayList;
import java.util.List;

import rainbownlp.core.Artifact;
import rainbownlp.core.Phrase;
import rainbownlp.core.PhraseLink;
import rainbownlp.machinelearning.IFeatureCalculator;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.FileUtil;
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.ConfigurationUtil;

public class InSentenceExampleBuilder {
	
	public static void main(String[] args) throws Exception
	{
//		InSentenceExampleBuilder.createExamples(true, "InSentenceExperiment1");
//		InSentenceExampleBuilder.createExamples(false, "InSentenceExperiment1");
	}
	
	public List<IFeatureCalculator> featureCalculators = 
			new ArrayList<IFeatureCalculator>();
	
	
	public void createExamples(boolean is_training_mode, String experimentGroup) throws Exception
	{
		List<Artifact> sentences = 
				Artifact.listByType(Artifact.Type.Sentence,is_training_mode);
		
		int counter = 0;
		int example_counter = 0;
		for(Artifact sentence : sentences)
		{
			MLExample.hibernateSession = HibernateUtil.clearSession(MLExample.hibernateSession);
			List<Phrase> phrases = Phrase.getOrderedPhrasesInSentence(sentence);

			if(!possiblyHasLink(sentence, phrases)) continue;
			counter++;
			HibernateUtil.startTransaction();

			for(int i=0; i< phrases.size()-1;i++)
			{
				Phrase p1 = phrases.get(i);
				for(int j=0; j< phrases.size();j++){
					Phrase p2 = phrases.get(j);
					PhraseLink phrase_link = 
							PhraseLink.getInstance(p1, p2);
					int expected_class = phrase_link.getLinkType().ordinal();

					ConfigurationUtil.SaveInGetInstance = false;

					MLExample link_example = 
							MLExample.getInstanceForLink(phrase_link, experimentGroup);
					link_example.setExpectedClass((double) expected_class);
					link_example.setRelatedPhraseLink(phrase_link);

					link_example.setPredictedClass((double) -1);


					if(sentence.getAssociatedFilePath().contains("/train/"))
						link_example.setForTrain(true);
					else
						link_example.setForTrain(false);

					MLExample.saveExample(link_example);

					ConfigurationUtil.SaveInGetInstance = true;

					link_example.calculateFeatures(featureCalculators);
					FileUtil.logLine("debug.log","example processed: "+example_counter);
				}
			}


			HibernateUtil.clearLoaderSession();
			FileUtil.logLine(null,"LinkExampleBuilder--------Sentence processed: "+counter);
			HibernateUtil.endTransaction();
		}
	}
	//<event> in the <event> 
	//<event> when <event>
	
	/**
	 * Abstract implementation here, inherit to inject more logics
	 * @param sentence
	 * @param phrases
	 * @return
	 */
	protected boolean possiblyHasLink(Artifact sentence,
			List<Phrase> phrases)
	{
		boolean hasLink = true;
		if(phrases.size()<2) return false;
		
		return hasLink;
	}

	/**
	 * Abstract implementation here, inherit to inject more logics
	 * @param sentence
	 * @param phrases
	 * @return
	 */
	protected boolean isValidLink(Phrase phrase1, Phrase phrase2)
	{
		boolean hasLink = true;
		if(phrase1.equals(phrase2)) return false;
		
		return hasLink;
	}
	
}
