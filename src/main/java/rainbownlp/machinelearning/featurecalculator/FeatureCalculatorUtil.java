package rainbownlp.machinelearning.featurecalculator;

import java.util.ArrayList;
import java.util.List;

import rainbownlp.core.Artifact;

public class FeatureCalculatorUtil {

	public static List<String> getNGramBefore(Artifact startArtifact, int n) {
		ArrayList<String> ngrams = new ArrayList<String>();
		int stepRemained = n;
		Artifact curArtifact = startArtifact.getPreviousArtifact();
		String curInclusiveString = startArtifact.getContent();
		while(stepRemained > 0 && curArtifact != null)
		{
			String curContent = curArtifact.getContent().trim();
			if(curContent.equals(""))
				continue;
			
			curInclusiveString = curContent + "_" + curInclusiveString;
			String jumpString = curContent+"_"+startArtifact.getContent();
			curArtifact = curArtifact.getPreviousArtifact();
			stepRemained--;
			
			ngrams.add(curInclusiveString);
			ngrams.add(jumpString);
		}
		
		return ngrams;
	}

	public static List<String> getNGramAfter(Artifact endArtifact, int n) {
		ArrayList<String> ngrams = new ArrayList<String>();
		int stepRemained = n;
		Artifact curArtifact = endArtifact.getNextArtifact();
		String curInclusiveString = endArtifact.getContent();
		while(stepRemained > 0 && curArtifact != null)
		{
			String curContent = curArtifact.getContent().trim();
			if(curContent.equals(""))
				continue;
			
			curInclusiveString += "_" + curContent;
			String jumpString = endArtifact.getContent() + "_" + curContent;
			curArtifact = curArtifact.getNextArtifact();
			stepRemained--;
			
			ngrams.add(curInclusiveString);
			ngrams.add(jumpString);
		}
		
		return ngrams;
	}

}
