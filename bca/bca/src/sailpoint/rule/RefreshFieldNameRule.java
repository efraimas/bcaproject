package sailpoint.rule;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Identity;
import sailpoint.server.InternalContext;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class RefreshFieldNameRule{

	public static Logger logger = Logger
			.getLogger("sailpoint.rule.RefreshFieldNameRule");

	static String CLASS_NAME = "RefreshFieldNameRule";
	
	static String requesterBranch = ""; 
	static String ManagerIdBranch = "";
	static boolean sameBranch = true;

	public static String getBranchNameFromBranchCode(SailPointContext context,
			String branchCode) throws GeneralException {
		String branchName = "";
		
		String METHOD_NAME = "::getBranchNameFromBranchCode::";
		String ATTRIBUTE_NAME = "branchTable";
		String keyCode = "subBranchCode";
		String keyName = "branchName";

		logger.debug("Method name" + METHOD_NAME);

		Custom cbgCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_BRANCH_TABLE_CUSTOM_OBJECT);

		Attributes cbgAttributes = cbgCustomObject.getAttributes();

		List localList = cbgAttributes.getList(ATTRIBUTE_NAME);
		

		Iterator it = localList.iterator();

		while (it.hasNext()) {
			Map branchMap = (Map) it.next();
			if (branchCode.equalsIgnoreCase((String) branchMap
					.get(keyCode))) {
				branchName = (String) branchMap.get(keyName);
			}
		}

		return branchName;
	}

	public static String getSubdivisionNameFromSubDivisionCode(
			SailPointContext context, String subDivisionCode)
			throws GeneralException {

		String subDivisionName = "";
		
		String METHOD_NAME = "::getSubdivisionNameFromSubDivisionCode::";

		logger.debug("Method name" + METHOD_NAME);

		Custom subDivCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_SUB_DIVISION_TABLE_MAPPING_CUSTOM_OBJECT);

		Attributes subDivAttr = subDivCustomObject.getAttributes();

		Map subDivMap = subDivAttr.getMap();

		subDivisionName = (String)subDivMap.get((String)subDivisionCode);

		return subDivisionName == null ? "" : subDivisionName;
	}
	
	
	
	
	/**
	 * This method is added because, there's some refresh field name, call same method but with parameter internal context,
	 * not by SailpointContext, don't know why
	 * */
	public static String getSubdivisionNameFromSubDivisionCode(
			InternalContext con, String subDivisionCode)
			throws GeneralException {
		
		return getSubdivisionNameFromSubDivisionCode(con.getContext(), subDivisionCode);
	}

	public static String getRegionNameFromRegionCode(SailPointContext context,
			String regionCode) throws GeneralException {

		String regionName = "";

		String METHOD_NAME = "::getRegionNameFromRegionCode::";

		logger.debug("Method name" + METHOD_NAME);

		Custom regionCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_REGION_TABLE_MAPPING_CUSTOM_OBJECT);

		Attributes regionAttributes = regionCustomObject.getAttributes();

		Map mapRegion = regionAttributes.getMap();	
		
		regionName = (String)mapRegion.get((String)regionCode);

		return regionName == null ? "":regionName;
	}

	public static String getPositionNameFromPositionCode(SailPointContext context,
			String positionCode) throws GeneralException {

		String positionName = "";

		String METHOD_NAME = "::getPositionNameFromRegioPositionCode::";

		logger.debug("Method name" + METHOD_NAME);

		Custom regionCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_POSITION_TABLE_MAPPING_CUSTOM_OBJECT);

		Attributes positionAttributes = regionCustomObject.getAttributes();

		Map mapPosition = positionAttributes.getMap();	
		
		positionName = (String)mapPosition.get((String)positionCode);

		return positionName == null ? "":positionName;
	}
	
	public static String getJobNameFromJobCode(SailPointContext context,
			String jobCode) throws GeneralException {

		String jobName = "";

		String METHOD_NAME = "::getJobNameFromJobCode::";

		logger.debug("Method name" + METHOD_NAME);

		Custom regionCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_JOB_TABLE_MAPPING_CUSTOM_OBJECT);

		Attributes jobAttributes = regionCustomObject.getAttributes();

		Map mapJob = jobAttributes.getMap();	
		
		jobName = (String)mapJob.get((String)jobCode);

		return jobName == null ? "":jobName;
	}

	public static String getDivisionNameFromDivisionCode(SailPointContext context,
			String divisionCode) throws GeneralException {

		String divisionName = "";

		String METHOD_NAME = "::getRegionNameFromRegionCode::";

		logger.debug("Method name" + METHOD_NAME);

		Custom regionCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_DIVISION_TABLE_MAPPING_CUSTOM_OBJECT);

		Attributes regionAttributes = regionCustomObject.getAttributes();

		Map divisionMap = regionAttributes.getMap();

		divisionName = (String)divisionMap.get((String)divisionCode);

		return divisionName == null ? "" : divisionName;
	}
	 
	public static String getValidEmailAddress(SailPointContext context, String firstname, String middlename, String lastname, String domain)  throws GeneralException {

		if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isNotEmptyString(lastname) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + "_" + lastname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
			return firstname + "_" + lastname + domain;
		}
		if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
			return firstname + domain;
		}
		
		if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isNotEmptyString(lastname) && CommonUtil.isNotEmptyString(middlename) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + "_" + middlename + "_" + lastname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
			return firstname + "_" + middlename + "_" + lastname + domain;
		}
		return "";
	}
	public static String getValidEmailAddress_1(SailPointContext context, Identity identity, String firstname, String middlename, String lastname, String domain)  throws GeneralException {
		
		if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isNotEmptyString(lastname) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + "_" + lastname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
			
			String idEmployee = (String) identity.getAttribute(IdentityAttribute.EMPLOYEE_ID);
			
			if(idEmployee.length() > 6){
				return "U" + idEmployee.substring(2, idEmployee.length()) + domain;
			}
			else if (idEmployee.length() == 6){
				return "U" + idEmployee + domain;
			}
			
		}
		if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
			
			String idEmployee = (String) identity.getAttribute(IdentityAttribute.EMPLOYEE_ID);
			
			if(idEmployee.length() > 6){
				return "U" + idEmployee.substring(2, idEmployee.length()) + domain;
			}
			else if (idEmployee.length() == 6){
				return "U" + idEmployee + domain;
			}
		}
		
		if(CommonUtil.isNotEmptyString(firstname) && CommonUtil.isNotEmptyString(lastname) && CommonUtil.isNotEmptyString(middlename) && CommonUtil.isUniqueValue(context, IdentityAttribute.EMAIL, firstname + "_" + middlename + "_" + lastname + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL)){
			String idEmployee = (String) identity.getAttribute(IdentityAttribute.EMPLOYEE_ID);
			
			if(idEmployee.length() > 6){
				return "U" + idEmployee.substring(2, idEmployee.length()) + domain;
			}
			else if (idEmployee.length() == 6){
				return "U" + idEmployee + domain;
			}
		}
		
		return "";
	}
	
	public static String getManagerNameFromManagerId(SailPointContext context, String managerId) throws GeneralException{
		 				
		String userId =  context.getUserName();
		
			
		requesterBranch = getBranchCode(context, userId);
		ManagerIdBranch = getBranchCode(context, managerId);
		
		sameBranch = isRequesterhasSameBranch();
				
		logger.debug("Your Requester Branch is " + requesterBranch);
		logger.debug("Your Manager Branch is " + ManagerIdBranch);
		logger.debug("Branch Validation return " + sameBranch);
		
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null){
			return "";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.SALUTATION_NAME);
		}
	}
	
	private static String getBranchCode(SailPointContext context, String Id) throws GeneralException{
		
		Identity userId = IdentityUtil.searchActiveIdentityById(context, Id);
		
		if(userId == null){
			return "";
		}else{
			return (String) userId.getAttribute(IdentityAttribute.BRANCH_CODE);
		}
		
	}
	
	private static boolean isRequesterhasSameBranch(){
		boolean sameBranch = false;
		
		logger.debug("Rewrite Requester Branch is " + requesterBranch);
		logger.debug("Rewrite Manager Branch is " + ManagerIdBranch);
		
		if(requesterBranch.equals(ManagerIdBranch)){
			sameBranch = true;
		}else{
			sameBranch = false;
		}
		
		return sameBranch;
	}
	
	public static String getBranchCodeFromManager(SailPointContext context, String managerId) throws GeneralException{
		
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null){
			return "";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.BRANCH_CODE);
		}
		
	}
	
	public static String getBranchNameFromManager(SailPointContext context, String managerId) throws GeneralException{
			
			Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
			
			if(manager == null){
				return "";
			}else if(sameBranch == false){
				return "";
			}else{
				return (String) manager.getAttribute(IdentityAttribute.BRANCH_NAME);
			}
			
		}
	public static String getSubDivisionCodeFromManager(SailPointContext context, String managerId) throws GeneralException{
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null){
			return "";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.SUBDIVISION_CODE);
			
		}
	}
	public static String getSubDivisionNameFromManager(SailPointContext context, String managerId) throws GeneralException{
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null){
			return "";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.SUBDIVISION_NAME);
			
		}
	}
	public static String getDivisionCodeFromManager(SailPointContext context, String managerId) throws GeneralException{
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null){
			return"";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.DIVISION_CODE);
			
		}
	}
	public static String getDivisionNameFromManager(SailPointContext context, String managerId) throws GeneralException{
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null){
			return"";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.DIVISION_NAME);
		}
	}
	public static String getReggionCodeFromManager(SailPointContext context, String managerId) throws GeneralException{
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null ){
			return "";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.REGION_CODE);
			
		}
	}
	public static String getReggionNameFromManager(SailPointContext context, String managerId) throws GeneralException{
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null ){
			return "";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.REGION_NAME);
			
		}
	}
	public static String getPositionCodeFromManager(SailPointContext context, String managerId) throws GeneralException{
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null ){
			return "";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.POSITION_CODE);
			
		}
	}
	public static String getPositionNameFromManager(SailPointContext context, String managerId) throws GeneralException{
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null ){
			return "";
		}else if(sameBranch == false){
			return "";
		}else{
			return (String) manager.getAttribute(IdentityAttribute.REGION_CODE);
			
		}
	}
	public static Object getCostCenterFromManager(SailPointContext context, String managerId) throws GeneralException{
		Identity manager = IdentityUtil.searchActiveIdentityById(context, managerId);
		
		if(manager == null){
			return "";
		}else if(sameBranch == false){
			return "";
		}else{
			return manager.getAttribute(IdentityAttribute.COST_CENTER);
			
		}
	}
}