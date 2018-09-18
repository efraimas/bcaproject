package sailpoint.customtask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

@SuppressWarnings({ "unused", "rawtypes", "static-access", "resource" })
public class Base24FileFeedConverter extends AbstractTaskExecutor {

	public static String CLASS_NAME = "::Base24FileFeedConverter::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.Base24FileFeedConverter");
	
	private static String delimiter = ",";
	
	public static String lineHeader = "\"" + "LAST ACTION-DATE" + "\""
			+ delimiter + "\"" + "WILAYAH" + "\""
			+ delimiter + "\"" + "CABUT" + "\""
			+ delimiter + "\"" + "KODEC" + "\""
			+ delimiter + "\"" + "USER-ID" + "\""
			+ delimiter + "\"" + "GROUP" + "\""
			+ delimiter + "\"" + "USER" + "\""
			+ delimiter + "\"" + "NIP" + "\""
			+ delimiter + "\"" + "NAMA USER" + "\""
			+ delimiter + "\"" + "FILE-HAL-AKSES" + "\""
			+ delimiter + "\"" + "PASSWORD" + "\""
			+ delimiter + "\"" + "REVOKE-DATE" + "\""
			+ delimiter + "\"" + "RESUME-DATE" + "\"";
	
	private static int idxWilayah = 8;
	
	private static int idxCabut = 14;
	
	private static int idxKodec = 20;
	
	private static int idxUserId = 39;
	
	private static int idxGroup = 43;
	
	private static int idxUser = 49;
	
	private static int idxNip = 56;
	
	private static int idxNamaUser = 77;
	
	private static int idxFile = 84;
	
	private static int idxHal = 87;
	
	
	private static String invalidString = "^.*(USER-ID|RETENSI|LAPORAN|CABANG|---).*$";
	
	private String profiles = "";
	
	private String addNewLine(String input){
				String result = "";

				try{
					result += "\n" + "\"\",\"" + input.substring(0, idxWilayah).trim() + "\"" 
							+ delimiter + "\"" + input.substring(idxWilayah, idxCabut).trim() + "\"" 
							+ delimiter + "\"" + input.substring(idxCabut, idxKodec).trim() + "\""
							+ delimiter + "\"" + input.substring(idxKodec, idxUserId).trim() + "\""
							+ delimiter + "\"" + input.substring(idxUserId, idxGroup).trim() + "\""
							+ delimiter + "\"" + input.substring(idxGroup, idxUser).trim() + "\""
							+ delimiter + "\"" + input.substring(idxUser, idxNip).trim() + "\""
							+ delimiter + "\"" + input.substring(idxNip, idxNamaUser).trim() + "\"" ;
				}catch(Exception e){
					e.printStackTrace();
					System.exit(0);
				}
		return result;
	}
	
	private void addProfiles(String input){
		
		try{
			this.profiles += (this.profiles.trim().length() == 0 ? "" : delimiter) + input.substring(idxNamaUser, idxFile).trim()
					+ "-" + input.substring(idxFile, idxHal).trim()
					+ "-" + input.substring(idxHal, input.length()).trim();	
		//this.profiles.trim().length() == 0 ?"" :
		}catch(Exception e){
			System.out.println(input);
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void execute(SailPointContext arg01, TaskSchedule arg11,
			TaskResult arg2, Attributes<String, Object> attributes)  throws Exception {
		
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String inputFilePath = "";
		String outputFilePath = "";

		List inputAttributeListKey = attributes.getKeys();
		
		Iterator it = inputAttributeListKey.iterator();
		while (it.hasNext()) {
			String keyName = (String) it.next();
			String keyValue = attributes.getString(keyName);

			if (keyName.equalsIgnoreCase("inputFilePath")) {
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "File Location Found....");
				inputFilePath = keyValue;
			}
			
			if (keyName.equalsIgnoreCase("outputFilePath")) {
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "File Location Found....");
				outputFilePath = keyValue;
			}
		}
		
		Base24FileFeedConverter converter = new Base24FileFeedConverter();
		
		try {
			converter.writeFile(outputFilePath, converter.readFile(inputFilePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("finally")
	private String readFile(String filePath) throws IOException{
		StringBuilder csvString = new StringBuilder();
		csvString.append(lineHeader);
		File file = new File(filePath); /* ini digunakan pada versi java 6 */
		
		Charset charset = Charset.forName("US-ASCII");
		
		logger.debug(CLASS_NAME + " mulai membaca file " + takeTime());
		
		BufferedReader reader = new BufferedReader(new FileReader(file)); /* ini juga digunakan pada java 6 */
		try
		{
		    String line = null;
		    String tempStr = "";
		    
		    while((line = reader.readLine()) != null && line.trim().length() > 0)
		    	{	 
		   
		    	boolean newUser = isNewUser(line);
		    	System.out.println("teks : " + line);
		    	if(!line.matches(invalidString))
		    		{
		    		if(newUser)
		    			{
			    			tempStr += this.profiles.trim().length() == 0 ?"" : delimiter + "\"" + this.profiles + "\",\"\"";
			    			this.profiles = "";
			    			tempStr  += addNewLine(line);
		    			}
				    		if(!newUser)
				    		addProfiles(line);	
		    		}
		    		}
		    if(((line = reader.readLine()) == null || line.lastIndexOf("TOTAL RECORD") > 0) && this.profiles!= null && this.profiles.length() > 1){
		    	tempStr += delimiter + "\"" + this.profiles + "\",\"\"";
		    	this.profiles = "";
		    }
		    
		    csvString.append(tempStr);
		   
		    
		} catch (IOException x) {
			x.printStackTrace();
			System.exit(0);
		}finally{
			logger.debug(CLASS_NAME + " selesai membaca file " + takeTime());
			return csvString.toString();
		}
	}
	
	private boolean isNewUser(String input){
		try{
			if((input != null && input.substring(0, 3) != null && input.substring(0, 3).trim().length() > 1) ){ //&& input.indexOf("BASE") > 0
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static void writeFile(String filePath, String content){
		File outputFile = new File(filePath);
		
		logger.debug(CLASS_NAME + " mulai menulis file " + takeTime());
		
		FileWriter fw = null;
			try {
					outputFile.createNewFile();
					fw = new FileWriter(outputFile.getAbsoluteFile());

				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		logger.debug(CLASS_NAME + " selesai menulis file " + takeTime());
	}

	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private static String takeTime(){
		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		return df.format(new Date());
	}
	
	/*public static void main(String args[]){
		
		System.out.println("Mulai eksekusi : " + takeTime());
		String inputFilePath = "E:\\project\\BCA\\test\\USERID_B24.TXT";
		String outputFilePath = "E:\\project\\BCA\\test\\USERID_B24.csv";
		
		Base24FileFeedConverter converter = new Base24FileFeedConverter();
		
		try {
			System.out.println("Persiapan untuk membaca file : " + takeTime());
			String readFile = converter.readFile(inputFilePath);			
			System.out.println("Selesai membaca file : " + takeTime());			
			converter.writeFile(outputFilePath, readFile);			
			System.out.println("Selesai menulis file : " + takeTime());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}*/

}