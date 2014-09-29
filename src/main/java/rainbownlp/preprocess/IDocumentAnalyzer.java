package rainbownlp.preprocess;

import java.util.List;

import rainbownlp.core.Artifact;

public interface IDocumentAnalyzer {
	static public enum InputType {
		TextFiles
	}
	/**
	 * Process given documents and load them into Artifact table
	 * @param rootPath root of all documents to be processed
	 * @return number of processed documents
	 * @throws Exception
	 */
	public List<Artifact> processDocuments(String rootPath);
}
