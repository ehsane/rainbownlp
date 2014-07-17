/**
 * 
 */
package rainbownlp.machinelearning.featurecalculator.link;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rainbownlp.analyzer.sentenceclause.Clause;
import rainbownlp.analyzer.sentenceclause.SentenceClauseManager;
import rainbownlp.core.Artifact;
import rainbownlp.core.FeatureValuePair;
import rainbownlp.core.Phrase;
import rainbownlp.core.PhraseLink;
import rainbownlp.core.FeatureValuePair.FeatureName;
import rainbownlp.machinelearning.IFeatureCalculator;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.MLExampleFeature;
import rainbownlp.parser.DependencyLine;
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.StanfordDependencyUtil;
import rainbownlp.util.StringUtil;

/**
 * @author Azadeh
 * 
 */
public class ParseDependencyFeatures implements IFeatureCalculator {
	
	public static void main (String[] args) throws Exception
	{
////		List<MLExample> trainExamples = 
////			MLExample.getAllExamples(LinkExampleBuilder.ExperimentGroupTimexEvent, true);
//		List<MLExample> trainExamples2 = 
//			MLExample.getAllExamples(LinkExampleBuilder.ExperimentGroupEventEvent, true,100);
//		List<MLExample> all_train_examples = new ArrayList<MLExample>();
////		all_train_examples.addAll(trainExamples);
//		all_train_examples.addAll(trainExamples2);
//		for (MLExample example:all_train_examples)
//		{
////			if(example.getExampleId() != 4893) continue;
//			ParseDependencyFeatures lbf = new ParseDependencyFeatures();
//			lbf.calculateFeatures(example);
//		}
		
	}
	@Override
	public void calculateFeatures(MLExample exampleToProcess) throws Exception {
			
			PhraseLink phraseLink = exampleToProcess.getRelatedPhraseLink();
			Phrase phrase1 = Phrase.getInstance(phraseLink.getFromPhrase().getPhraseId());
			Phrase phrase2 = Phrase.getInstance(phraseLink.getToPhrase().getPhraseId());
			Artifact parent_sent = phrase1.getStartArtifact().getParentArtifact();
			SentenceClauseManager clauseManager =
					new SentenceClauseManager(parent_sent);
	
			ArrayList<DependencyLine> dep_lines = clauseManager.sentDepLines;
			
			String rel_prep1 = getRelPrepositionToPhrase(phrase1,dep_lines);
			if (rel_prep1==null)
			{
				rel_prep1="no-preposition";
			}
			
			FeatureValuePair fromPhraserelPrep = FeatureValuePair.getInstance
			(FeatureName.FromPhraseRelPrep, rel_prep1,"1");
		
			MLExampleFeature.setFeatureExample(exampleToProcess, fromPhraserelPrep);
			
			
			//To phrase related prep
			String rel_prep2 = getRelPrepositionToPhrase(phrase2,dep_lines);
			if (rel_prep2==null)
			{
				rel_prep2="no-preposition";
			}
			
			FeatureValuePair toPhraserelPrep = FeatureValuePair.getInstance
			(FeatureName.ToPhraseRelPrep, rel_prep2,"1");
		
		    MLExampleFeature.setFeatureExample(exampleToProcess, toPhraserelPrep);
			
			////////////////////////////////////////////////////////////////
			boolean are_gov_connected = areGovVerbsDirectlyConnected(phrase1, phrase2, clauseManager);
			FeatureValuePair gov_connected_feature = FeatureValuePair.getInstance
			(FeatureName.areGovVerbsConnected, are_gov_connected?"1":"0");
		
		    MLExampleFeature.setFeatureExample(exampleToProcess, gov_connected_feature);
		   ///// ///////////////////////////////////////////////////////////
		    String gov_verb1 = getGovernorVerb(phrase1,clauseManager);
		    String gov_verb2 = getGovernorVerb(phrase2,clauseManager);
		    if (gov_verb1 ==null)
		    {
		    	gov_verb1 ="#notFound#";
		    }
		    if (gov_verb2 ==null)
		    {
		    	gov_verb2 ="#notFound#";
		    }
		   
	    	FeatureValuePair fromGovVerb = FeatureValuePair.getInstance
			(FeatureName.FromPhraseGovVerb, gov_verb1, "1");
		
		    MLExampleFeature.setFeatureExample(exampleToProcess, fromGovVerb);
	    
	 
	    	FeatureValuePair toGovVerb = FeatureValuePair.getInstance
			(FeatureName.ToPhraseGovVerb, gov_verb2, "1");
		
		    MLExampleFeature.setFeatureExample(exampleToProcess, toGovVerb);
		    
		    
		    //get verb ausxilaries
		    List<String> from_verb_auxs = getVerbAuxilaries(phrase1, clauseManager);
		    for (String verb_aux: from_verb_auxs)
		    {
		    	FeatureValuePair fromverbAuxFeatures = FeatureValuePair.getInstance
				(FeatureName.FromPhraseGovVerbAux, verb_aux,"1");
			
			    MLExampleFeature.setFeatureExample(exampleToProcess, fromverbAuxFeatures);
		    }
		    
		    List<String> to_verb_auxs = getVerbAuxilaries(phrase2, clauseManager);
		    for (String verb_aux: to_verb_auxs)
		    {
		    	FeatureValuePair toverbAuxFeatures = FeatureValuePair.getInstance
				(FeatureName.toPhraseGovVerbAux, verb_aux,"1");
			
			    MLExampleFeature.setFeatureExample(exampleToProcess, toverbAuxFeatures);
		    }
				
	}
	
