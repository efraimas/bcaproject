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

public class BCATokenMapping extends AbstractTaskExecutor{
	public static String CLASS_NAME = "::BCATokenMapping::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.BCATokenMapping");

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
			    	line = line.replace("\"", "");
			    	String[] temp = line.split(",");
			    	
			    	String IdToken = temp[0]; 
			    	String NIP = temp[1];
			    	logger.debug(CLASS_NAME + METHOD_NAME + "ID Token dari Csv :" + IdToken);
			    	logger.debug(CLASS_NAME + METHOD_NAME + "NIP :" + NIP);
			    	
			    	Identity identity = null;
			    	if(NIP.contains("CADANGAN")){
			    		logger.debug(CLASS_NAME + METHOD_NAME + "Inside... cadangan");
						
						QueryOptions qo = new QueryOptions();
						qo.add(Filter.eq(IdentityAttribute.DISPLAY_NAME, (String)NIP.trim()));
						Iterator<Identity> it = context.search(Identity.class, qo);
						if(it.hasNext()){
							logger.debug(CLASS_NAME + METHOD_NAME + "Cadangan Ditemukan");
							identity = it.next();
						}else{
							logger.debug(CLASS_NAME + METHOD_NAME + "Cadangan tidak ditemukan");
							identity = null;
						}
			    	}else{
			    		logger.debug(CLASS_NAME + METHOD_NAME + "Inside... bukan cadangan");
			    		identity = context.getObjectByName(Identity.class, NIP.trim());
			    	}
			    	
			    	if(null != identity){
			    		logger.debug("Identity ");
						identity.setAttribute(IdentityAttribute.TOKEN_ID, IdToken);
				    	
						logger.debug("Identity will be saved with NIP " + identity.getAttribute(IdentityAttribute.DISPLAY_NAME) + " and Token ID " 
						+ identity.getAttribute(IdentityAttribute.TOKEN_ID) );
						
						//context.startTransaction();
						context.saveObject(identity);
						context.commitTransaction();	
						context.decache();
			    	}else{
			    		logger.debug(CLASS_NAME + METHOD_NAME + "identity null ...");
			    	}

			    /*	QueryOptions localQueryOptions = new QueryOptions();
					localQueryOptions.add(Filter.eq(IdentityAttribute.EMPLOYEE_ID,(String)NIP));
					Iterator<Identity> it = context.search(Identity.class, localQueryOptions);
					if(it.hasNext()){
						Identity identity = it.next();
						logger.debug("Identity found with NIP " + identity.getAttribute(IdentityAttribute.EMPLOYEE_ID));
						identity.setAttribute(IdentityAttribute.TOKEN_ID, IdToken);
						//identity.setExtended18(IdToken);
						logger.debug("Identity will be saved with NIP " + identity.getAttribute(IdentityAttribute.EMPLOYEE_ID) + " and Token ID " + identity.getAttribute(IdentityAttribute.TOKEN_ID) );
						context.saveObject(identity);
						context.commitTransaction();
					}else{
						break;
					}*/
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
	
	/*public static void main(String[]args){
		String filePath = "C:\\nasbca\\dataToken.csv";
		
		if(filePath != null){
			
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				 String line;
				 String identity;
				 String tokenId;
				    while ((line = br.readLine()) != null) {
				    	line = line.replace("\"", "");
				    	String[] temp = line.split(",");
						
				    	tokenId = temp[0];
				    	identity = temp[1];  // Rubah Nanti sesuai Csv nya
				    	
				    	System.out.println("ini identitynya : " + identity + "  ini token ID nya : " + tokenId );
				    	
				    	if(identity.contains("cadangan")){
				    		System.out.println("Masuk");
				    	}
				
				    }
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("File Path Tidak Ditemukan");
		}
	}*/

}
