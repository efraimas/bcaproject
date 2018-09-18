package sailpoint.provisioningpolicy.rule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.text.ParseException;

import org.apache.log4j.Logger;

import com.hxtt.sql.common.e;

import sailpoint.api.IdentityService;
import sailpoint.api.PasswordGenerator;
import sailpoint.api.Provisioner;
import sailpoint.api.RoleUtil;
import sailpoint.api.SailPointContext;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.BCAPasswordPolicyName;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.RACFProfile;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Bundle;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.QueryOptions;
import sailpoint.object.RoleAssignment;
import sailpoint.object.RoleTarget;
import sailpoint.rule.RefreshFieldNameRule;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes" })
public class BCAMasterProvisioningLibrary {
	
	public static String CLASS_NAME = "::BCAMasterProvisioningLibrary::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.BCAMasterProvisioningLibrary");

	public static String BLANK = "";

	// ///////////////////////////////////////////////////////////////////////
	// *** Active Directory Field Value Rules ***
	// ///////////////////////////////////////////////////////////////////////

	//Logon Name
	public static Object getFV_AD_BCA_userPrincipalName_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_userPrincipalName_Rule::";
		logger.debug(METHOD_NAME + "Inside...");
		
		String val = "";
		String PREFIX = "U";
		String employeeid = (String) identity.getAttribute(IdentityAttribute.EMPLOYEE_ID);
		String domain = "@dti";
		
		val = PREFIX + employeeid.substring(2); 
		
		if("false".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.IS_HR_MANAGED))){
			val = PREFIX + employeeid; 
		}
		val = val + domain;
			
		logger.debug(METHOD_NAME + "val: " + val);
		return val;
	}

	//Name
		public static Object getFV_AD_BCA_name_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_name_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String firstName = (String)identity.getAttribute(IdentityAttribute.FIRST_NAME);
			String middleName = (String)identity.getAttribute(IdentityAttribute.MIDDLE_NAME);
			String lastName = (String) identity.getAttribute(IdentityAttribute.LAST_NAME);
			String val = "";
						
			if(lastName!=null && lastName.length()>0){
				if(middleName!=null && middleName.length()>0){
					val = firstName + " " + middleName + " " + lastName;
					logger.debug(METHOD_NAME + "Display Name1: "+val);
				}
				else{
					val = firstName + " " + lastName;
					logger.debug(METHOD_NAME + "Display Name2: "+val);
				}
			}
			else{
				val = firstName;
				logger.debug(METHOD_NAME + "Display Name3: "+val);
			}
			return val.trim();
		}
		
		//sAMAccountName
		public static Object getFV_AD_BCA_sAMAccountName_Rule(
				SailPointContext context, Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_sAMAccountName_Rule	::";
			logger.debug(METHOD_NAME + "Inside...");

			
			
			String val = "";
			String PREFIX = "U";
			String employeeId = (String) identity.getAttribute(IdentityAttribute.EMPLOYEE_ID);
			
			val = PREFIX + employeeId.substring(2); 
			
			if("false".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.IS_HR_MANAGED))){
				val = PREFIX + employeeId; 
			}
			logger.debug(METHOD_NAME + "val: " + val);
			return val;
		}
	
		//employeeID
		public static Object getFV_AD_BCA_employeeID_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_employeeid_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = ((String)identity.getAttribute(IdentityAttribute.EMPLOYEE_ID)).trim();
			Integer empID = Integer.valueOf(val);
			logger.debug(METHOD_NAME + "EmployeeID already convert: " + empID);
			
			StringBuilder sb = new StringBuilder();
			sb.append("");
			sb.append(empID);
			String employeeID = sb.toString();
			
			if(identity.getAttribute(IdentityAttribute.IS_HR_MANAGED) == null 
					|| "false".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.IS_HR_MANAGED))){
				val = ((String)identity.getAttribute(IdentityAttribute.EMPLOYEE_ID)).trim();
			}
			return employeeID.trim();
		}
	
		//Display Name
		public static Object getFV_AD_BCA_displayName_Rule(
				SailPointContext context, Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_displayName_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String firstName = "";
			String middleName = "";
			String lastName = "";
			String val = "";
			
			firstName 	= (String) identity.getAttribute(IdentityAttribute.FIRST_NAME);
			middleName 	= (String) identity.getAttribute(IdentityAttribute.MIDDLE_NAME);
			lastName 	= (String) identity.getAttribute(IdentityAttribute.LAST_NAME);
			
			if(lastName!=null && lastName.length()>0){
				if(middleName!=null && middleName.length()>0){
					val = firstName + " " + middleName + " " + lastName;
					logger.debug(METHOD_NAME + "Display Name1: "+val);
				}
				else{
					val = firstName + " " + lastName;
					logger.debug(METHOD_NAME + "Display Name2: "+val);
				}
			}
			else{
				val = firstName;
				logger.debug(METHOD_NAME + "Display Name3: "+val);
			}
			return val.trim();
		}
		
		//First Name
		public static Object getFV_AD_BCA_givenName_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_givenName_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.FIRST_NAME);
			logger.debug(METHOD_NAME + "val: " + val);

			return val.trim();
		}
		
		//Middle Name
		public static Object getFV_AD_BCA_middleName_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_middlename_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.MIDDLE_NAME);
			logger.debug(METHOD_NAME + "val: " + val);

			return val.trim();
		}
		
		//Last Name
		public static Object getFV_AD_BCA_sn_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_sn_Rule	::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";

			if(identity.getAttribute(IdentityAttribute.LAST_NAME) != null){
				val = (String) identity.getAttribute(IdentityAttribute.LAST_NAME);
			}
			logger.debug(METHOD_NAME + "val: " + val);
			
			return val.trim();
		}
		
		//Department or Division Name
		public static Object getFV_AD_BCA_department_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_department_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			val = (String)identity.getAttribute(IdentityAttribute.DIVISION_NAME);
			
			if(val!=null && val.length()>0){
				logger.debug(METHOD_NAME + "val: " + val);
			}
			else{
				val = (String)identity.getAttribute(IdentityAttribute.BRANCH_NAME);
				logger.debug(METHOD_NAME + "Branch Name: " + val);
			}

			return val.trim();
		}

		//Company or Cost Center
		public static Object getFV_AD_BCA_company_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_company_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = null;
			val = (String)identity.getAttribute(IdentityAttribute.COST_CENTER);
			logger.debug(METHOD_NAME + "val: " + val);

			return val.trim();
		}
		
		//Country
		public static Object getFV_AD_BCA_co_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_co_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "Indonesia";

			logger.debug(METHOD_NAME + "val: " + val);

			return val.trim();
		}
		
		//Title or Position Name
		public static Object getFV_AD_BCA_title_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_title_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.POSITION_NAME);
			logger.debug(METHOD_NAME + "val: " + val);

			return val.trim();
		}
		
		//City
		public static Object getFV_AD_BCA_l_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_l_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.CITY);
			logger.debug(METHOD_NAME + "val: " + val);

			return val;
		}
		
		//Province
		public static Object getFV_AD_BCA_st_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_st_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.PROVINCE);
			logger.debug(METHOD_NAME + "val: " + val);

			return val.trim();
		}
		
		//CN or Salutation Name
		public static Object getFV_AD_BCA_cn_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_cn_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.SALUTATION_NAME);
			logger.debug(METHOD_NAME + "val: " + val);

			return val.trim();
		}
		
		//Job Description
		public static Object getFV_AD_BCA_description_Rule(
				SailPointContext context, Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_description_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			
			val = (String) identity.getAttribute(IdentityAttribute.JOB_DESCRIPTION);
			logger.debug(METHOD_NAME + "val: " + val);
			return val.trim();
		}
		
		//Telephone Number
		public static Object getFV_AD_BCA_telephoneNumber_Rule(
				SailPointContext context, Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_telephoneNumber_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			
			val = (String) identity.getAttribute(IdentityAttribute.TELEPHONE_NUMBER);
			logger.debug(METHOD_NAME + "val: " + val);
			return val.trim();
		}
		
		//Manager
		public static Object getFV_AD_BCA_manager_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_manager_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			String val = "";
			try{
			
				IdentityService idServ = new IdentityService(context);
				QueryOptions qo = new QueryOptions();
				Application app = null;
				qo.add(Filter.eq("name", "AD BCA"));
				Iterator<Application> it = context.search(Application.class, qo);
				if(it.hasNext()){
					 app = it.next();
					logger.debug("application found : " + app.getName());
				}else{
					logger.debug("application not found");
				}
				logger.debug("Application will be : " + app);
				Identity manager = identity.getManager();
		        List <Link> accountLinks = idServ.getLinks(manager, app); 
		        logger.debug("account links : " + accountLinks);
		        if(null != accountLinks && !accountLinks.isEmpty()){  
				    for(Link link : accountLinks){
				    	logger.debug("link : " + link.toXml());
				    	logger.debug(link.getDisplayName());
				    	logger.debug(manager.getDisplayName());
				    	
				    		try{
				    			val = link.getNativeIdentity();
				    			logger.debug("native identity ad bca : " + link.getNativeIdentity() );
				    		}catch(Exception e){
				    			logger.debug("attribute DN not found");
				    		}
				    }  
				}
				logger.debug(CLASS_NAME + METHOD_NAME + "Nilai : " + val);
			}catch (Exception e) {
				// TODO: handle exception
				logger.debug(CLASS_NAME + METHOD_NAME + "link not found");
				val = "";
			}

			return val;
		}
		
		private static String getValidNickname(SailPointContext context, String firstname, String middlename, String lastname)  throws GeneralException {

			if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isNotEmptyString(lastname) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + "_" + lastname)){
				return firstname + "_" + lastname;
			}
			if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname)){
				return firstname;
			}
			if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isNotEmptyString(lastname) && CommonUtil.isNotEmptyString(middlename) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + "_" + middlename + "_" + lastname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
				return firstname + "_" + middlename + "_" + lastname;
			}
			return "";
		}
		
		//Email Alias (mailNickname)
		public static Object getFV_AD_BCA_mailNickname_Rule(
				SailPointContext context, Identity identity, String op) throws GeneralException {
			String METHOD_NAME = "::getFV_AD_BCA_mailNickname_Rule::";
			logger.debug(METHOD_NAME + "Inside...");

			logger.debug(METHOD_NAME + "OP: " +op);
			
			String val = "";
			
			val = BCAMasterProvisioningLibrary.getValidNickname(context, (String)identity.getAttribute(IdentityAttribute.FIRST_NAME), 
					(String)identity.getAttribute(IdentityAttribute.MIDDLE_NAME), (String)identity.getAttribute(IdentityAttribute.LAST_NAME));
			
			logger.debug(METHOD_NAME + "val: " + val);
			return val.toLowerCase().replace("'", "");
		}
		
		//End Date
		public static Object getFV_AD_BCA_accountExpires_Rule(SailPointContext context,
				Identity identity, String op) {
			String METHOD_NAME = "::getFV_AD_BCA_accountExpires_Rule::";
			logger.debug(METHOD_NAME + "Inside...");
			long epoch = 0;
			String valEpoch = null;
			String val = (String) identity.getAttribute(IdentityAttribute.END_DATE);
			logger.debug(METHOD_NAME + "val:" + val);
			
			if(val!=null && val.length()==8){
				
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				try {
					date = df.parse(val);
					
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
				
				//process to convert to epoch date
				try {
					epoch = date.getTime();
					epoch = (epoch * 10000L) + 116444736000000000L;	
				}catch(Exception e) {
					logger.debug(METHOD_NAME + "failed to parse the date");
				}
				valEpoch = String.valueOf(epoch);
				
				if(!valEpoch.equalsIgnoreCase("0") && valEpoch != null) {
					logger.debug("value epoch : " + valEpoch);
					return valEpoch;
				}else {
					return null;
				}
				
			}
			else{
				return val;
			}
			
		}

	
	/**
	 * Set to 0 in AD (zero, this is an integer in AD)
	 * 
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_countryCode_Rule(
			SailPointContext context, Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_countryCode_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "0";

		logger.debug(METHOD_NAME + "val: " + val);

		return val;
	}

	/**
	 * Set to 0 in AD (zero, this is an integer in AD)
	 * 
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_codePage_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_codePage_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "0";
		logger.debug(METHOD_NAME + "val: " + val);

		return val;
	}

	public static Object getFV_AD_BCA_distinguishedName_Rule(
			SailPointContext context, Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_distinguishedName_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		String cnString = "";
		String dnString = "";
		String ouString = "000";

		Application adApplication;
		try {
			adApplication = context.getObjectByName(Application.class,
					CommonUtil.AD_APPLICATION);

			logger.debug(METHOD_NAME + "Application Name: "
					+ adApplication.getName());

			Attributes adAttribute = adApplication.getAttributes();
			logger.debug(METHOD_NAME + "Attribute Map: " + adAttribute.getMap());

			dnString = (String) adAttribute.get("searchDN");
			logger.debug(METHOD_NAME + "dnString: " + dnString);
			
			
		} catch (GeneralException e) {

			logger.debug(METHOD_NAME + "Stack Trace: " + e.getMessage());
		}
		if(null != identity.getAttribute(IdentityAttribute.REGION_CODE) && ((String)identity.getAttribute(IdentityAttribute.REGION_CODE)).trim().length() > 1)
		
		if(!"0998".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_CODE))){
			
			if("WILAYAH I".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil1" + ",ou=KantorCabang,";
			}
			else if("WILAYAH II".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil2" + ",ou=KantorCabang,";
			}
			else if("WILAYAH III".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil3" + ",ou=KantorCabang,";
			}
			else if("WILAYAH IV".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil4" + ",ou=KantorCabang,";
			}
			else if("WILAYAH V".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil5" + ",ou=KantorCabang,";
			}
			else if("WILAYAH VI".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil6" + ",ou=KantorCabang,";
			}
			else if("WILAYAH VII".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil7" + ",ou=KantorCabang,";
			}
			else if("WILAYAH VIII".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil8" + ",ou=KantorCabang,";
			}
			else if("WILAYAH IX".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil9" + ",ou=KantorCabang,";
			}
			else if("WILAYAH X".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil10" + ",ou=KantorCabang,";
			}
			else if("WILAYAH XI".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil11" + ",ou=KantorCabang,";
			}
			else if("WILAYAH XII".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.REGION_NAME))){
				ouString = ",ou=" + "Kanwil12" + ",ou=KantorCabang,";
			}
			
		}
		else{
			ouString = ",ou=KantorPusat,";
		}	
		
		String firstName = (String) identity.getAttribute(IdentityAttribute.FIRST_NAME);
		String lastName = (String)identity.getAttribute(IdentityAttribute.LAST_NAME);
		String middleName = (String)identity.getAttribute(IdentityAttribute.MIDDLE_NAME);
		
		if(lastName != null){
			if(middleName !=null){
				cnString = firstName + " " + middleName + " " + lastName;
			}
			else{
				cnString = firstName + " " + lastName;	
			}
		}
		else{
			cnString = firstName;
		}
		
		//for DEV and UAT 
		dnString = "DC=dti,DC=co,DC=id";
		//dnString = "DC=intra,DC=bca,DC=co,DC=id";
		logger.debug(METHOD_NAME + "Ini domain : " + dnString);
		
		val = "cn=" + cnString + ouString + dnString;
		val.trim();
		logger.debug(METHOD_NAME + "val: " + val);

		return val;
	}

	/**
	 * By default the password policy name is hard coded into the Common Utility
	 * class... any change in the password policy name in IIQ should also
	 * reflect in the common utility class....
	 * 
	 * 
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 * @throws GeneralException
	 */
	public static Object getFV_AD_BCA_password_Rule(SailPointContext context,
			Identity identity, String op) throws GeneralException {
		String METHOD_NAME = "::getFV_AD_BCA_password_Rule	::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String val = "";

		val = CommonUtil.generatePassword(context,
				CommonUtil.BCA_AD_PASSWORD_POLICY_NAME);
		logger.debug(CLASS_NAME + METHOD_NAME + "Returning Password value....");

		return val;
	}
	
	public static void updateAdPassword(SailPointContext con, Identity employee, String newPassword, String identityName){
		
		String METHOD_NAME = "::updateAdPassword	::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		AccountRequest accReq = new AccountRequest();
		
		accReq.setApplication(CommonUtil.AD_APPLICATION);
		accReq.setOperation(AccountRequest.Operation.Modify);
		
		accReq.add(new AttributeRequest("sAMAccountName",ProvisioningPlan.Operation.Set,identityName));  
		accReq.add(new AttributeRequest("ObjectType",ProvisioningPlan.Operation.Set,"User"));  
		accReq.add(new AttributeRequest("*password*",ProvisioningPlan.Operation.Set,newPassword));
		
		ProvisioningPlan plan = new ProvisioningPlan();
		
		logger.debug(METHOD_NAME + " add account request to provisioning plan");
		
		plan.add(accReq);
		
		logger.debug(METHOD_NAME + " add identity to provisioning plan");
		
		plan.setIdentity(employee);
		
		Provisioner provisioner = new Provisioner(con);
		
		try {
			provisioner.execute(plan);
			logger.debug(METHOD_NAME + " success to update password");
		} catch (GeneralException e) {
			logger.debug(METHOD_NAME + " fail to update password");
			e.printStackTrace();
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + " Done...");
		
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_primaryGroupDN_Rule(
			SailPointContext context, Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_primaryGroupDN_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		// TODO Implement it.... Get the details from yann on what should be the
		// value.....

		return val;
	}

	/**
	 * The method will check if the identity have an HR application link or
	 * not.. if its present it will return true.. else flase...
	 * 
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 * @throws GeneralException
	 */
	@SuppressWarnings("deprecation")
	public static Object getFV_AD_BCA_isHRManaged_Rule(
			SailPointContext context, Identity identity, String op)
			throws GeneralException {
		String METHOD_NAME = "::getFV_AD_BCA_isHRManaged_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		Application application = context.getObjectByName(Application.class,
				CommonUtil.HR_APPLICATION);
		logger.debug(METHOD_NAME + "Application Object: "
				+ application.getName());

		if (identity.getLink(application) != null) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_dob_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_dob_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";

		val = (String) identity.getAttribute(IdentityAttribute.DATE_OF_BIRTH);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_status_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_status_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		
		val = (String) identity.getAttribute(IdentityAttribute.STATUS);
		logger.debug(METHOD_NAME + "val: " + val);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_subdivisionCode_Rule(
			SailPointContext context, Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_subdivisionCode_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_subdivisionName_Rule(
			SailPointContext context, Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_subdivisionName_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		
		val = (String)identity.getAttribute(IdentityAttribute.SUBDIVISION_NAME);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_doj_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_doj_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.DATE_OF_JOINING);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_jobCode_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_jobCode_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.JOB_CODE);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_branchCode_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_branchCode_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.BRANCH_CODE);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_branchName_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_branchName_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.BRANCH_NAME);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_regionCode_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_regionCode_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.REGION_CODE);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_regionName_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_regionName_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.REGION_CODE);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_positionCode_Rule(
			SailPointContext context, Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_positionCode_Rules::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.POSITION_CODE);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_positionName_Rule(
			SailPointContext context, Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_positionName_Rules::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.POSITION_NAME);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_echelon_Rule(SailPointContext context,
			Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_echelon_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.ECHELON);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_divisionCode_Rule(
			SailPointContext context, Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_divisionCode_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.DIVISION_CODE);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 */
	public static Object getFV_AD_BCA_divisionName_Rule(
			SailPointContext context, Identity identity, String op) {
		String METHOD_NAME = "::getFV_AD_BCA_divisionName_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		val = (String)identity.getAttribute(IdentityAttribute.DIVISION_NAME);

		return val;
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 * @throws GeneralException
	 */
	public static Object getFV_AD_BCA_userpassword_Rule(
			SailPointContext context, Identity identity, String op)
			throws GeneralException {
		String METHOD_NAME = "::getFV_AD_BCA_userpassword_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String password = "P@ssw0rd01"; // default password if the rule fails
		if (context != null) {

			Application app = context.getObjectByName(Application.class, CommonUtil.AD_APPLICATION); // application is always defined
			System.out.println(" app? " + (app != null));
			if (app != null) {
				List listPolicies = app.getPasswordPolicies();
				System.out.println(" app/pwd-policies=" + app.getName() + "/"
						+ listPolicies.size());
				if (listPolicies.size() > 0) {
					Identity id = context.getObjectByName(Identity.class,
							identity.getName());
					if (id != null) {
						System.out.println(" id=" + id.getName());
						PasswordGenerator pg = new PasswordGenerator(context);
						String pgPass = pg.generatePassword(id, app);
						if ((pgPass != null) && (pgPass.length() != 0)) {
							password = pgPass;
						}
					}
				}
			}
		}
		
		
		return password;

	}
	

	// ///////////////////////////////////////////////////////////////////////
	// *** Exchange Field Value Rules ***
	// ///////////////////////////////////////////////////////////////////////
	
	public static Object getFV_AD_BCA_mail_Rule(SailPointContext context,
			Identity identity, String op, String type) throws GeneralException {
		String METHOD_NAME = "::getFV_AD_BCA_mail_Rule::";
		logger.debug(METHOD_NAME + "Inside...");

		String val = "";
		
		val = RefreshFieldNameRule.getValidEmailAddress(context, (String)identity.getAttribute(IdentityAttribute.FIRST_NAME), 
				(String)identity.getAttribute(IdentityAttribute.MIDDLE_NAME), (String)identity.getAttribute(IdentityAttribute.LAST_NAME), type);
		
		logger.debug(METHOD_NAME + "val: " + val);


		return val!= null ? val.toLowerCase() : "";
	}

	/**
	 * @param context
	 * @param identity
	 * @param op
	 * @return
	 * @throws GeneralException 
	 */
	public static Object getFV_AD_BCA_homeMDB_Rule(SailPointContext context,
			Identity identity, String op) throws GeneralException {
		String METHOD_NAME = "::getFV_AD_BCA_homeMDB_Rule::";
		logger.debug(METHOD_NAME + "Inside...");
		
		Map exchangeDbMap = (Map)ExchangeProvisioning.getExchangeConfig(context, "database internal");
		logger.debug("Map size " + exchangeDbMap.size());
		logger.debug("Identity First Name " + (String)identity.getAttribute(IdentityAttribute.FIRST_NAME));
		String firstLetterName = ((String)identity.getAttribute(IdentityAttribute.FIRST_NAME)).substring(0, 1);
		logger.debug("First Letter " + firstLetterName);  
		
		//database exchange should be like this
		//CN=BCA A-C,CN=Databases,CN=Exchange Administrative Group (FYDIBOHF23SPDLT),CN=Administrative Groups,CN=DevOrg,CN=Microsoft Exchange,CN=Services,CN=Configuration,DC=dti,DC=co,DC=id
		
		return exchangeDbMap.get(firstLetterName);
	}


	// ///////////////////////////////////////////////////////////////////////
		// *** Mainframe RACF Field Value Rules ***
		// ///////////////////////////////////////////////////////////////////////

		/**
		 * This method will be called by all USER ID Creation rule present in role
		 * provisioning profile...
		 * 
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @param profileName
		 * @return
		 * @throws GeneralException
		 */

	@SuppressWarnings("finally")
	public static Object getFV_IBM_MAINFRAME_RACF_USER_ID_Rule(
			SailPointContext context, Identity identity, String op,
			String profileName, ProvisioningProject project) throws GeneralException {

		String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_USER_ID_Rule::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		logger.debug(CLASS_NAME + METHOD_NAME + "Identity Name: "
				+ identity.getDisplayName() + "  profileName: " + profileName
				+ " Operation: " + op);
		
		/**
		 * To check provisioning project from lcm or no, if not from lcm, this method should not called, start
		 * */
		
		boolean isIncrement = false;
		
		logger.debug(METHOD_NAME + CLASS_NAME + project.toXml());
		
		ProvisioningPlan plan = project.getMasterPlan();
	
		
		if(plan != null){
			
			@SuppressWarnings("deprecation")
			AccountRequest planAccReq = plan.getAccountRequest("IIQ");
			
			logger.debug(CLASS_NAME + METHOD_NAME + " get Master Plan IIQ Account Request");
			
			if(planAccReq != null){
				
				Attributes attrAccReq = planAccReq.getArguments();
				
				logger.debug(CLASS_NAME + METHOD_NAME + " get IIQ Account Request Arguments");
				
				if(attrAccReq != null){
					
					Map attrAccReqMap = attrAccReq.getMap();
					
					logger.debug(CLASS_NAME + METHOD_NAME + " get IIQ Account Request Arguments Map");
					
					if(attrAccReq != null && attrAccReqMap.get("interface") != null && "LCM".equalsIgnoreCase((String)attrAccReqMap.get("interface"))){
						isIncrement = true;
					}
					
				}
			}
		}
		
		
		
		/*if(planMap.get("lcmUser") != null){
			logger.debug(CLASS_NAME + METHOD_NAME + " this request not from lcm");
			isIncrement = false;
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + " this request from lcm");*/
		
		/**
		 * End 
		 * */
		
		
		/**
		 * To check service type from permittedBy attribute on account request, start
		 * */
		
		String serviceType = getRoleServiceType(project);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " service type " + serviceType);
						
		/**
		 * End
		 * */
		
		
		/**
		 * Custom Object for Kantor Kas is different with other service type, so it's defined different with others, start
		 * */
		String customObjectName = serviceType + " SEQ";
		
		boolean isKantorKas = serviceType.indexOf("Kantor Kas Tipe I") >= 0;
		boolean isBDSWEB = serviceType.indexOf("BDS Web") >= 0;
		boolean isBCABIZZ = serviceType.indexOf("BCA BIZZ") >= 0;
		boolean isReguler = serviceType.indexOf("Reguler") >= 0;
		boolean isSolitaire = serviceType.indexOf("Solitaire") >= 0;
		boolean isPrioritas = serviceType.indexOf("Prioritas") >= 0;
		boolean isWeekendBanking = serviceType.indexOf("Weekend Banking") >= 0;
		boolean isVideoBanking = serviceType.indexOf("Video Banking") >= 0;

		
		logger.debug(CLASS_NAME + METHOD_NAME + " kantor Kas Status : " + isKantorKas + " BDS Web Status : " + isBDSWEB
			       + " BCA BIZZ Status : " + isBCABIZZ + " BCA Reguler Status : " + isReguler + " BCA Solitaire Status : " + isSolitaire
			       + " BCA Prioritas status : " + isPrioritas + " BCA Weekend Banking Status : " + isWeekendBanking
			       + " BCA Video Banking Status : " + isVideoBanking + " profilenya : " + profileName );
	
		
		if("0998".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "9650".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "0987".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "0986".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "0973".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "0970".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "0969".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "0968".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "0965".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "0959".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))
				|| "0958".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.BRANCH_CODE))){
			//customObjectName = "BCA Kantor Pusat Sequence";
			logger.debug(CLASS_NAME + METHOD_NAME + " Sequence full");
			throw new GeneralException("KANTOR PUSAT TIDAK DIPERBOLEHKAN REQUEST AKSES INI");
		}else if(isKantorKas && RACFProfile.CUSTOMER_SERVICE_CSO.equalsIgnoreCase(profileName)){
			customObjectName = serviceType + " CSO SEQ";
		}else if(isKantorKas && (RACFProfile.TELLER_DFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGFO.equalsIgnoreCase(profileName))){
			customObjectName = serviceType + " Teller SEQ";
		}
		else if(isBCABIZZ && (RACFProfile.SUPERVISOR_SG.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_ST.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_SD.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STG.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_SGD.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STD.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STGD.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_SCSO.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STGDC.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STGC.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Final Letter S";
		}
		else if (isReguler && (RACFProfile.SUPERVISOR_ST.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_SD.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STG.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_SGD.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STD.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STGD.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_SCSO.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STGDC.equalsIgnoreCase(profileName) 
				|| RACFProfile.SUPERVISOR_STGC.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_SG.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Final Letter S";
		}
		else if(isSolitaire && (RACFProfile.IDS_USER_PROFILE_SUPERVISOR_ST_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STD_PRIORITAS.equalsIgnoreCase(profileName)	
				|| RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STG_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGC_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGD_PRIORITAS.equalsIgnoreCase(profileName)	
				|| RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGDC_PRIORITAS.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Reguler SEQ";
		}
		else if (isReguler && (RACFProfile.IDS_REGULER_SUPERVISOR_STGDC_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_STGC_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_STGD_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_STD_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_STG_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_STGDC_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_STGC_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_STGD_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_STD_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_SUPERVISOR_STG_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Seq K dan L";
		}
		else if (isSolitaire && (RACFProfile.IDS_SOLITAIRE_SUPERVISOR_ST_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STD_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STG_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STGD_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STGC_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STGDC_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Seq K dan L";
		}
		else if (isPrioritas && (RACFProfile.IDS_PRIORITAS_SUPERVISOR_ST_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_PRIORITAS_SUPERVISOR_STD_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_PRIORITAS_SUPERVISOR_STG_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_PRIORITAS_SUPERVISOR_STGD_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_PRIORITAS_SUPERVISOR_STGC_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_PRIORITAS_SUPERVISOR_STGDC_INQ_REQ_NASABAH_PRIORITAS.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Seq K dan L";
		}
		else if(isPrioritas && (RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGDC_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGD_PRIORITAS.equalsIgnoreCase(profileName)	
				|| RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGC_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STG_PRIORITAS.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_USER_PROFILE_SUPERVISOR_ST_PRIORITAS.equalsIgnoreCase(profileName)
				)){
			customObjectName = "IDS Service Type - Reguler SEQ";
		}
		else if (isWeekendBanking && (RACFProfile.TELLER_TGFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_DFO.equalsIgnoreCase(profileName)
				|| RACFProfile.CASH_VAULT_CV.equalsIgnoreCase(profileName)
				|| RACFProfile.CUSTOMER_SERVICE_CSO.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_WEEKEND_BANKING_USER_PROFILE_INQUIRY_INQ.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Weekend Banking SEQ";
		}
		//untuk id T satu sequence
		else if (isReguler && (RACFProfile.SUPERVISOR_SG.equalsIgnoreCase(profileName) 
				|| RACFProfile.OPERATOR_GBO.equalsIgnoreCase(profileName) 
				|| RACFProfile.OPERATOR_TBO.equalsIgnoreCase(profileName) 
				|| RACFProfile.OPERATOR_DBO.equalsIgnoreCase(profileName) 
				|| RACFProfile.OPERATOR_GFBO.equalsIgnoreCase(profileName) 
				|| RACFProfile.OPERATOR_TFBO.equalsIgnoreCase(profileName) 
				|| RACFProfile.OPERATOR_DFBO.equalsIgnoreCase(profileName) 
				|| RACFProfile.OPERATOR_GDBO.equalsIgnoreCase(profileName) 
				|| RACFProfile.OPERATOR_TGBO.equalsIgnoreCase(profileName) 
				|| RACFProfile.OPERATOR_TDBO.equalsIgnoreCase(profileName)
				|| RACFProfile.OPERATOR_GDFBO.equalsIgnoreCase(profileName)
				|| RACFProfile.OPERATOR_TGFBO.equalsIgnoreCase(profileName)
				|| RACFProfile.OPERATOR_TGDBO.equalsIgnoreCase(profileName)
				|| RACFProfile.OPERATOR_TGDFBO.equalsIgnoreCase(profileName)
				|| RACFProfile.CASH_VAULT_CV.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_DFO.equalsIgnoreCase(profileName)
				|| RACFProfile.CUSTOMER_SERVICE_CSO.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_USER_PROFILE_INQUIRY_INQ.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_TELLER_GFO.equalsIgnoreCase(profileName)
				|| RACFProfile.IDS_REGULER_TELLER_TFO.equalsIgnoreCase(profileName))){
			customObjectName = "BCA_CxxxyyyT_Sequence";
		}
		else if (isBDSWEB && (RACFProfile.CASH_VAULT_CV.equalsIgnoreCase(profileName)
				|| RACFProfile.CUSTOMER_SERVICE_CSO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - BDS Web SEQ";
		}
		else if (isBCABIZZ && (RACFProfile.CASH_VAULT_CV.equalsIgnoreCase(profileName)
				|| RACFProfile.CUSTOMER_SERVICE_CSO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - BCA BIZZ SEQ";
		}
		else if (isPrioritas && (RACFProfile.CASH_VAULT_CV.equalsIgnoreCase(profileName)
				|| RACFProfile.CUSTOMER_SERVICE_CSO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Prioritas SEQ";
		}
		else if (isSolitaire && (RACFProfile.CASH_VAULT_CV.equalsIgnoreCase(profileName)
				|| RACFProfile.CUSTOMER_SERVICE_CSO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Solitaire SEQ";
		}
		else if (isVideoBanking && (RACFProfile.CASH_VAULT_CV.equalsIgnoreCase(profileName)
				|| RACFProfile.CUSTOMER_SERVICE_CSO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Video Banking SEQ";
		}
		else if (isBDSWEB && (RACFProfile.TELLER_TGFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_DFO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - BDS Web SEQ";
		}
		else if (isBCABIZZ && (RACFProfile.TELLER_TGFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_DFO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - BCA BIZZ SEQ";
		}
		else if (isPrioritas && (RACFProfile.TELLER_TGFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_DFO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Prioritas SEQ";
		}
		else if (isSolitaire && (RACFProfile.TELLER_TGFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_DFO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Solitaire SEQ";
		}
		else if (isSolitaire && (RACFProfile.SOLITAIRE_SUPERVISOR_STGDC.equalsIgnoreCase(profileName)
				|| RACFProfile.SOLITAIRE_SUPERVISOR_STGD.equalsIgnoreCase(profileName)
				|| RACFProfile.SOLITAIRE_SUPERVISOR_STGC.equalsIgnoreCase(profileName)
				|| RACFProfile.SOLITAIRE_SUPERVISOR_STG.equalsIgnoreCase(profileName)
				|| RACFProfile.SOLITAIRE_SUPERVISOR_STD.equalsIgnoreCase(profileName)
				|| RACFProfile.SOLITAIRE_SUPERVISOR_ST.equalsIgnoreCase(profileName)
				|| RACFProfile.SOLITAIRE_SUPERVISOR_SGD.equalsIgnoreCase(profileName)
				|| RACFProfile.SOLITAIRE_SUPERVISOR_SG.equalsIgnoreCase(profileName)
				|| RACFProfile.SOLITAIRE_SUPERVISOR_SD.equalsIgnoreCase(profileName)
				|| RACFProfile.SOLITAIRE_SUPERVISOR_SCSO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Seq K dan L";
		}
		else if (isVideoBanking && RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110.equalsIgnoreCase(profileName)){
			customObjectName = "IDS Service Type - Final Letter S";
		}
		else if (isWeekendBanking && RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110.equalsIgnoreCase(profileName)){
			customObjectName = "IDS Service Type - Final Letter S";
		}
		else if(isVideoBanking && RACFProfile.IDS_VIDEO_BANKING_USER_PROFILE_INQUIRY_INQ.equalsIgnoreCase(profileName)){
			customObjectName = "IDS Service Type - Video Banking SEQ";
		}
		else if (isPrioritas && RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110.equalsIgnoreCase(profileName)){
			customObjectName = "IDS Service Type - Final Letter S";
		}
		else if (isVideoBanking && (RACFProfile.TELLER_TGFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TGDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_TFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_GDFO.equalsIgnoreCase(profileName)
				|| RACFProfile.TELLER_DFO.equalsIgnoreCase(profileName))){
			customObjectName = "IDS Service Type - Video Banking SEQ";
		}
		else if(isSolitaire &&  RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110.equalsIgnoreCase(profileName)){
			customObjectName = "IDS Service Type - Final Letter S";
		}
		
		else if(isBDSWEB &&  profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SG)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_ST)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STG)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGC)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGDC)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SCSO)){
			customObjectName = "IDS Service Type - Final Letter S";
		}
		else if(isBCABIZZ &&  profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110)){
			customObjectName = "IDS Service Type - Final Letter S";
		}
		else if(isPrioritas && profileName.equals(RACFProfile.IDS_PRIORITAS_USER_PROFILE_INQUIRY_INQ)){
			customObjectName = "IDS Service Type - Prioritas SEQ";
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + " customObjectName : " + customObjectName);
		
		/**
		 * End
		 * */
		
		String val = "";
		// final String PREFIX_CONSTANT = "B"; // We are only handling one RACF
		// profile right now hence making
		// this as constant... in later
		// phase.. this might get changed...
		String prefix = "";

		String finalLetter = "";
		
		logger.debug(METHOD_NAME + profileName + " preparation to get Role Request");
		
		String branchCode = (String) identity
				.getAttribute(IdentityAttribute.BRANCH_CODE);
		
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code: " + branchCode);

		// Checking if the branch code value is null or blank...
		if (branchCode == null || branchCode.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "Branch Code for this identity is empty.. Please provide branchCode to generate RACF USER ID....");
			return val;
		}

		// Call the method to get the UserID prefix portion from the branch
		// code...
		prefix = RACFProvisioning.getUserIDFromBranchCode(context, branchCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "Prefix: " + prefix);
		
		logger.debug(CLASS_NAME + METHOD_NAME + "profileName: " + profileName);
		
		String positionCode = (String)identity.getAttribute(IdentityAttribute.POSITION_CODE);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " positionCode: " + positionCode);

		finalLetter = RACFProvisioning.getFinalLetterFromRequesterType(context,
				profileName, positionCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "finalLetter: " + finalLetter);
		
		try{
			val = ServiceTypeIDSProvisioning.getNextSequence(context, customObjectName, prefix, finalLetter, isIncrement, profileName);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			logger.debug(CLASS_NAME + METHOD_NAME + "Unique USER ID Found: " + val);

			return val;
		}
	}

		/**
		 * The method should return the NAME field what IIQ got from HR Feed....
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_IBM_MAINFRAME_RACF_NAME_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_NAME_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			String firstName = (String) identity.getAttribute(IdentityAttribute.FIRST_NAME);
			String lastName = (String)identity.getAttribute(IdentityAttribute.LAST_NAME);
			String middleName = (String)identity.getAttribute(IdentityAttribute.MIDDLE_NAME);
			
			if(CommonUtil.isNotEmptyString(firstName)){
				val += firstName;
			}
			
			if(CommonUtil.isNotEmptyString(middleName)){
				val += " " + middleName;
			}
			
			if(CommonUtil.isNotEmptyString(lastName)){
				val += " " + lastName;
			}
			
			//Max length for field name in Mainframe is 20
			if(val.length() > 20){
				val = val.substring(0, 20);
			}
			
			logger.debug(CLASS_NAME + METHOD_NAME + "Name Field Returning: " + val);

			return val;

		}

		/**
		 * Method to compute the default group.... as part of phase 1 the value is
		 * constant... which is ....
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_IBM_MAINFRAME_RACF_UG_DEF_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_UG_DEF_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			
			String branchCode = (String)identity.getAttribute(IdentityAttribute.BRANCH_CODE);
			
			if("0205".equalsIgnoreCase(branchCode) || "205".equalsIgnoreCase(branchCode)){ //Untuk KCK
				return "IBSWIL13";
			}

			String regionCode = (String) identity
					.getAttribute(IdentityAttribute.REGION_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "Region Code is " + regionCode
					+ " for identity " + identity.getDisplayName());

			if (regionCode == null || regionCode.equalsIgnoreCase("")) {
				logger.debug(CLASS_NAME
						+ METHOD_NAME
						+ "Region Code is Null for Identity: "
						+ identity.getDisplayName()
						+ " hence cannot compute the UG_DEF attribute value.... returning blank.......");
				return val;
			}

			val = RACFProvisioning.getMFGroupFromRegionCode(context, regionCode);
			logger.debug(CLASS_NAME + METHOD_NAME + "mfGroupName: " + val);

			return val;

		}

		/**
		 * The password is generated based on the Password Policy Defined for
		 * Mainframe.... if you need to change the policy go to IIQ UI Screen to
		 * make it change....
		 * 
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_IBM_MAINFRAME_RACF_password_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_password_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";

			try {
				val = CommonUtil.generatePassword(context,
						BCAPasswordPolicyName.RACF_PASSWORD_POLICY_NAME);
				String temp_displayName = (String)identity.getDisplayName();
				logger.debug(CLASS_NAME
						+ METHOD_NAME
						+ "Password Generated Successfully, for user " + temp_displayName + " is :" + val);
			} catch (GeneralException e) {
				logger.debug(METHOD_NAME + "Error Caught: " + e.getMessage());
				e.printStackTrace();
			}

			return val;

		}

		/**
		 * This is same value as the default group....
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_IBM_MAINFRAME_RACF_OWNER_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_OWNER_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			val = (String) BCAMasterProvisioningLibrary
					.getFV_IBM_MAINFRAME_RACF_UG_DEF_Rule(context, identity, op);
			logger.debug(CLASS_NAME + METHOD_NAME + "Value: " + val);

			return val;

		}
		
		public static Object getFV_IBM_MAINFRAME_RACF_groups_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_groups_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			val = (String) BCAMasterProvisioningLibrary
					.getFV_IBM_MAINFRAME_RACF_UG_DEF_Rule(context, identity, op);
			logger.debug(CLASS_NAME + METHOD_NAME + "Value: " + val);

			return val;

		}
		

		/**
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_IBM_MAINFRAME_RACF_DATA_Rule(  ///
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_DATA_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String branchCode = "";
			String branchName = "";
			String regionCode = "";

			String employeeID = "";
			String dob = "";
			String doj = "";
			String jobDesc = "";
			String firstName = "";
			
			String val = "";

			final String HQ_REGION_CODE = "998";
			final String EIGHT_SPACES = "        ";
			final String NIP = "NIP: ";
			final String LAHIR = "LAHIR: ";
			final String MASUK = "MASUK: ";
			final String SPACE = " ";
			final String DOUBLE_SPACE = "  ";
			final String DASH = "-";

			// initializing dob and doj with six spaces... just in case....
			dob = EIGHT_SPACES;
			doj = EIGHT_SPACES;

			boolean isBranchEmployee = false;
			boolean isRegionalHQEmployee = false;
			boolean isHQEmployee = false;
			
			branchCode = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...Branch Code" + branchCode);
			
			regionCode = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside... Region Code" + regionCode);

			employeeID = CommonUtil.getShortEmployeeId((String) identity.getAttribute(IdentityAttribute.EMPLOYEE_ID));
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside... EmployeeID" + employeeID);
			
			firstName = (String) identity.getAttribute(IdentityAttribute.FIRST_NAME);
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside... Firstname" + firstName);
			
			if((String)identity.getAttribute(IdentityAttribute.JOB_DESCRIPTION) == null || "".equalsIgnoreCase((String) identity.getAttribute(IdentityAttribute.JOB_DESCRIPTION))){
				jobDesc = "";
				dob = getMainFrameDateFormatNonEmployee((String) identity.getAttribute(IdentityAttribute.DATE_OF_BIRTH));
				doj = getMainFrameDateFormatNonEmployee((String) identity.getAttribute(IdentityAttribute.DATE_OF_JOINING));
			}else{
				jobDesc = (String)identity.getAttribute(IdentityAttribute.JOB_DESCRIPTION);
				dob = getMainFrameDateFormat((String) identity.getAttribute(IdentityAttribute.DATE_OF_BIRTH));
				doj = getMainFrameDateFormat((String) identity.getAttribute(IdentityAttribute.DATE_OF_JOINING));
			}
			
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside... Job Description" + jobDesc);
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside... Dob" + dob);
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside... Doj" + doj);

			if (HQ_REGION_CODE.equalsIgnoreCase(branchCode)) {
				isHQEmployee = true;
				isRegionalHQEmployee = false;
				isBranchEmployee = false;
			} else if (branchCode.equalsIgnoreCase(regionCode)) {
				isRegionalHQEmployee = true;
				isHQEmployee = false;
				isBranchEmployee = true;

			} else {
				isBranchEmployee = true;
				isHQEmployee = false;
				isRegionalHQEmployee = false;
			}

			if (isBranchEmployee && !"CADANGAN".equalsIgnoreCase(firstName)) {
				logger.debug(CLASS_NAME + METHOD_NAME + "Identity : "+ identity.getDisplayName() + " is a branch employee....");
				// Checking branch code length to be exact 4... if not take
				// action...
				if ("".equalsIgnoreCase(jobDesc)){
					val = branchCode + SPACE + NIP + employeeID + SPACE + LAHIR + SPACE + dob + SPACE + MASUK + SPACE + doj;
					logger.debug(CLASS_NAME + METHOD_NAME + "USER ID TIPE SATU : " + val);
					logger.debug(CLASS_NAME + METHOD_NAME + "Value Returned : " + val);
					return val;
				}
				else{
					val = branchCode + SPACE + NIP + employeeID + SPACE + LAHIR + dob + SPACE + MASUK + doj + DOUBLE_SPACE + jobDesc;
					logger.debug(CLASS_NAME + METHOD_NAME + "USER ID TIPE DUA : " + val);
					logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: "+ val);
					return val;
				}
			}
			
			//Kondisi CADANGAN
			else if(isBranchEmployee && "CADANGAN".equalsIgnoreCase(firstName)){
				logger.debug(CLASS_NAME + METHOD_NAME + "Identity : " + identity.getDisplayName() + " is a branch employee....but name CADANGAN");
				
				if(branchCode!=null && branchCode.length()==4){
					val = branchCode + SPACE + NIP + SPACE + SPACE+ SPACE+ SPACE+ SPACE + SPACE + SPACE + 
							LAHIR + SPACE + SPACE + DASH + SPACE + SPACE + DASH + SPACE + SPACE + SPACE +
							MASUK + SPACE + SPACE + DASH + SPACE + SPACE + DASH + SPACE + SPACE + SPACE + SPACE +
							"CADANGAN BCA " + branchCode;
					logger.debug(CLASS_NAME + METHOD_NAME + "USER ID TIPE TIGA : " + val);
					logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: "+ val);
					return val;
				}
				else{
					branchCode = (String) identity.getAttribute(IdentityAttribute.LAST_NAME);
					branchName = "KANTOR PUSAT";
					
					logger.debug(CLASS_NAME + METHOD_NAME + "Inside...Branch Code" + branchCode);
					val = branchCode + SPACE + NIP + SPACE + SPACE+ SPACE+ SPACE+ SPACE + SPACE + SPACE + 
							LAHIR + SPACE + SPACE + DASH + SPACE + SPACE + DASH + SPACE + SPACE + SPACE +
							MASUK + SPACE + SPACE + DASH + SPACE + SPACE + DASH + SPACE + SPACE + SPACE + SPACE + SPACE +
							"CADANGAN BCA " + branchName;
					logger.debug(CLASS_NAME + METHOD_NAME + "USER ID TIPE EMPAT : " + val);
					logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: "+ val);
					return val;
				}
			}
			else{
				logger.debug(CLASS_NAME + METHOD_NAME + "USER ID TIPE LIMA : ATTRIBUTE tidak ada yang benar");
			}
			
			return val;
		}
		
		/**
		 * Method currently sending the default value of 30 as defined in TDD...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_IBM_MAINFRAME_RACF_INTERVAL_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_INTERVAL_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			// Below is a preset value as defined in the TDD... if you need to
			// change... make a change here....
			String val = "30";

			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);

			return val;

		}

		/**
		 * Method currently sending default value of "N" as defined in TDD...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_IBM_MAINFRAME_RACF_REVOKED_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_REVOKED_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			// Below is a preset value as defined in the TDD... if you need to
			// change... make a change here....
			String val = "N";

			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);
			return val;

		}

		/**
		 * Method currently returning the branch code value for the identity...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_IBM_MAINFRAME_RACF_BRANCH_CODE_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_BRANCH_CODE_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);

			return val;

		}
		
		// ///////////////////////////////////////////////////////////////////////
				// *** Base24 File Feed Field Value Rules ***
				// ///////////////////////////////////////////////////////////////////////
		
		
		/**
		 * Method currently returning the NIP value for the identity...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_BASE24_FILE_FEED_NIP_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			
			String METHOD_NAME = "::getFV_BASE24_FILE_FEED_NIP_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.EMPLOYEE_ID);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);

			return val;

		}
		
		
		/**
		 * Method currently returning the NAMA USERS value for the identity...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_BASE24_FILE_FEED_NAMA_USER_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			
			String METHOD_NAME = "::getFV_BASE24_FILE_FEED_NAMA_USER_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
				
			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.SALUTATION_NAME);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);
				
			return val;

		}
		
		
		
		/**
		 * Method currently returning the KODE CABANG value for the identity...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_BASE24_FILE_FEED_KODE_CABANG_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			
			String METHOD_NAME = "::getFV_BASE24_FILE_FEED_KODE_CABANG_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);
				
			return val;

		}
		
		/**
		 * Method currently returning the WILAYAH value for the identity...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_BASE24_FILE_FEED_WILAYAH_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			
			String METHOD_NAME = "::getFV_BASE24_FILE_FEED_WILAYAH_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
				
			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);
			
			return val;

		}
		
		/**
		 * Method currently returning the GROUP value for the identity...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_BASE24_FILE_FEED_GROUP_USER_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			
			String METHOD_NAME = "::getFV_BASE24_FILE_FEED_GROUP_USER_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);

			return val;

		}
		
		/**
		 * Method currently returning the PERMISSION value for the identity...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getFV_BASE24_FILE_FEED_WILAYAH_PERMISSION_Rule(
				SailPointContext context, Identity identity, String op)
				throws GeneralException {
			
			String METHOD_NAME = "::getFV_BASE24_FILE_FEED_WILAYAH_PERMISSION_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);

			return val;

		}
		
		/**
		 * Method currently returning the USER value for the identity...
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		
		/*
		 * Example of Base24 User ID: CSO1.TCT03
		 The format is <GROUP>.<3-letter branch code><2-digit sequence number>
		 * */
		public static Object getFV_BASE24_FILE_FEED_USER_ID_Rule(
				SailPointContext context, Identity identity, String op, String group)
				throws GeneralException {
			 
			String _3letterBranchCode = "";
			String branchCode = "";
			
			String METHOD_NAME = "::getFV_BASE24_FILE_FEED_USER_ID_Rule::";
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
			
			branchCode = (String)identity.getAttribute(IdentityAttribute.BRANCH_CODE);
			_3letterBranchCode = Base24Provisioning.getInitialFromBranchCode(context, branchCode);

			String val = "";
			val = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Value: " + val);

			return val;

		}
		
		public static Object getFV_IBM_MAINFRAME_RACF_USER_ID_Rule(
				SailPointContext context, Identity identity, String op,
				String serviceTypeProfileName, ProvisioningProject project, Bundle role) throws GeneralException{
			
			String METHOD_NAME = "::TestForm::";
			
			if(project != null){
				logger.debug(CLASS_NAME + METHOD_NAME + " Projects not null ");
				logger.debug(CLASS_NAME + METHOD_NAME + " Projects xml " + project.toXml());
			}else{
				logger.debug(CLASS_NAME + METHOD_NAME + " Field is null ");
			}
			
			return "TEST";
		}
		
		/**
		 * The password is generated based on the Password Policy Defined for
		 * Mainframe.... if you need to change the policy go to IIQ UI Screen to
		 * make it change....
		 * 
		 * 
		 * @param context
		 * @param identity
		 * @param op
		 * @return
		 * @throws GeneralException
		 */
		public static Object getMainframePasswordByRole(SailPointContext context, Identity identity, ProvisioningProject project)
				throws GeneralException {
			String METHOD_NAME = "::getMainframePasswordByRole::";
			
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

			String val = "";
			
			ProvisioningPlan provPlan = project.getMasterPlan();
			
			logger.debug(CLASS_NAME + METHOD_NAME + " Get Master Plan : " + provPlan);
			
			AccountRequest iiqAccReq = provPlan.getIIQAccountRequest();
			
			String roleName = "";
			
			if(iiqAccReq == null){ //Generate password for Change password
				
				logger.debug(CLASS_NAME + METHOD_NAME + " Change password ");
				
				Attributes projAttr = project.getAttributes();
				
				String employeeId = (String)projAttr.get("requester");
				
				logger.debug(CLASS_NAME + METHOD_NAME + " Change password for " + employeeId);
				
				List listAccountRequest = provPlan.getAccountRequests("IBM MAINFRAME RACF");
				
				String mfUserId = "";
				
				if(listAccountRequest != null && listAccountRequest.size() > 0){
					
					logger.debug(CLASS_NAME + METHOD_NAME + " list account request for mainframe user is not null");
					
					mfUserId = ((AccountRequest)listAccountRequest.get(0)).getNativeIdentity();
				}
				
				logger.debug(CLASS_NAME + METHOD_NAME + " Change password for native " + mfUserId);
				
				List lstRoleAssigned = (List)identity.getPreference("roleAssignments");
				
				Iterator iteratorRoleAssigned = lstRoleAssigned.iterator();
				
				while(iteratorRoleAssigned.hasNext()){
					
					logger.debug(CLASS_NAME + METHOD_NAME + " Enter roles iteratorRoleAssigned iterator");
					
					RoleAssignment roleAssigned = (RoleAssignment)iteratorRoleAssigned.next();
					
					List lstRoleTarget = roleAssigned.getTargets();
					
					if(lstRoleTarget != null && lstRoleTarget.size() > 0){
						
						Iterator iteratorRoleTarget = lstRoleTarget.iterator();
						
						while(iteratorRoleTarget.hasNext()){
							
							logger.debug(CLASS_NAME + METHOD_NAME + " Enter roles iteratorRoleTarget iterator");
							
							RoleTarget roleTarget = (RoleTarget)iteratorRoleTarget.next();
							
							logger.debug(CLASS_NAME + METHOD_NAME + roleTarget.getApplicationName() + "::" + roleTarget.getNativeIdentity());
							
							if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(roleTarget.getApplicationName()) && mfUserId.equalsIgnoreCase(roleTarget.getNativeIdentity())){
								
								String roleId = roleAssigned.getRoleId();
								Bundle bundle = context.getObjectById(Bundle.class, roleId);
								
								logger.debug(CLASS_NAME + METHOD_NAME + " detected role with id " + roleId);
								
								roleName = bundle.getDisplayName();
								logger.debug(CLASS_NAME + METHOD_NAME + "role Name nya : " + roleName);
							}
						}
						
					}
					
					
				}
			}else{ //Generate password for user creation
				
				logger.debug(CLASS_NAME + METHOD_NAME + " Get IIQ Account Request : " + iiqAccReq);
				
				Attributes attr = iiqAccReq.getArguments();
				
				logger.debug(CLASS_NAME + METHOD_NAME + " Get IIQ Account Request Attributes : " + attr);
				
				//String roleName = (String)iiqAccReq.getAttributeRequest("assignedRoles").getValue();
				
				roleName = (String)attr.get("displayableName");
				logger.debug(CLASS_NAME + METHOD_NAME + "role Name nya : " + roleName);
				
			}
				
			
			logger.debug(CLASS_NAME + METHOD_NAME + " role name : " + roleName);
			
			//boolean isIdsApps = roleName.indexOf("IDS") == 0;
			
			boolean isIdsApps = false;
			
			logger.debug(CLASS_NAME + METHOD_NAME + " ids status is " + isIdsApps);
			
			if( isIdsApps = roleName.indexOf("IDS") == 0  || roleName.contains("IDS")){
			
				try {
					String tempVal = CommonUtil.generatePassword(context, BCAPasswordPolicyName.IDS_MAINFRAME_POLICY);
					val = tempVal.substring(0, 6).toUpperCase() + "IS";
	
					String temp_displayName = (String)identity.getDisplayName();
					logger.debug(CLASS_NAME + METHOD_NAME + "Password Generated Successfully, for user " + temp_displayName + " is :" + val);
					
				} catch (GeneralException e) {
					logger.debug(METHOD_NAME + "Error Caught: " + e.getMessage());
					e.printStackTrace();
				}
			
			}
			else if( isIdsApps = roleName.indexOf("ILS") == 0 || roleName.contains("ILS")){
				
				try {
					String tempVal = CommonUtil.generatePassword(context, BCAPasswordPolicyName.ILS_MAINFRAME_POLICY);
					val = tempVal.substring(0, 6).toUpperCase() + "IS";
			
					String temp_displayName = (String)identity.getDisplayName();
					logger.debug(CLASS_NAME + METHOD_NAME + "Password Generated Successfully, for user " + temp_displayName + " is :" + val);
					
				} catch (GeneralException e) {
					logger.debug(METHOD_NAME + "Error Caught: " + e.getMessage());
					e.printStackTrace();
				}
				
			}else if( isIdsApps = roleName.indexOf("ORT") == 0 || roleName.contains("ORT")){
				try {
						String tempVal = CommonUtil.generatePassword(context, BCAPasswordPolicyName.ORT_MAINFRAME_POLICY);
						val = tempVal.substring(0, 6).toUpperCase() + ""; // ini ditanya lagi password policy apa ?
	
						String temp_displayName = (String)identity.getDisplayName();
						logger.debug(CLASS_NAME + METHOD_NAME + "Password Generated Successfully, for user " + temp_displayName + " is :" + val);
					}catch (GeneralException e) {
						logger.debug(METHOD_NAME + "Error Caught: " + e.getMessage());
						e.printStackTrace();
					}	
			}else if(isIdsApps = roleName.indexOf("OR") == 0 || roleName.contains("OR")){
				try {
						String tempVal = CommonUtil.generatePassword(context, BCAPasswordPolicyName.OR_MAINFRAME_POLICY);
						val = tempVal.substring(0, 6).toUpperCase() + "IS";
				
						String temp_displayName = (String)identity.getDisplayName();
						logger.debug(CLASS_NAME + METHOD_NAME + "Password Generated Successfully, for user " + temp_displayName + " is :" + val);
					}catch (GeneralException e) {
						logger.debug(METHOD_NAME + "Error Caught: " + e.getMessage());
						e.printStackTrace();
					}
			}else{
				try {
						String tempVal = CommonUtil.generatePassword(context, BCAPasswordPolicyName.EXCLUDEBDS_MAINFRAME_POLICY);
						val = tempVal.substring(0, 6).toUpperCase() + "";
						
						String temp_displayName = (String)identity.getDisplayName();
						logger.debug(CLASS_NAME + METHOD_NAME + "Password Generated Successfully, for user " + temp_displayName + " is :" + val);
					} catch (GeneralException e) {
						logger.debug(METHOD_NAME + "Error Caught: " + e.getMessage());
						e.printStackTrace();
					}
			}
				return val;
		}
		
		private static String getRoleServiceType(ProvisioningProject project) throws GeneralException{
			
			String serviceType = "";
			
			ProvisioningPlan provPlan = project.getPlan("IIQ");
			
			AccountRequest iiqAccReq = provPlan.getIIQAccountRequest();
			
			serviceType = (String)iiqAccReq.getAttributeRequest("assignedRoles").getValue();
			logger.debug(CLASS_NAME + "Role getRoleServiceType : " + iiqAccReq.toXml());
			
			return generateServiceTypeName(serviceType);
		}
		
		private static String getRoleServiceType(ProvisioningProject project, boolean status){
			
			String METHOD_NAME = "::getRoleServiceType::";
			
			String serviceType = "";
			
			AccountRequest accReq = project.getIIQAccountRequest();
			
			logger.debug(CLASS_NAME + METHOD_NAME + " get IIQ Account Request");
			
			
			AttributeRequest attrReq = accReq.getAttributeRequest("detectedRoles");
			
			logger.debug(CLASS_NAME + METHOD_NAME + " get detected Roles Attribute");
			
			Attributes attr = attrReq.getArgs();
			
			logger.debug(CLASS_NAME + METHOD_NAME + " get Attributes");
			
			Map mapAttr = attr.getMap();
			
			logger.debug(CLASS_NAME + METHOD_NAME + " get Map");
			
			
			
			if(mapAttr.get("permittedBy") != null){
				
				serviceType = (String)mapAttr.get("permittedBy");
				
				logger.debug(CLASS_NAME + METHOD_NAME + " service type is " + serviceType);
			}
			
			return serviceType;
		}
		
		private static String generateServiceTypeName(String serviceType){
			
			String methodName = "::generateServiceTypeName::";
			
			String serviceTypeFullName = "";
			
			String delimiter = "-";
			
			logger.debug(CLASS_NAME + methodName + " serviceType : " + serviceType);
			
			if(serviceType.indexOf(delimiter) >= 0){
				if(serviceType.indexOf("IDS Service Type") >= 0){
					serviceTypeFullName = "IDS Service Type " + serviceType.substring(serviceType.indexOf(delimiter)).trim();
				}else{
					serviceTypeFullName = "IDS Service Type - " + serviceType.substring(0, serviceType.indexOf(delimiter)).trim();
				}
			}
			
			return serviceTypeFullName;
		}
		
		//RESUME_DATE
		public static Object getFV_IBM_MAINFRAME_RACF_RESUME_Rule(SailPointContext context,ProvisioningProject project)
			throws GeneralException {
			String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_RESUME_Rule::";
			logger.debug(METHOD_NAME + "Inside...");
					
			logger.debug(METHOD_NAME + "RESUME_DATE get " + project.getMasterPlan().toXml() + project.getPlan(CommonUtil.RACF_APPLICATION_NAME).toXml());
			ProvisioningPlan plan = project.getMasterPlan();
					
			logger.debug(METHOD_NAME + "RESUME_DATE get " + plan.toXml());
			logger.debug(METHOD_NAME + "RESUME_DATE get " + plan.getArguments());
			List<AccountRequest> lst = plan.getAccountRequests();
			Iterator it = lst.iterator();
			String val = null;
			while(it.hasNext()){
				AccountRequest req = (AccountRequest)it.next();
				
				try {
					logger.debug(METHOD_NAME + "RESUME_DATE " + req.getArgument("RESUME_DATE"));
					logger.debug( METHOD_NAME + "RESUME_DATE " + req.getArguments());
					val = req.getArgument("RESUME_DATE").toString();
					break;
				}catch(Exception e) {
					val = "";
				}
			}
				
			return val;
		}
		
		private static String getMainFrameDateFormat(String date){
			if(date == null || date.length() != 8)
				return "";
			else
				return date.substring(6) + "-" + date.substring(4, 6) + "-" + date.substring(2, 4);
		}
		
		private static String getMainFrameDateFormatNonEmployee(String date){
			if(date == null || date.length() != 8)
				return "";
			else
				return date.substring(0, 2) + "-" + date.substring(2, 4) + "-" + date.substring(6);
		}
		
		/*public static void main(String args[]){
		
		String role = "Reguler - Operator - GBO";
		
		System.out.println("Before " + role + " after " + generateServiceTypeName(role));

		}*/
		
}