	public static String getRelPrepositionToPhrase(Phrase pPhrase, ArrayList<DependencyLine> dep_lines) throws SQLException
	{
		String related_prep = null;
//		List<Integer> included_offsets = pPhrase.listWordOffsetsInPhrase();
		String sent_dep_string;
		if (dep_lines ==null)
		{
			dep_lines =StanfordDependencyUtil.parseDepLinesFromString
				(pPhrase.getStartArtifact().getParentArtifact().getStanDependency());
		}
		int head_offset = pPhrase.getHeadArtifact().getWordIndex();
		// looking for pre_ relations that have any of the words in the phrase as the gov or 
		for (DependencyLine dep: dep_lines)
		{
			String rel_name = dep.relationName.toLowerCase();
			if (rel_name.startsWith("prep_"))
			{
				if (head_offset == dep.secondOffset-1 ||
						head_offset == dep.firstOffset-1)
				{
					if (rel_name.matches("prep_.*"))
					{
						related_prep = rel_name.replaceAll("prep_(.*)", "$1");
						break;
					}
					
				}
			}
		}
		// check the original line also
		if (related_prep==null)
		{
			Artifact prev_artifact = pPhrase.getStartArtifact().getPreviousArtifact();
			if (prev_artifact != null && prev_artifact.getPOS().toLowerCase().equals("dt"))
			{
				related_prep = prev_artifact.getContent();
			}
			if (prev_artifact != null && prev_artifact.getPOS().toLowerCase().equals("in"))
			{
				related_prep = prev_artifact.getContent();
			}
		}
		return related_prep;
	}
	public static String getGovernorVerb(Phrase pPhrase, SentenceClauseManager pClauseManager ) throws Exception
	{
		String gov_verb = null;
		if (pPhrase.getGovVerb()!=null)
		{
			return StringUtil.getWordLemma(pPhrase.getGovVerb().getContent());
		}
		else
		{
			// get sentence clauses
			Artifact head = pPhrase.getHeadArtifact();
			SentenceClauseManager clauseManager;
			if (pClauseManager.getRelatedSentence() != pPhrase.getStartArtifact().getParentArtifact())
			{
				clauseManager = new SentenceClauseManager(pPhrase.getStartArtifact().getParentArtifact());
			}
			else
			{
				clauseManager = pClauseManager;
			}
			Clause related_clause = clauseManager.clauseMap.get(head.getWordIndex()+1);
			
			if (related_clause!=null)
			{
				gov_verb = related_clause.clauseVerb.verbMainPart;
	
				if (!gov_verb.matches(""))
				{
					Artifact gov_verb_artifact = 
						Artifact.findInstance(clauseManager.getRelatedSentence(), 
								related_clause.clauseVerb.offset-1);
					if (!gov_verb_artifact.getPOS().startsWith("VB"))
					{
						gov_verb = null;
					}
					else
					{
						pPhrase.setGovVerb(gov_verb_artifact);
						HibernateUtil.save(pPhrase);
					}
				}
				
				
			}
			if (gov_verb ==null || gov_verb.equals("") )
			{
				Artifact ga= pPhrase.calclateGovVerb();
				if (ga != null)
				{
					gov_verb= ga.getContent();
					pPhrase.setGovVerb(ga);
					HibernateUtil.save(pPhrase);
				}
			}
		}
		if (gov_verb != null)
		{
			gov_verb = StringUtil.getWordLemma(gov_verb);
		}
		
		return gov_verb;
	}
	public static String getGovernorVerbPOS(Phrase pPhrase, SentenceClauseManager pClauseManager ) throws Exception
	{
		String gov_verb_pos = null;
		Artifact gov_verb = pPhrase.getGovVerb();
		if (gov_verb != null)
		{
			return gov_verb.getPOS();
		}
		// get sentence clauses
		Artifact head = pPhrase.getHeadArtifact();
		SentenceClauseManager clauseManager;
		if (pClauseManager ==null || pClauseManager.getRelatedSentence() != pPhrase.getStartArtifact().getParentArtifact())
		{
			clauseManager = new SentenceClauseManager(pPhrase.getStartArtifact().getParentArtifact());
		}
		else
		{
			clauseManager = pClauseManager;
		}
		Clause related_clause = clauseManager.clauseMap.get(head.getWordIndex()+1);
		
		if (related_clause!=null)
		{
			
			gov_verb_pos = clauseManager.getPOSTag(related_clause.clauseVerb.offset);
		}
//		TODO: perform the right action
//		if (gov_verb==null)
//		{
//			 throw (new Exception());
//		}
		

		return gov_verb_pos;
	}
	public static Integer getGovernorVerbTense(Phrase pPhrase, SentenceClauseManager pClauseManager ) throws Exception
	{
		Integer gov_verb_tense = null;
		Artifact head;

		Artifact gov_verb = pPhrase.getGovVerb();
		if (gov_verb != null)
		{
			head = gov_verb;
		}
		else
		{
			head = pPhrase.getHeadArtifact();
		}
		// get sentence clauses
		
		SentenceClauseManager clauseManager;
		if (pClauseManager ==null || pClauseManager.getRelatedSentence() != 
				pPhrase.getStartArtifact().getParentArtifact())
		{
			clauseManager = new SentenceClauseManager(pPhrase.getStartArtifact().getParentArtifact());
		}
		else
		{
			clauseManager = pClauseManager;
		}
		Clause related_clause = clauseManager.clauseMap.get(head.getWordIndex()+1);
		
		if (related_clause!=null)
		{
			gov_verb_tense = related_clause.clauseVerb.getTense(clauseManager);
			
		}
		

		return gov_verb_tense;
	}
	public static List<String> getVerbAuxilaries(Phrase pPhrase, SentenceClauseManager pClauseManager ) throws Exception
	{
		List<String> aux =  new ArrayList<String>();
		
		Clause related_clause = getPhraseRelatedClause(pPhrase,pClauseManager);
		
		if (related_clause!=null)
		{
			aux =related_clause.clauseVerb.auxs;
		}
		return aux;
	}
	public static Clause getPhraseRelatedClause(Phrase pPhrase, SentenceClauseManager pClauseManager) throws Exception
	{
		// get sentence clauses
		Artifact head;
		Artifact gov_verb = pPhrase.getGovVerb();
		if (gov_verb != null)
		{
			head = gov_verb;
		}
		else
		{
			head = pPhrase.getHeadArtifact();
		}
		
		SentenceClauseManager clauseManager;
		
		if (pClauseManager.getRelatedSentence() != pPhrase.getStartArtifact().getParentArtifact())
		{
			clauseManager = new SentenceClauseManager(pPhrase.getStartArtifact().getParentArtifact());
		}
		else
		{
			clauseManager = pClauseManager;
		}
		Clause related_clause = clauseManager.clauseMap.get(head.getWordIndex()+1);
		
		return related_clause;
	}
	public static boolean areGovVerbsDirectlyConnected(Phrase pPhrase1, Phrase pPhrase2,SentenceClauseManager pClauseManager) throws Exception
	{
		boolean are_connected = false;
		Artifact gov_verb1_artifact = pPhrase1.getGovVerb();
		Artifact gov_verb2_artifact = pPhrase2.getGovVerb();
		String p1_gov_verp=null;
		Integer p1_gov_verb_offset=null;
		String p2_gov_verp = null;
		Integer p2_gov_verb_offset=null;
		if (gov_verb1_artifact != null)
		{
			p1_gov_verp = gov_verb1_artifact.getContent();
			p1_gov_verb_offset = gov_verb1_artifact.getWordIndex()+1;
		}
		if (gov_verb2_artifact != null)
		{
			p2_gov_verp = gov_verb2_artifact.getContent();
			p2_gov_verb_offset = gov_verb2_artifact.getWordIndex()+1;
		}
		SentenceClauseManager clauseManager;
		Artifact head2 = pPhrase2.getHeadArtifact();
		if (pClauseManager.getRelatedSentence() != pPhrase1.getStartArtifact().getParentArtifact())
		{
			clauseManager = new SentenceClauseManager(pPhrase1.getStartArtifact().getParentArtifact());
		}
		else
		{
			clauseManager = pClauseManager;
		}
		if (p1_gov_verp ==null)
		{
			Artifact head1 = pPhrase1.getHeadArtifact();
			
			Clause related_clause1 = clauseManager.clauseMap.get(head1.getWordIndex()+1);
			
			if (related_clause1!=null)
			{
				
				p1_gov_verp = related_clause1.clauseVerb.verbMainPart;
				p1_gov_verb_offset = related_clause1.clauseVerb.offset;
			}
		}
		if (p1_gov_verp ==null)
		{
			
			Clause related_clause2 = clauseManager.clauseMap.get(head2.getWordIndex()+1);
		
			
			if (related_clause2!=null)
			{
				p2_gov_verp = related_clause2.clauseVerb.verbMainPart;
				p2_gov_verb_offset = related_clause2.clauseVerb.offset;
			}
		}
		
		if (p2_gov_verp != null && p1_gov_verp!=null && p1_gov_verp.equals(p2_gov_verp) && p1_gov_verb_offset==p2_gov_verb_offset)
		{
			are_connected = true;
			return are_connected;
		}
		
		if (p2_gov_verp != null && p1_gov_verp!=null )
		{
			are_connected = StanfordDependencyUtil.haveDirectRelation(p1_gov_verp,p1_gov_verb_offset,
					p2_gov_verp,p2_gov_verb_offset,clauseManager.sentDepLines);
		}
		
		return are_connected;
	}
	
	
}
