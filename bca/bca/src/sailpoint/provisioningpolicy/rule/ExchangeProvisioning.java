package sailpoint.provisioningpolicy.rule;

import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Identity;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class ExchangeProvisioning {
	
	public static String CLASS_NAME = "::ExchangeProvisioning::";

	public static Logger logger = Logger
			.getLogger("sailpoint.provisioningpolicy.rule.ExchangeProvisioning");
	
	public static String getHomeMdbDatabase(SailPointContext context,
			Identity identity, String type) throws GeneralException{
		String METHOD_NAME = "::getHomeMdbDatabase::";
		logger.debug(METHOD_NAME + "Inside...");
		
		//Map<?, ?> exchangeDbMap = getExchangeConfig(context, "database internal");

		logger.debug("Identity First Name " + (String)identity.getAttribute(IdentityAttribute.FIRST_NAME));
		String firstLetterName = ((String)identity.getAttribute(IdentityAttribute.FIRST_NAME)).substring(0, 1);
		logger.debug("First Letter " + firstLetterName);
		
		//database exchange should be like this
		//CN=BCA A-C,CN=Databases,CN=Exchange Administrative Group (FYDIBOHF23SPDLT),CN=Administrative Groups,CN=DevOrg,CN=Microsoft Exchange,CN=Services,CN=Configuration,DC=dti,DC=co,DC=id
		if("internal".equalsIgnoreCase(type))
			return (String)getExchangeConfig(context, "database internal");
		else if("eksternal".equalsIgnoreCase(type))
			return (String)((Map)getExchangeConfig(context, "database eksternal")).get(firstLetterName);
		else if("vip".equalsIgnoreCase(type))
			return  (String)getExchangeConfig(context, "database vip");
		else
			return "";
	}
	
	public static String getMailDomain(SailPointContext context,
			Identity identity, String type) throws GeneralException{
		String METHOD_NAME = "::getMailDatabase::";
		logger.debug(METHOD_NAME + "Inside...");
		
		if("internal".equalsIgnoreCase(type))
			return (String)getExchangeConfig(context, "internal domain");
		else if("eksternal".equalsIgnoreCase(type))
			return (String)getExchangeConfig(context, "eksternal domain");
		else if("vip".equalsIgnoreCase(type))
			return  (String)getExchangeConfig(context, "vip domain");
		else
			return "";
	}
	
	public static Object getExchangeConfig(SailPointContext context, String key) throws GeneralException{
		
		Object returnMap = null;
		
		Custom custom = CommonUtil.getCustomObject(context, CustomObject.BCA_EXCHANGE_CONFIG);
		
		Attributes<?, ?> attr = custom.getAttributes();
		
		Map<?, ?> customMap = attr.getMap();
		
		returnMap = customMap.get(key);
		
		return returnMap;
		
	}

}
