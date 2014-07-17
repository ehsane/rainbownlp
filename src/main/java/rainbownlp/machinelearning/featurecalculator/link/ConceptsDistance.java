/**
 * 
 */
package rainbownlp.machinelearning.featurecalculator.link;

import java.util.List;
import rainbownlp.machinelearning.IFeatureCalculator;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.FileUtil;

/**
 * @author ehsan
 * 
 */
public class ConceptsDistance implements IFeatureCalculator {
	public static void main (String[] args) throws Exception
	{
		String experimentgroup = "LinkClassificationEventEvent";
		List<MLExample> trainExamples = 
			MLExample.getAllExamples(experimentgroup, true);
		int counter = 0;
		for (MLExample example:trainExamples)
		{
			ConceptsDistance lbf = new ConceptsDistance();
			lbf.calculateFeatures(example);
			counter++;
			FileUtil.logLine(null, "Processed : "+counter +"/"+trainExamples.size());
		}
		
	}
	@Override
	public void calculateFeatures(MLExample exampleToProcess) {
		
	
	}
	
}
