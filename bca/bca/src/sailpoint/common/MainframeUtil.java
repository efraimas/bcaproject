package sailpoint.common;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.object.Bundle;
import sailpoint.tools.GeneralException;

public class MainframeUtil {
	
String className = "::MainframeUtil::";
	
	public static Logger logger = Logger
			.getLogger("sailpoint.common.MainframeUtil");
	
public static String getBcaMainframeApplication(SailPointContext ctx, String roleName) throws GeneralException{
		
		String methodName = "::getBcaMainframeApplication::";
		
		String applicationName = "";
		
		Bundle bundle = ctx.getObjectByName(Bundle.class, roleName);
		
		if(bundle != null){
			String displayName = bundle.getDisplayName();
			
			logger.debug(methodName + " displayName " + displayName + " displayableName " + bundle.getDisplayableName());
			
			if(displayName.indexOf("-") > 0){
				applicationName = displayName.substring(0, displayName.indexOf(" - "));
				
				logger.debug(methodName + " application Name " + applicationName);
			}
		}else{
			logger.debug(methodName + " tidak ada role dengan nama " + roleName);
		}
		
		logger.debug(methodName + " return value " + applicationName);
		
		return applicationName;
	}

}
