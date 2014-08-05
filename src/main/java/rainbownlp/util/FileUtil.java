package rainbownlp.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class FileUtil {
	public static final String DEBUG_FILE = "debug.log";
	/**
	 * Create new file if not exists
	 * @param path
	 * @return true if new file created
	 */
	public static boolean createFileIfNotExists(String path) {
		boolean result = false;
		File file = new File(path);
		if (!file.exists()) {
			try {
				result = file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	 
	 public static void appendLine(String path, String line) throws UnsupportedEncodingException, FileNotFoundException
	 {
		 File file = new File(path);
		 Writer writer = new BufferedWriter(new OutputStreamWriter(
			        new FileOutputStream(file, true), "UTF-8"));
			
			try {
				writer.write(line+"\n");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	 }
	public static void createFilewithFormat(String path,
				List<String> contentLines,String format) throws UnsupportedEncodingException, FileNotFoundException {

			File file = new File(path);
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), format));
			
			try {
				
				for(int i=0;i<contentLines.size();i++)
				{
					String line = contentLines.get(i);
					out.write(line+"\n");
				}
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}

	
	public static void createFileIfNotExists(String path,
			List<String> contentLines) {

		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				for(int i=0;i<contentLines.size();i++)
				{
					String line = contentLines.get(i);
					writer.write(line+"\n");
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void createFile(String path,
			List<String> contentLines) {
		System.out.println("Creating file: "+path);
		File file = new File(path);
		
		try {
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(int i=0;i<contentLines.size();i++)
			{
				String line = contentLines.get(i);
				writer.write(line+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 
	 * @param path
	 * @return true if created
	 */
	public static boolean createFolderIfNotExists(String path) {
		boolean result = false;
		File folder = new File(path);
		if (!folder.exists() || !folder.isDirectory()) {
			result = folder.mkdirs();
		}

		return result;
	}

	public static String deleteAndCreateFolder(String path) throws IOException {
		
		boolean success_del;
		File folder = new File(path);
		success_del = deleteDirectory(folder);
		if (!success_del) {
		    // Deletion failed
			throw new IOException();
		}
		File newFolder = new File(path);

		if (newFolder.mkdir()==true)
		{
			return path;
		}
		else{
			throw new IOException();
		}

	}
  static  boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }
	
	public static void CopyFileToDirectory(String file_name, String from_directory, String to_directory)
	{
	  	InputStream inStream = null;
		OutputStream outStream = null;
	 
    	try{
 
    	    File afile =new File(from_directory+"/"+file_name);
    	    File bfile =new File(to_directory+"/"+file_name);
 
    	    inStream = new FileInputStream(afile);
    	    outStream = new FileOutputStream(bfile);
 
    	    byte[] buffer = new byte[1024];
 
    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = inStream.read(buffer)) > 0){
 
    	    	outStream.write(buffer, 0, length);
 
    	    }
 
    	    inStream.close();
    	    outStream.close();
 
    	    System.out.println("File is copied successful!");
 
    	}catch(IOException e){
    	    e.printStackTrace();
    	}
	    
	}
	
	/**
	 * Read whole file content into a string
	 * @param filePath
	 * @return The File content
	 * @throws IOException
	 */
	public static String readWholeFile(String filePath) throws IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		FileReader fr = new FileReader(filePath);
        BufferedReader reader = new BufferedReader(fr);
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        fr.close();
        return fileData.toString();
	}
	
	public static List<File> getFilesInDirectory(String root,String extension)
	{
		List<File> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(root))) {
            for (Path path : directoryStream) {
            	if(path.toString().endsWith(extension))
            		fileNames.add(new File(path.toString()));
            }
        } catch (IOException ex) {}
        
        return fileNames;
	}

	public static List<String> loadLineByLine(String file_path) {
		List<String> lines = new ArrayList<String>();
		try {
			File f = new File(file_path);
			if(!f.exists()) return lines;
			BufferedReader br1 = new BufferedReader(
					new FileReader(f));
			while (br1.ready()) {
				String line = br1.readLine();
				lines.add(line);
			}
			br1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
	public static List<String> loadLineByLineAndTrim(String file_path) {
		List<String> lines = new ArrayList<String>();
		try {
			File f = new File(file_path);
			if(!f.exists()) return lines;
			BufferedReader br1 = new BufferedReader(
					new FileReader(f));
			while (br1.ready()) {
				String line = br1.readLine();
				lines.add(line.trim());
			}
			br1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}


	public static boolean fileExists(String path) {
		File file = new File(path);
		return file.exists();
	}
	public static String getFilePathInDirectory(String directory, String fileName)
	{
		String file_path = directory;
		file_path += "/"+fileName;
		return file_path;
	}
	public static void logLine(String filePath, String line)
	{
		if(filePath==null)
		{
			String log = (new Date())+" : "+line+"\n";
			System.out.print(log);
		}else
		{
			try {
				
				BufferedWriter writer = new BufferedWriter( new FileWriter( filePath , true ) );
//				writer.write((new Date())+" : "+line+"\n");
				writer.write(line+"\n");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static String ReadFileInToString(String filePath) throws IOException {
	       
        //create file object
        File file = new File(filePath);
        BufferedInputStream bin = null;
        String strFileContents = null;
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
	}
	
	public static String getTextFileName(String FilePath)
	{
		String file_name= getFileNameWithoutPath(FilePath);
		String text_file = "/"+file_name+".txt";
		return text_file;
	}
	public static String getFileNameWithoutPath(String FilePath)
	{
		String file_name="";
		Pattern p = Pattern.compile("(.*/)(.+)\\.\\w\\w\\w.?(txt)?$");
		Matcher m = p.matcher(FilePath);
		if (m.matches())
		{
			file_name = m.group(2);
		}
		return file_name;
	}
	
	public static Document parseXmlFile(String xmlFile) throws Exception{
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


		//Using factory get an instance of document builder
		DocumentBuilder db = dbf.newDocumentBuilder();

		//parse using builder to get DOM representation of the XML file
		Document dom = db.parse(xmlFile);


		return dom;
	}

	public static void logLine(String line) {
		logLine(null, line);
	}

 

}
