package rainbownlp.preprocess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;
import rainbownlp.util.ConfigurationUtil;

public class SentenceDetector {
	SentenceDetectorME sentenceDetector = null;
	public SentenceDetector() throws FileNotFoundException{
		InputStream modelIn = ConfigurationUtil.class.getClassLoader().getResourceAsStream("en-sent.bin");

		try {
		  SentenceModel model = new SentenceModel(modelIn);
		  sentenceDetector = new SentenceDetectorME(model);
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
	}
	
	public String[] getSentence(String text){
		String sentences[] = sentenceDetector.sentDetect(text);
		
		return sentences;
	}
	public Integer[] getSentencePos(String text){
		
		Span[] spans= sentenceDetector.sentPosDetect(text);
		Integer[] positions = new Integer[spans.length];
		for(int i=0;i<spans.length;i++){
			positions[i] = spans[i].getStart();
		}
		return positions;
	}
}
