package sailpoint.customtask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

@SuppressWarnings({ "unused", "rawtypes"})
public class BCACombineBase24Output extends AbstractTaskExecutor{

	
	public static String CLASS_NAME = "::BCACombineBase24Output::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.BCACombineBase24Output");
	
	
	@Override
	public void execute(SailPointContext context, TaskSchedule arg1,
			TaskResult arg2, Attributes<String, Object> attributes) throws Exception {
		
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String inputFilePathBase24 = "";
		String inputFilePathSail = "";

		List inputAttributeListKey = attributes.getKeys();

		Iterator it = inputAttributeListKey.iterator();
		while (it.hasNext()) {
			String keyName = (String) it.next();
			String keyValue = attributes.getString(keyName);

			if (keyName.equalsIgnoreCase("inputFilePathBase24")) {
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "File Location Found....");
				inputFilePathBase24 = keyValue;
			}
			
			if (keyName.equalsIgnoreCase("inputFilePathSail")) {
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "File Location Found....");
				inputFilePathSail = keyValue;
			}
			

		}
		
		BCACombineBase24Output combine = new BCACombineBase24Output();
		
		try{
			appendFiles(inputFilePathBase24, inputFilePathSail);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public boolean terminate() {

		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		return false;
	}
	
	
	public static void appendFiles(String base24report, String base24Generate) throws IOException {
	
	        try {
	        
	        BufferedWriter out = new BufferedWriter(new FileWriter(base24report, true));
	        BufferedReader input = new BufferedReader(new FileReader(base24Generate));

	        String str;
	        
	        
	        int count = 0;
	        while ((str = input.readLine()) != null) {
	        	if (count != 0) {
	        		out.write("\n" + str);
	        	}
	            count++;
	        }
		        input.close();
		        out.close();
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	}
	

}
