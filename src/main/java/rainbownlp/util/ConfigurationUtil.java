package rainbownlp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigurationUtil {
	public static final String RuleSureCorpus = "RuleSure";
	public static boolean SaveInGetInstance = true;
	static Properties configFile = new Properties();
	static boolean loadedRNLPConfig = false;
	public enum OperationMode
	{
		TRIGGER,
		EDGE,
		ARTIFACT
	}
	public static OperationMode Mode = OperationMode.TRIGGER;
	public static boolean TrainingMode = true;
	// This switch between using Development set or Test set for evaluation, set to true if you want to generate test submission files
	public static boolean ReleaseMode = false;
	public static int NotTriggerNumericValue = 10;
	public static int NotEdgeNumericValue = 9;
	public static int MinInstancePerLeaf;
	public static Double SVMCostParameter;
	public static Double SVMPolyCParameter;
	public static enum SVMKernels  {
		  Linear, //0: linear (default)
        Polynomial, //1: polynomial (s a*b+c)^d
        Radial, //2: radial basis function exp(-gamma ||a-b||^2)
        SigmoidTanh //3: sigmoid tanh(s a*b + c)
	};
	public static SVMKernels SVMKernel;

	public static boolean batchMode = false;
	public static int crossValidationFold;
	public static int crossFoldCurrent = 0;
	
	public static String[] getArrayValues(String key)
	{
		if(getValue(key)==null)
			return null;
		String[] arrayValues = getValue(key).split("\\|");
		return arrayValues;
	}
	public static void init()
	{
		if(!loadedRNLPConfig){
			  Properties rnlpConfigFile = new Properties();
			try {
				InputStream config_file = 
						ConfigurationUtil.class.getClassLoader().
							getResourceAsStream("configuration.conf");//
					//Configuration.class.getClassLoader().getResourceAsStream("/configuration.properties");
				rnlpConfigFile.load(config_file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			configFile.putAll(rnlpConfigFile);
			
			MinInstancePerLeaf = Integer.parseInt(configFile.getProperty("MinInstancePerLeaf"));
			SVMCostParameter = Double.parseDouble(configFile.getProperty("SVMCostParameter"));
			SVMPolyCParameter = Double.parseDouble(configFile.getProperty("SVMPolyCParameter"));
			ReleaseMode = Boolean.parseBoolean(configFile.getProperty("ReleaseMode"));
			SVMKernel = SVMKernels.values()[Integer.parseInt(configFile.getProperty("SVMKernel"))];
		}
	}
	
	public static void init(String configurationFileName) throws IOException
	{
		init();
		configFile = new Properties();
		InputStream config_file = 
				ConfigurationUtil.class.getClassLoader().
				getResourceAsStream(configurationFileName);//
		//Configuration.class.getClassLoader().getResourceAsStream("/configuration.properties");
		configFile.load(config_file);
	}
	public static void init(String configurationFileName, String hibernateConfigFile) throws IOException
	{
		init(configurationFileName);
		HibernateUtil.initialize("hibernate-oss.cfg.xml");
	}
	public static String getValue(String key){
		init();
		return configFile.getProperty(key);
	}

	public static int getValueInteger(String key) {
		int result = Integer.parseInt(getValue(key));
		return result;
	}

	public static String getResourcePath(String resourceName) {
		return ConfigurationUtil.class.getClassLoader().getResource(resourceName).getPath();
	}

	public static InputStream getResourceStream(String resourceName) {
		return ConfigurationUtil.class.getClassLoader().getResourceAsStream(resourceName);
	}
	

	
	
}
