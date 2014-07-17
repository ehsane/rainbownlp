package rainbownlp.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.uima.tokenize.WhitespaceTokenizer;
import rainbownlp.core.Artifact;
import rainbownlp.util.HibernateUtil;
//This class will read all the training sentences and parse them and put penn tree and dependency and POS in the databse

public class GrammaticalParser {
	public ArrayList<WordTag> sentenceWords = new ArrayList<WordTag>();
	private String POS;
	private String parseTree;
	private String dependencies;

	public GrammaticalParser() throws  IOException
	{
		
	}
	
	public static StanfordParser s_parser = new StanfordParser();
	public static void main(String[] args) throws Exception
	{

		
////		StanfordParser s_parser = new StanfordParser();
//		//get all sentence artifact
		List<Artifact> sentences = 
			Artifact.listByType(Artifact.Type.Sentence,true);
		GrammaticalParser ph = new GrammaticalParser();
		
		for (Artifact sentence:sentences)
		{

		break;
		}

		
	}
	public void parseSentence(String sent)
	{
		if (s_parser == null)
		{
			s_parser = new StanfordParser();
		}
		s_parser.parse(sent);
		//TODO put dependencies
		POS = s_parser.getTagged();
		dependencies = s_parser.getDependencies();
//		parseTree = s_parser.getPenn();
	}
	
	
	public void setPOS(String pOS) {
		POS = pOS;
	}

	public String getPOS() {
		return POS;
	}
	public void setParseTree(String parseTree) {
		this.parseTree = parseTree;
	}

	public String getParseTree() {
		return parseTree;
	}
	public void setDependencies(String dependencies) {
		this.dependencies = dependencies;
	}

	public String getDependencies() {
		return dependencies;
	}
	private static class WordTag{
		public String content;
		public String POS;
		public int offset;
		public WordTag() {
			// TODO Auto-generated constructor stub
		}
	}
	
	
}
