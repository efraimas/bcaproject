package sailpoint.provisioningpolicy.rule;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Identity;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BCAEmailAlias {
	
	public static String CLASS_NAME = "::BCAEmailAlias::";

	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.BCAEmailAlias");
	
	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @param type
	 * @return
	 * @throws GeneralException
	 */
	public static Object getFV_AD_CP_EmailAddressesEksternal_Rule(SailPointContext context, Identity identity) throws GeneralException{
		String METHOD_NAME = "::getFV_AD_CP_EmailAddressesEksternal_Rule::";
		logger.trace(METHOD_NAME + "Inside...");
		
		String val = "";
		String email  = (String)BCAEmailAlias.getValidEmailAddressIntra(context, (String)identity.getAttribute(IdentityAttribute.FIRST_NAME), 
				(String)identity.getAttribute(IdentityAttribute.MIDDLE_NAME), (String)identity.getAttribute(IdentityAttribute.LAST_NAME));
		try {
			val = email.toLowerCase() + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL;
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + "val : " + val);
		return val;
	}
	
	public static Object getFV_AD_CP_EmailAddressesInternal_Rule(SailPointContext context, Identity identity) throws GeneralException{
		String METHOD_NAME = "::getFV_AD_CP_EmailAddressesEksternal_Rule::";
		logger.trace(METHOD_NAME + "Inside...");
		
		String val = "";
		String email  = (String)BCAEmailAlias.getValidEmailAddressIntra(context, (String)identity.getAttribute(IdentityAttribute.FIRST_NAME), 
				(String)identity.getAttribute(IdentityAttribute.MIDDLE_NAME), (String)identity.getAttribute(IdentityAttribute.LAST_NAME));
		try {
			val = email.toLowerCase() + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL;
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + "val : " + val);
		return val;
	}
	
	public static Object getFV_AD_CP_proxyAddressesEksternal_Rule(SailPointContext context, Identity identity) throws GeneralException
	{
		String METHOD_NAME = "::getFV_AD_CP_proxyAddressesEksternal_Rule::";
		logger.trace(METHOD_NAME + "Inside...");
		
		List val = new ArrayList();
		
		String smtp  = "smtp:";
		String smtp2 = "smtp:";
		String smtp3 = "smtp:";
		
		String email  = (String)BCAEmailAlias.getValidEmailAddressIntra(context, (String)identity.getAttribute(IdentityAttribute.FIRST_NAME), 
				(String)identity.getAttribute(IdentityAttribute.MIDDLE_NAME), (String)identity.getAttribute(IdentityAttribute.LAST_NAME));
		
		String email2 = (String)BCAEmailAlias.getValidEmailAddress_Uxxxx(context, (String)identity.getAttribute(IdentityAttribute.EMPLOYEE_ID));
		
		smtp  = smtp  + email.toLowerCase() + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL;
		smtp2 = smtp2 +  email2 + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL;
		smtp3 = smtp3 +  email2 + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTRA;
		
		val.add(smtp);
		val.add(smtp2);
		val.add(smtp3);
		logger.debug(CLASS_NAME + METHOD_NAME + "val : " + val);
		
		return val;
	}
	
	public static Object getFV_AD_CP_proxyAddressesInternal_Rule(SailPointContext context, Identity identity) throws GeneralException
	{
		String METHOD_NAME = "::getFV_AD_CP_proxyAddressesInternal_Rule::";
		logger.trace(METHOD_NAME + "Inside...");
		
		List val = new ArrayList();
		
		String smtp  = "SMTP:";
		String smtp2  = "smtp:";
		
		String email  = (String)BCAEmailAlias.getValidEmailAddressIntra(context, (String)identity.getAttribute(IdentityAttribute.FIRST_NAME), 
				(String)identity.getAttribute(IdentityAttribute.MIDDLE_NAME), (String)identity.getAttribute(IdentityAttribute.LAST_NAME));
		String email2 = (String)BCAEmailAlias.getValidEmailAddress_Uxxxx(context, (String)identity.getAttribute(IdentityAttribute.EMPLOYEE_ID));
		
		smtp  = smtp  + email.toLowerCase() + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTRA;
		smtp2 = smtp2  + email2.toLowerCase() + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTRA;
		
		val.add(smtp);
		val.add(smtp2);
		logger.debug(CLASS_NAME + METHOD_NAME + "val : " + val);
		
		return val;
	}
	
	/**
	 * @param context
	 * @param employeeId
	 * @param domain
	 * @return
	 * @throws GeneralException
	 */
	
	public static Object getValidEmailAddress_Uxxxx(SailPointContext context, String employeeId) throws GeneralException{
		
		String emp = "u";
		
		if(employeeId!=null && employeeId.length() == 8 ){
		
		if(CommonUtil.isNotEmptyString(employeeId)  && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, employeeId + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL )){
			return emp + employeeId.substring(2, 8) ;
		}
		return "";
		
		}
		if(employeeId !=null && employeeId.length()== 6 ){
			if(CommonUtil.isNotEmptyString(employeeId)  && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, employeeId  + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
				return emp + employeeId ;
		}	
		return "";
		
		}
		
		return "";
	}
	
	public static Object getValidEmailAddressIntra(SailPointContext context, String firstname, String middlename, String lastname)  throws GeneralException {

		if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isNotEmptyString(lastname) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + "_" + lastname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
			return firstname + "_" + lastname ;
		}
		if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
			return firstname ;
		}
		
		if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isNotEmptyString(lastname) && CommonUtil.isNotEmptyString(middlename) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + "_" + middlename + "_" + lastname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
			return firstname + "_" + middlename + "_" + lastname ;
		}
		return "";
	}
	public static void main(String[]args) {
		String a = "A'FIR_RACHMAN";
		System.out.println(a.replace("'", ""));
	}

}
	
