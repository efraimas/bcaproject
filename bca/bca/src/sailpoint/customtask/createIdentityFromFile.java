package sailpoint.customtask;

import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

public class createIdentityFromFile extends AbstractTaskExecutor{
	public static String CLASS_NAME = "::createIdentityFromFile::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.createIdentityFromFile");
	@Override
	public void execute(SailPointContext context, TaskSchedule taskSchedule, TaskResult taskResult, Attributes<String, Object> attributes)
			throws Exception {
		// TODO Auto-generated method stub
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String filePath = (String) attributes.get("fileIdentity");
		logger.debug(CLASS_NAME + METHOD_NAME + "will be processed " + filePath);
		
		if (filePath != null) {
			logger.debug(CLASS_NAME + METHOD_NAME + "file path is not null");
			
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			    String line;
			    int clearFirstLine = 0;
			    while ((line = br.readLine()) != null) {
			    	logger.debug(CLASS_NAME + METHOD_NAME + "line :" + line);
			    	if(clearFirstLine == 0){
			    		clearFirstLine++;
			    	}else{
			    		line = line.replace("\"", "");
				    	String[] temp = line.split(",");
				    	
				    	String employeeId = temp[1];
				    	
				    	Identity identity = new Identity();
			    	}
			    }
			}catch (Exception e) {
				e.printStackTrace();
			}
				// TODO: handle exception
		}
		
		
	}

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void main(String[]args){
		String filePath = "C:\\Users\\aspadmin\\Desktop\\Sent Email\\Powershell\\Create ID Non Karyawan new.csv";
		
		if(filePath != null){
			
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				 String line;
				 String identity;
				 int clearFirstLine = 0;
				    while ((line = br.readLine()) != null) {
				    	if(clearFirstLine == 0){
				    		clearFirstLine++;
				    	}else{
				    		line = line.replace("\"", "");
					    	String[] temp = line.split(",");
							
					    	identity = temp[0];  
					    	System.out.println(identity);
				    	}
				    }
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("File Path Tidak Ditemukan");
		}
	}
}
