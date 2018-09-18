package sailpoint.provisioningpolicy.rule;

import sailpoint.api.SailPointContext;
import sailpoint.object.Identity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class FieldValueRuleLibrary {

	public static Log logger = LogFactory
			.getLog("sailpoint.provisioningpolicy.rule.FieldValueRuleLibrary");

	/**
	 * @param context
	 * @param identity
	 * @param appName
	 * @param fieldName
	 * @param op
	 * @return
	 * @throws Exception
	 */
	public static Object getDynamicFieldValueRule(SailPointContext context,
			Identity identity,sailpoint.object.Field field) throws Exception {
		
		String METHOD_NAME = "::getDynamicFieldValueRule::";
		logger.debug(METHOD_NAME + "Enter getDynamicFieldValueRule");
		logger.debug(METHOD_NAME + "Field XML: " + field.toXml());
		
		String template = field.getTemplate();
		String appName = field.getApplication();
		String fieldName = field.getName();
		String op = "Create";
		Object val = null;

		logger.debug(METHOD_NAME + "Get appName : " + appName);
		logger.debug(METHOD_NAME + "Get fieldName : " + fieldName);
		
		if (null != template) {
			
			logger.debug(METHOD_NAME + "Template is not null....");
			op = template;
			
			String aName = appName;
	
			if (null != appName) {
				logger.debug(METHOD_NAME + "Application Name is not null...");
				aName = appName.replaceAll(" ", "_");
				aName.trim();
				logger.debug(METHOD_NAME + "aName: " + aName);
				
		
				String fName = fieldName.replaceAll("-", "_");
				fName = fName.replaceAll(" ", "_");
				fName.trim();
				logger.debug(METHOD_NAME + "fName: " + fName);
				
				
				/*if("company".equalsIgnoreCase(fName)){ //This Condition add for testing force invoke method "to getFV_AD_BCA_company_Rule"
				
					String methodName = "getFV_" + aName + "_" + fName + "_Rule";
					methodName.trim();
					logger.debug(METHOD_NAME + "methodName: " + methodName);
				
					try {
						val = BCAMasterProvisioningLibrary.getFV_AD_BCA_company_Rule(context, identity, op);
						logger.debug("Inside Try ....");
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
						val = "CHECK CONFIG";
					}
					logger.debug("Val : " + val);
				}else if("manager".equalsIgnoreCase(fName)){
					try {
						val = BCAMasterProvisioningLibrary.getFV_AD_BCA_managerid_Rule(context, identity, op);
						logger.debug("Inside Try ....");
					} catch (Exception e){
						e.printStackTrace();
						logger.error(e.getMessage());
						val = "CHECK CONFIG";
					}
					logger.debug("Val : " + val);
				}else*/
					String methodName = "getFV_" + aName + "_" + fName + "_Rule";
					methodName.trim();
					logger.debug(METHOD_NAME + "methodName: " + methodName);
			
					Object[] params = { context, identity, op };
			
					try {
						logger.debug(METHOD_NAME + "Dynamically invoke method: "
								+ methodName + " with arguments: " + params);
			
						val = invokeMethod(methodName, params);
					} catch (Exception e) {
						logger.debug(METHOD_NAME + "Exception with value of field { "
								+ fieldName + " }, invoking method, " + methodName + ": "
								+ e); 
						e.printStackTrace();
						logger.error(e.getMessage());
						val = "CHECK CONFIG";
					}
			}
		}
		logger.debug(METHOD_NAME + "Exit getDynamicFieldValueRule: " + val);
		return val;
		
	}

	/**
	 * This is the method Invoker...
	 * 
	 * @param methodName
	 * @param params
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */

	public static Object invokeMethod(String methodName, Object[] params)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		String METHOD_NAME = "::invokeMethod::";
		String MASTER_PROVISIONING_LIBRARY = "sailpoint.provisioningpolicy.rule.BCAMasterProvisioningLibrary";

		logger.debug(METHOD_NAME + "Inside...");

		Class c = Class.forName(MASTER_PROVISIONING_LIBRARY);
		Object localObject = c.newInstance();

		Method[] allMethods = c.getDeclaredMethods();
		for (Method method : allMethods) {
			String localMethodName = method.getName();
			 logger.debug(METHOD_NAME + "localMethodName: " +
			 localMethodName);
			if (localMethodName.equalsIgnoreCase(methodName)) {
				logger.debug(METHOD_NAME
						+ "localMethodName which got invoked: "
						+ localMethodName);
				Object returnObject = method.invoke(localObject, params);
				logger.debug(METHOD_NAME + "Object Returned: " + returnObject);
				return returnObject;
			}
		}

		return null;
	}

}