package sailpoint.customtask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

public class correlationRacfData extends AbstractTaskExecutor{
	public static String CLASS_NAME = "::correlationRacfData::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.correlationRacfData");

	@Override
	public void execute(SailPointContext context, TaskSchedule taskSchedule, TaskResult taskResult, Attributes<String, Object> attributes)
			throws Exception {
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

	
		String filePath = (String) attributes.get("fileToken");
		logger.debug(CLASS_NAME + METHOD_NAME + "will be processed " + filePath);
		
		if (filePath != null) {
			logger.debug(CLASS_NAME + METHOD_NAME + "file path is not null");
			
			//File file = new File(filePath);
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			    	logger.debug(CLASS_NAME + METHOD_NAME + "line :" + line);
			    	
			    	String IdToken = line.substring(9, line.length());
			    	String NIP = line.substring(0,8);
			    	
			    	logger.debug(CLASS_NAME + METHOD_NAME + "ID Token dari Csv :" + IdToken);
			    	logger.debug(CLASS_NAME + METHOD_NAME + "NIP :" + NIP);
			    	QueryOptions localQueryOptions = new QueryOptions();
					localQueryOptions.add(Filter.eq(IdentityAttribute.EMPLOYEE_ID,(String)NIP));
					Iterator<Identity> it = context.search(Identity.class, localQueryOptions);
					while(it.hasNext()){
						Identity identity = it.next();
						logger.debug("Identity found with NIP " + identity.getAttribute(IdentityAttribute.EMPLOYEE_ID));
						identity.setAttribute(IdentityAttribute.TOKEN_ID, IdToken);
						logger.debug("Identity will be saved with NIP " + identity.getAttribute(IdentityAttribute.EMPLOYEE_ID) + " and Token ID " + identity.getAttribute(IdentityAttribute.TOKEN_ID) );
//						context.saveObject(identity);
					}
			    }
			}catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		else{
			logger.debug(CLASS_NAME + METHOD_NAME + "Data Dalam File Csv Tidak Ditemukan dengan alamat : " + filePath);
		}
	}

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		return false;
	}

}
