package rainbownlp.machinelearning;

import java.util.List;

import rainbownlp.core.Artifact;

public interface IMLExampleBuilder {
	public List<MLExample> getExamples(String artifactsCategory);
}
