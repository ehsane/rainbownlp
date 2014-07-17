package rainbownlp.core;

import java.util.Date;
import java.util.HashMap;

public class TimeStamp {
	static HashMap<String, Date> blockTimes = new HashMap<String, Date>();
	public static void setStart(String blockName) {
		blockTimes.put(blockName, new Date());
	}

	public static void setEnd(String blockName) {
		Date now = new Date();
		Date start = blockTimes.get(blockName);
		if(start!=null){
			Double timePassed = (double)(now.getTime() - start.getTime())/(double)1000;
			if(timePassed>1)
			System.out.println("TIME - "+blockName+" : "+timePassed);
		}
		
	}

}
