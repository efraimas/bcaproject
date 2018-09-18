package sailpoint.correlationconfig.rule;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.Base24Attribute;
import sailpoint.common.BcaConstantCode;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Link;
import sailpoint.object.ResourceObject;
import sailpoint.server.InternalContext;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Base24CorrelationRule {

	public static Logger logger = Logger
			.getLogger("sailpoint.correlationconfig.rule.Base24CorrelationRule");

	/**
	 * @param context
	 * @param environment
	 * @param application
	 * @param account
	 * @param link
	 * @return
	 * @throws GeneralException
	 */

	public static Map base24CorrelationRule_backup(InternalContext internalContext,
			Map environment, Application application, ResourceObject account,
			Link link) throws GeneralException {

		String METHOD_NAME = "::base24CorrelationRule::";
		logger.debug(METHOD_NAME + "Inside...");
		
		Map correlatedMap = new HashMap();
		SailPointContext context = internalContext.getContext();
		Attributes localAccountAttribute = account.getAttributes();
		
		if (localAccountAttribute.containsKey(Base24Attribute.NIP)) {
			String nip = CommonUtil.getEmployeeIdBase24(localAccountAttribute.getString(Base24Attribute.NIP));
			logger.debug(METHOD_NAME + "nip: " + nip);

			/*if(CommonUtil.isNotEmptyString(nip)){
				logger.debug(METHOD_NAME + "Returning Identity Map with employeeid: "
						+ nip);
				correlatedMap.put("identityAttributeName",IdentityAttribute.EMPLOYEE_ID);
				correlatedMap.put("identityAttributeValue",nip);
				return correlatedMap;
			}*/
			String employeeId = CommonUtil.searchIdentityId(context,
					IdentityAttribute.EMPLOYEE_ID, nip);
			if (!CommonUtil.isNotEmptyString(employeeId)) {
				logger.debug(METHOD_NAME + "No Identity Correlation Found...");
				return correlatedMap;
			} else {
				logger.debug(METHOD_NAME + "Returning Identity Map with employeeid: "
						+ employeeId);
				correlatedMap.put("identityAttributeName",IdentityAttribute.EMPLOYEE_ID);
				correlatedMap.put("identityAttributeValue",employeeId);
				return correlatedMap;
			}

		} else {
			logger.debug(METHOD_NAME + "NIP data not found for account "
					+ account.getIdentity() + "  returning nothing....");
			return correlatedMap;
		}

	}
	
	/**
	 * @param context
	 * @param environment
	 * @param application
	 * @param account
	 * @param link
	 * @return
	 * @throws GeneralException
	 */

	public static Map base24CorrelationRule(SailPointContext context,
			Map environment, Application application, ResourceObject account,
			Link link) throws GeneralException {

		String METHOD_NAME = "::base24CorrelationRule::";
		logger.debug(METHOD_NAME + "Inside...");
		
		Map correlatedMap = new HashMap();
		Attributes localAccountAttribute = account.getAttributes();

		if (localAccountAttribute.containsKey(Base24Attribute.NIP) && !localAccountAttribute.getString(Base24Attribute.NIP).contains("CADANGAN")){

			String nip = CommonUtil.getEmployeeIdBase24(localAccountAttribute.getString(Base24Attribute.NIP));
			
			logger.debug(METHOD_NAME + "nip: " + nip);

			if(CommonUtil.isNotEmptyString(nip)){
				logger.debug(METHOD_NAME + "Returning Identity Map with employeeid: "
						+ nip);
				correlatedMap.put("identityAttributeName",IdentityAttribute.EMPLOYEE_ID);
				correlatedMap.put("identityAttributeValue",nip);
				logger.debug(METHOD_NAME + "correlatedMap : " + correlatedMap);
				return correlatedMap;
			}else{
				logger.debug(METHOD_NAME + "Returning Identity Map system user");
				correlatedMap.put("identityAttributeName",IdentityAttribute.NAME);
				correlatedMap.put("identityAttributeValue", BcaConstantCode.BASE_24_SYSTEM_USER);
				logger.debug(METHOD_NAME + "correlatedMap : " + correlatedMap);
				return correlatedMap;
			}
		}else if(localAccountAttribute.containsKey(Base24Attribute.NAMA_USER) && localAccountAttribute.getString(Base24Attribute.NAMA_USER).contains("CADANGAN") && localAccountAttribute.containsKey(Base24Attribute.KODEC)){
			String branchCode = localAccountAttribute.getString(Base24Attribute.KODEC);
			String identityName = "CADANGAN " + branchCode;
			logger.debug(METHOD_NAME + " Memproses user cadangan dengan kode " + branchCode);
			
			correlatedMap.put("identityAttributeName",IdentityAttribute.NAME);
			correlatedMap.put("identityAttributeValue",identityName);
			logger.debug(METHOD_NAME + "correlatedMap : " + correlatedMap);
			return correlatedMap;
			
		}else if(!localAccountAttribute.containsKey(Base24Attribute.NIP)){
			logger.debug(METHOD_NAME + "Returning Identity Map system user");
			correlatedMap.put("identityAttributeName",IdentityAttribute.NAME);
			correlatedMap.put("identityAttributeValue", BcaConstantCode.BASE_24_SYSTEM_USER);
			logger.debug(METHOD_NAME + "correlatedMap : " + correlatedMap);
			return correlatedMap;
		} 
		else {
			logger.debug(METHOD_NAME + "NIP data not found for account "
					+ account.getIdentity() + "  returning nothing....");
			logger.debug(METHOD_NAME + "correlatedMap : " + correlatedMap);
			return correlatedMap;
		}
	}
	
}
