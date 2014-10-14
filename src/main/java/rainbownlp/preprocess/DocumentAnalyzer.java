package rainbownlp.preprocess;

import java.util.List;

import rainbownlp.core.Artifact;
import rainbownlp.core.RainbowEngine.DatasetType;

public abstract class DocumentAnalyzer {
	static public enum InputType {
		TextFiles
	}
	/**
	 * Process given documents and load them into Artifact table
	 * @param rootPath root of all documents to be processed
	 * @return number of processed documents
	 * @throws Exception
	 */
	public abstract List<Artifact> processDocuments(String rootPath);
	
	DatasetType dsType;
	/**
	 * Optional: You can specify the group name the loaded artifacts belong to. e.g. Test or Train  set
	 * If not specified train set will be used
	 * @param dsType
	 */
	public void setDatasetType(DatasetType pDsType){
		dsType = pDsType;
	}
}
