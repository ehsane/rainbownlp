/**
 * 
 */
package rainbownlp.machinelearning.featurecalculator.link;

import java.util.List;

import rainbownlp.core.Artifact;
import rainbownlp.core.FeatureValuePair;
import rainbownlp.core.Phrase;
import rainbownlp.core.PhraseLink;
import rainbownlp.core.FeatureValuePair.FeatureName;
import rainbownlp.core.graph.GraphEdge;
import rainbownlp.machinelearning.IFeatureCalculator;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.MLExampleFeature;
import rainbownlp.parser.DependenciesTreeUtil;

/**
 * @author ehsan
 * 
 */
public class ParseTreeFeatures implements IFeatureCalculator {
	public static void main(String[] args) throws Exception
	{
//		List<MLExample> trainExamples = 
//			MLExample.getAllExamples(LinkExampleBuilder.ExperimentGroupEventEvent, true);
//		trainExamples.addAll(
//				MLExample.getAllExamples(LinkExampleBuilder.ExperimentGroupTimexEvent, true));
//		for ( MLExample example_to_process: trainExamples )
//		{
//			ParseTreeFeatures pt =  new ParseTreeFeatures();
//			
//			pt.calculateFeatures(example_to_process);
//		}
	}
	
		@Override
	public void calculateFeatures(MLExample exampleToProcess) {
			try {
				PhraseLink phraseLink = exampleToProcess.getRelatedPhraseLink();
				Phrase phrase1 = phraseLink.getFromPhrase();
				Phrase phrase2 = phraseLink.getToPhrase();
				
				Artifact fromArtifact = phrase1.getStartArtifact();
				
				Artifact toArtifact = phrase2.getStartArtifact();
				DependenciesTreeUtil depUtil =
						new DependenciesTreeUtil(fromArtifact.getParentArtifact());
				
				
				List<GraphEdge> path =
						depUtil.getParseTreePath(fromArtifact, toArtifact);
				if(path!=null)
				{
					FeatureValuePair parseTreePathSize = FeatureValuePair.getInstance(
							FeatureName.ParseTreePathSize, 
							String.valueOf(path.size()));
					
					MLExampleFeature.setFeatureExample(exampleToProcess, parseTreePathSize);
					for(int i=0;i<path.size();i++)
					{
						GraphEdge edge = path.get(i);
						String token = 
								edge.getTarget().toString().replaceAll("\\d+$", "");
						FeatureValuePair parsePathFeature = FeatureValuePair.getInstance(
								FeatureName.ParseTreePath, 
								token, "1");
						
						MLExampleFeature.setFeatureExample(exampleToProcess, parsePathFeature);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}

	
}
