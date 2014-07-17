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
import rainbownlp.util.ConfigurationUtil;

public class ParseHandler {
	public ArrayList<WordTag> sentenceWords = new ArrayList<WordTag>();
	POSModel posModel;
	POSTaggerME tagger;
	ChunkerME chunkerME;

	public ParseHandler() throws  IOException
	{
		POSModel posModel = new POSModelLoader()
			.load(new File(ConfigurationUtil.getResourcePath("en-pos-maxent.bin")));
		tagger = new POSTaggerME(posModel);
//		// chunker
		InputStream is = ConfigurationUtil.getResourceStream("en-chunker.bin");
		ChunkerModel cModel = new ChunkerModel(is);
 
		chunkerME = new ChunkerME(cModel);
	}
	public static StanfordParser s_parser = new StanfordParser();
	public static void main(String[] args) throws Exception
	{

		
////		StanfordParser s_parser = new StanfordParser();
//		//get all sentence artifact
		List<Artifact> sentences = 
			Artifact.listByType(Artifact.Type.Sentence,true);
		ParseHandler ph = new ParseHandler();
		
		for (Artifact sentence:sentences)
		{
			ph.sentenceChunker(sentence.getContent());
			
//			calculatePOS(s_parser,sentence);
			
//			//now parse the normalized sentence( here just normalized to head)
//			NormalizedSentence normalized_sent_obj = NormalizedSentence.getInstance(sentence,NormalizationMethod.MethodType.MentionToHead);
//			String normalized_sent = normalized_sent_obj.getNormalizedContent();
//			s_parser.parse(normalized_sent);
//			
//			String nor_dependencies = s_parser.getDependencies();
//			String nor_penn_tree = s_parser.getPenn();
//			normalized_sent_obj.setNormalizedDependency(nor_dependencies);
//			normalized_sent_obj.setNormalizedPennTree(nor_penn_tree);
//			
//			HibernateUtil.save(normalized_sent_obj);
//			
//			HibernateUtil.clearLoaderSession();
		break;
		}

		
	}
	
	public void calculatePOS(Artifact sentence ) throws Exception
	{
		if (s_parser == null)
		{
			s_parser = new StanfordParser();
		}
		s_parser.parse(sentence.getContent());
		//TODO put dependencies
		String pos_tagged_sentence = s_parser.getTagged();
		String dependencies = s_parser.getDependencies();
		String penn_tree = s_parser.getPenn();
		
		sentence.setPOS(pos_tagged_sentence);
		sentence.setStanDependency(dependencies);
		sentence.setStanPennTree(penn_tree);
		HibernateUtil.save(sentence);
		
		ArrayList<WordTag> w_tags = analyzePOSTaggedSentence(pos_tagged_sentence);
		for (int i=0;i<w_tags.size();i++)
		{
			WordTag wt = w_tags.get(i);
			//get artifact
			Artifact word_in_sent = Artifact.findInstance(sentence, i);
					
			if (word_in_sent.getContent().matches("\\w+") && !word_in_sent.getContent().equals(wt.content))
			{
				throw (new Exception("Related artifact is not found"));
			}
			//set POS
			word_in_sent.setPOS(wt.POS);
			HibernateUtil.save(word_in_sent);
			
			HibernateUtil.clearLoaderSession();
		}
	}
	public String calculatePOS(String content) throws Exception
	{
		if (s_parser == null)
		{
			s_parser = new StanfordParser();
		}
		s_parser.parse(content);
		//TODO put dependencies
		String pos_tagged_sentence = s_parser.getTagged();
		return pos_tagged_sentence;
	}
	
	//This will return a list of the word tag objects based on the tagged sentence
	public ArrayList<WordTag> analyzePOSTaggedSentence(String pTaggedSentence) throws Exception
	{
		String tokens[] = pTaggedSentence.split(" ");
		ArrayList<WordTag> word_tags =  new ArrayList<ParseHandler.WordTag>();
		int count=0;
		for (String token:tokens)
		{
			WordTag wt = new WordTag();
			Pattern p = Pattern.compile("(.*)\\/([^\\/]+)");
			Matcher m = p.matcher(token);
			if (m.matches())
			{
				String content = m.group(1);
				content =  content.replaceAll("\\\\/", "/");
				wt.content = content;
				wt.POS = m.group(2);
				wt.offset = count;
				word_tags.add(wt);
				count++;
			}
			else
			{
				throw (new Exception("the POS tag doesn't match the pattern"));
			}
			
			
		}
		return word_tags;
	}
	
//	public static void nounPhraseTagger() throws IOException
//	{
//		InputStream modelIn = null;
//		ChunkerModel model = null;
//
//		modelIn = new FileInputStream("/host/ubnutustuff/projects/rnlp/rnlp/resources/en-chunker.bin");
//		model = new ChunkerModel(modelIn);
//		modelIn.close();
//		ChunkerME chunker = new ChunkerME(model);
//		String input = 
//			"This is a very good test";
//		
//		if (s_parser == null)
//		{
//			s_parser = new StanfordParser();
//		}
//		s_parser.parse(input);
//		//TODO put dependencies
//		String pos_tagged_sentence = s_parser.getTagged();
//		
//		String sent_tokens[]= input.split(" ");
//		
//		String tag[] = chunker.chunk(sent_tokens, pos_tagged_sentence.split(" "));
//
//		for (int i=0; i<sent_tokens.length;i++)
//		{
//			System.out.println(sent_tokens[i]+"**"+tag[i]);
//		}
		
		


//	}
	public Span[] sentenceChunker(String sentence_content) throws Exception {
		 
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		
//		String sentence_content = sent.getContent();
//		String[] sent_tokens = {};
//		List<Artifact> childs = sent.getChildsArtifact();
//		
//		for (int i=0;i<childs.size();i++)
//		{
//			sent_tokens[i]=childs.get(i).getContent();
//			
//		}
		
//		String sentPOS = calculatePOS(sentence_content);
		
//		ObjectStream<String> lineStream = new PlainTextByLineStream(
//				new StringReader(sentence_content));
 
		perfMon.start();
//		String line;
//		String whitespaceTokenizerLine[] = null;
 
		String[] tags = null;
//		while ((line = lineStream.read()) != null) {
// 
//			whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE
//					.tokenize(line);
//			tags = tagger.tag(whitespaceTokenizerLine);
// 
////			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
////			System.out.println(sample.toString());
// 
//			perfMon.incrementCounter();
//		}
//		perfMon.stopAndPrintFinalResult();
		String[] tokens= sentence_content.split(" ");
		tags = tagger.tag(tokens);
		Span[] spans = chunkerME.chunkAsSpans(tokens, tags);
//		String shallow_parsed_sent = "";
//		String sent_transaction = "";
		for (Span s : spans)
//			System.out.println(s.toString()+ "  "+s.getType());
			System.out.println(s.toString()+ "  "+s.getType()+" "+tokens[s.getStart()] + " "+tokens[s.getEnd()-1]);
		return spans;
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
