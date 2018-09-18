package sailpoint.customtask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
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

@SuppressWarnings({ "unused", "rawtypes" })
public class SAPFileFeedConverter extends AbstractTaskExecutor{
	
	public static String CLASS_NAME = "::SAPFileFeedConverter::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.SAPFileFeedConverter");

	private static String delimiter = ",";
	
	private static String lineHeader = "\"" + "employeeId" + "\""
											+ delimiter + "\"" + "salutationName" + "\""
											+ delimiter + "\"" + "gender" + "\""
											+ delimiter + "\"" + "dob" + "\""
											+ delimiter + "\"" + "employeeStatus" + "\""
											+ delimiter + "\"" + "religion" + "\""
											+ delimiter + "\"" + "name" + "\""
											+ delimiter + "\"" + "address" + "\""
											+ delimiter + "\"" + "address1" + "\""
											+ delimiter + "\"" + "address2" + "\""
											+ delimiter + "\"" + "city" + "\""
											+ delimiter + "\"" + "province" + "\""
											+ delimiter + "\"" + "telpNumber" + "\""
											+ delimiter + "\"" + "zipCode" + "\""
											+ delimiter + "\"" + "email" + "\""
											+ delimiter + "\"" + "subDivisionCode" + "\""
											+ delimiter + "\"" + "subDivisionName" + "\""
											+ delimiter + "\"" + "doj" + "\""
											+ delimiter + "\"" + "jobCode" + "\""
											+ delimiter + "\"" + "jobDesc" + "\""
											+ delimiter + "\"" + "branchCode" + "\""
											+ delimiter + "\"" + "branchName" + "\""
											+ delimiter + "\"" + "regionCode" + "\""
											+ delimiter + "\"" + "regionName" + "\""
											+ delimiter + "\"" + "positionCode" + "\""
											+ delimiter + "\"" + "positionName" + "\""
											+ delimiter + "\"" + "managerId" + "\""
											+ delimiter + "\"" + "echelon" + "\""
											+ delimiter + "\"" + "divisionCode" + "\""
											+ delimiter + "\"" + "divisionName" + "\""
											+ delimiter + "\"" + "costCenter" + "\"";
	
	private static int employeeid = 8;	
	private static int salutationName = 48;	
	private static int gender = 108;	
	private static int dob = 116;	
	private static int employeeStatus = 136;	
	private static int religion = 161;	
	private static int name = 201;
	private static int address = 261;	
	private static int address1 = 301;	
	private static int address2 = 341;	
	private static int city = 381;
	private static int province = 401;	
	private static int telpNumber = 415;	
	private static int zipCode = 425;	
	private static int email = 666;	
	private static int subDivisionCode = 674;	
	private static int subDivisionName = 714;	
	private static int doj = 722;	
	private static int jobCode = 730;	
	private static int jobDesc = 770;	
	private static int branchCode = 774;	
	private static int branchName = 789;	
	private static int regionCode = 793;	
	private static int regionName = 823;	
	private static int positionCode = 831;	
	private static int positionName = 871;	
	private static int managerId = 879;	
	private static int echelon = 899;	
	private static int divisionCode = 907;	
	private static int divisioName = 947;	
	private static int costCenter = 960;
	
	private static String addDelimiter(String input){
		String result = null;
		int length = input.length();
		
		result = "\"" + input.substring(0, employeeid) + "\"" 
				+ delimiter + "\"" + input.substring(employeeid, salutationName).trim() + "\"" 
				+ delimiter + "\"" + input.substring(salutationName, gender).trim() + "\""
				+ delimiter + "\"" + isTglValid(input.substring(gender, dob)).trim() + "\""
				+ delimiter + "\"" + input.substring(dob, employeeStatus).trim() + "\""
				+ delimiter + "\"" + input.substring(employeeStatus, religion).trim() + "\""
				+ delimiter + "\"" + input.substring(religion, name).trim() + "\""
				+ delimiter + "\"" + removeDobleQuotes(input.substring(name, address)).trim() + "\""
				+ delimiter + "\"" + removeDobleQuotes(input.substring(address, address1)).trim() + "\""
				+ delimiter + "\"" + input.substring(address1, address2).trim() + "\""
				+ delimiter + "\"" + input.substring(address2, city).trim() + "\""
				+ delimiter + "\"" + input.substring(city, province).trim() + "\""
				+ delimiter + "\"" + input.substring(province, telpNumber).trim() + "\""
				+ delimiter + "\"" + input.substring(telpNumber, zipCode).trim() + "\""
				+ delimiter + "\"" + input.substring(zipCode, email).trim() + "\""
				+ delimiter + "\"" + input.substring(email, subDivisionCode).trim() + "\""
				+ delimiter + "\"" + input.substring(subDivisionCode, subDivisionName).trim() + "\""
				+ delimiter + "\"" + isTglValid(input.substring(subDivisionName, doj)).trim() + "\""
				+ delimiter + "\"" + input.substring(doj, jobCode).trim() + "\""
				+ delimiter + "\"" + input.substring(jobCode, jobDesc).trim() + "\""
				+ delimiter + "\"" + input.substring(jobDesc, branchCode).trim() + "\""
				+ delimiter + "\"" + input.substring(branchCode, branchName).trim() + "\""
				+ delimiter + "\"" + input.substring(branchName, regionCode).trim() + "\""
				+ delimiter + "\"" + input.substring(regionCode, regionName).trim() + "\""
				+ delimiter + "\"" + input.substring(regionName, positionCode).trim() + "\""
				+ delimiter + "\"" + input.substring(positionCode, positionName).trim() + "\"";
		
				if(length > managerId)
					result += delimiter + "\"" + input.substring(positionName, managerId).trim() + "\"";
				if(length > echelon)
					result += delimiter + "\"" + input.substring(managerId, echelon).trim() + "\"";
				if(length > divisionCode)
					result += delimiter + "\"" + input.substring(echelon, divisionCode).trim() + "\"";
				if(length > divisioName)
					result += delimiter + "\"" + input.substring(divisionCode, divisioName).trim() + "\"";
				if(length >= costCenter)
					result += delimiter + "\"" + input.substring(divisioName, costCenter).trim() + "\""; 
				result+= "\n";
		
		return result;
	}
	
	public void execute(SailPointContext arg0, TaskSchedule arg1,
			TaskResult arg2, Attributes attributes) throws Exception {
		// TODO Auto-generated method stub
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String inputFilePath = "";
		String outputFilePath = "";

		List inputAttributeListKey = attributes.getKeys();
		
		Iterator it = inputAttributeListKey.iterator();
		while (it.hasNext()) {
			String keyName = (String) it.next();
			String keyValue = attributes.getString(keyName);
			
			logger.debug(CLASS_NAME + METHOD_NAME + " key : " + keyName + ", value : " + keyValue);

			if (keyName.equalsIgnoreCase("inputFilePath")) {
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "Input File Location Found...." + keyValue);
				inputFilePath = keyValue;
			}
			
			if (keyName.equalsIgnoreCase("outputFilePath")) {
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "Output File Location Found...." + keyValue);
				outputFilePath = keyValue;
			}
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + inputFilePath + ":" + outputFilePath);
		SAPFileFeedConverter.runProcess(inputFilePath, outputFilePath);
	}
	
	public static void readAndWriteFile(String filePath, String outFilePath) {
		String csvHeaderString = lineHeader;
		File file = new File(filePath);
		File outputFile = new File(outFilePath);
		
		if (!outputFile.exists()) {
			
			BufferedWriter bw = null;
			BufferedReader reader = null;
			try {
				outputFile.createNewFile();
				
				FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				
				//Charset charset = Charset.forName("US-ASCII");
				reader = new BufferedReader(new FileReader(file));
				
				bw.write(csvHeaderString);
				bw.write("\n");

				String line = null;
				long time1 = System.currentTimeMillis();
				long time2 = System.currentTimeMillis();
				long time3 = System.currentTimeMillis();
				long totaltime1 = 0;
				long totaltime2 = 0;
				long totaltime3 = 0;
				
				while ((line = reader.readLine()) != null) {
					time1 = System.currentTimeMillis();
					totaltime1 += time1 - time3;
					String csvString = addDelimiter(line);
					time2 = System.currentTimeMillis();
					totaltime2 += time2 - time1;
					bw.write(csvString);
					time3 = System.currentTimeMillis();
					totaltime3 += time3 - time2;
				}
				
				logger.debug("totaltime1:" + totaltime1);
				logger.debug("totaltime2:" + totaltime2);
				logger.debug("totaltime3:" + totaltime3);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bw != null) {
					try {
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
/* Ditambahkan	*/	}else if(outputFile.exists()){
				BufferedWriter bw = null;
				BufferedReader reader = null;
				try {
					outputFile.createNewFile();
					
					FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
					bw = new BufferedWriter(fw);
					
					//Charset charset = Charset.forName("US-ASCII");
					reader = new BufferedReader(new FileReader(file));
					
					bw.write(csvHeaderString);
					bw.write("\n");
			
					String line = null;
					long time1 = System.currentTimeMillis();
					long time2 = System.currentTimeMillis();
					long time3 = System.currentTimeMillis();
					long totaltime1 = 0;
					long totaltime2 = 0;
					long totaltime3 = 0;
					
					while ((line = reader.readLine()) != null) {
						time1 = System.currentTimeMillis();
						totaltime1 += time1 - time3;
						String csvString = addDelimiter(line);
						time2 = System.currentTimeMillis();
						totaltime2 += time2 - time1;
						bw.write(csvString);
						time3 = System.currentTimeMillis();
						totaltime3 += time3 - time2;
					}
					
					logger.debug("totaltime1:" + totaltime1);
					logger.debug("totaltime2:" + totaltime2);
					logger.debug("totaltime3:" + totaltime3);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (bw != null) {
						try {
							bw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
/* END	*/	}
	}	
	
	public static String isTglValid(String input) {
		String pola = "yyyyMMdd";
		String output = "";
		try {
			output = input.trim();
			
			String month = output.substring(4, 6);		
			int bulan = Integer.parseInt(month);
			
			String date = output.substring(6, 8);
			int tanggal = Integer.parseInt(date);
			
			if(bulan>12){
				System.out.println("Format Bulan Salah");
				DateFormat df = new SimpleDateFormat(pola);
				df.setLenient(false);
				Date dt = df.parse(output);
			}
			
			if(tanggal>31){
				System.out.println("Format Tanggal Salah");
				DateFormat df = new SimpleDateFormat(pola);
				df.setLenient(false);
				Date dt = df.parse(output);
			}
			
		}catch(Exception e){
				//e.printStackTrace();
		}
			
			return output;
	}
	
	@SuppressWarnings("finally")
	public static String removeDobleQuotes(String input)
	{
		String output = "";
		try{
		 output = input.trim();
		if(output.length() > 1 && "\"".equalsIgnoreCase(output.substring(0,1)))
		{
			output = output.substring(1);
		}

		if(output.length() > 1 && "\"".equalsIgnoreCase(output.substring(output.length()-1, output.length())))
		{
			output = output.substring(0, output.length()-1);
		}
		}
		catch(Exception e){
			e.printStackTrace();

		}finally{
			return output;
		}
	}
	
	public static void runProcess(String inputFilePath, String outputFilePath) {//String args[]){
		
		try {
			logger.debug("Persiapan untuk membaca file : " + takeTime());
			
			SAPFileFeedConverter.readAndWriteFile(inputFilePath, outputFilePath);
			
			//String readFile = converter.readFile(inputFilePath);
			
			//System.out.println("Selesai membaca file : " + takeTime());
			
			//converter.writeFile(outputFilePath, readFile);
			
			logger.debug("Selesai menulis file : " + takeTime());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	private static String takeTime(){
		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		return df.format(new Date());
	}

	public boolean terminate() {
		// TODO Auto-generated method stub
		return false;
	}
}