package rainbownlp.preprocess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rainbownlp.core.Artifact;
import rainbownlp.util.FileUtil;
import rainbownlp.util.HibernateUtil;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;

public class SimpleDocumentLoader implements IDocumentAnalyzer {
	protected List<Artifact> documents;
	protected String documentExtension = "txt";
	public List<Artifact> getDocuments() {
		return documents;
	}
	/**
	 * Simply call loadSentences, override this method for more complex loaders
	 * @param doc
	 * @throws IOException
	 * @throws Exception 
	 */
	protected void processDocument(Artifact doc) throws Exception{
		loadSentences(doc);
	}

	/**
	 * Use tokenizer to find sentences
	 * @param parentArtifact can be document, paragraph or any other document section
	 * @throws IOException
	 */
	protected void loadSentences(Artifact parentArtifact) throws IOException {
		Tokenizer docTokenizer = new Tokenizer(parentArtifact.getAssociatedFilePath());
		HashMap<Integer, String> setences = docTokenizer.getSentences();
		List<Artifact> setencesArtifacts = new ArrayList<Artifact>();
		Artifact previous_sentence = null;

		for(int curSentenceIndex=0;
				curSentenceIndex<setences.keySet().size();curSentenceIndex++){
			//			System.out.print("\r Loading sentences for: "+parentDoc.get_associatedFilePath()+ " "+
			//					curSentenceIndex + "/ " + setences.size()+longspace);

			String tokenizedSentence = setences.get(curSentenceIndex);
			List<Integer> tokens_starts = 
					docTokenizer.sentences_tokens_indexes.get(curSentenceIndex);
			List<String> tokens = 
					docTokenizer.sentences_tokens_string.get(curSentenceIndex);

			if(tokens.size()==0)
				continue;

			Artifact new_sentence = Artifact.getInstance(Artifact.Type.Sentence,
					parentArtifact.getAssociatedFilePath(), tokens_starts.get(0));//line number start from 1
			new_sentence.setParentArtifact(parentArtifact);
			new_sentence.setLineIndex(curSentenceIndex+1);
			new_sentence.setContent(tokenizedSentence);
			if (previous_sentence != null) {
				new_sentence.setPreviousArtifact(previous_sentence);
				previous_sentence.setNextArtifact(new_sentence);
				HibernateUtil.save(previous_sentence);
			}

			HibernateUtil.save(new_sentence);

			loadWords(new_sentence,tokens, tokens_starts, curSentenceIndex);

			setencesArtifacts.add(new_sentence);

			//Pattern.insert(Util.getSentencePOSsPattern(curSentence));
			previous_sentence = new_sentence;
			HibernateUtil.clearLoaderSession();
		}
	}

	protected void loadWords(Artifact parentSentence,  List<String> tokens, 
			List<Integer> starts, int parentOffset) {

		List<Artifact> tokensArtifacts = new ArrayList<Artifact>();
		Artifact previous_word = null;
		int tokens_count = tokens.size();
		//save POS
		String textContent = "";
		Artifact new_word = null;
		//		Util.log(""+tokens_count, 1);
		for(int curTokenIndex=0;
				curTokenIndex<tokens_count;curTokenIndex++){
			textContent = 
					PTBTokenizer.ptbToken2Text(tokens.get(curTokenIndex));
			new_word = Artifact.getInstance(
					Artifact.Type.Word, 
					parentSentence.getAssociatedFilePath(), starts.get(curTokenIndex));
			new_word.setContent(textContent);
			new_word.setParentArtifact(parentSentence);
			new_word.setLineIndex(parentOffset+1);
			new_word.setWordIndex(curTokenIndex);

			if (previous_word != null) {
				new_word.setPreviousArtifact(previous_word);
				previous_word.setNextArtifact(new_word);
				HibernateUtil.save(previous_word);
			}

			HibernateUtil.save(new_word);

			tokensArtifacts.add(new_word);
			previous_word = new_word;

		}
	}
	@Override
	/**
	 * Create document(s) artifact(s) and call processDocument for each document
	 * @param filesRoot
	 */
	public List<Artifact> processDocuments(String rootPath) {
		File f = new File(rootPath);
		List<Artifact> loaded_documents = new ArrayList<Artifact>();
		if (f.exists() && f.isFile()) {
			//Util.log("Loading document :"+filesRoot, Level.INFO);
			//Util.generateParseFilesIfnotExist(filesRoot);

			Artifact new_doc = Artifact.getInstance(Artifact.Type.Document, rootPath, 0);
			try {
				processDocument(new_doc);
			} catch (Exception e) {
				e.printStackTrace();
			}

			loaded_documents.add(new_doc);

		} else {
			List<File> files = 
					FileUtil.getFilesInDirectory(rootPath, documentExtension);

			for(File file : files) {
				Artifact new_doc = 
						Artifact.getInstance(Artifact.Type.Document, file.getAbsolutePath(), 0);

				loaded_documents.add(new_doc);
			}

			for(Artifact doc:loaded_documents){
				System.out.print("\nLoading document: " + doc.getAssociatedFilePath());
				try {
					processDocument(doc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		this.documents = loaded_documents;
		return this.documents;
	}

}
