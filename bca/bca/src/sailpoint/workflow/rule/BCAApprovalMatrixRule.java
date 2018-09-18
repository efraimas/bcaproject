package sailpoint.workflow.rule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.BCAApproverMatrixConstant;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityRequesterType;
import sailpoint.common.IdentityStatus;
import sailpoint.common.IdentityUtil;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BCAApprovalMatrixRule {
	public static String CLASS_NAME = "::BCAApprovalMatrixRule::";
	public static Logger logger = Logger
			.getLogger("sailpoint.workflow.rule.BCAApprovalMatrixRule");

	// List of objects available in workflow.... before deploying.... comment
	// the below section....

	/**
	 * @param context
	 * @param identity
	 * @return
	 * @throws GeneralException
	 */
	public static Map getApproverList(SailPointContext context,
			Identity identity, String applicationName, String subApplicationName, Identity launcher, String flow, String USER_ID)
			throws GeneralException {
		String METHOD_NAME = ":: ::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		logger.debug(CLASS_NAME + METHOD_NAME + "applikasi name : " + applicationName + "sub applikasi name : " + subApplicationName);

		Map myApproverMap = new HashMap();

		Identity approver1 = null;
		Identity approver2 = null;
		Identity checker1 = null;
		Identity checker2 = null;
		Identity saAplikasi = null;

		String branchType = "";
		String requesterType = "";

		branchType = BCAApprovalMatrixRule.getBranchType(context, identity);
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Type: " + branchType);
		
		if (branchType.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "No Branch Type is found... Cannot proceed....");
			return myApproverMap;
		}
		
		String positionCode = (String)identity.getAttribute(IdentityAttribute.POSITION_CODE);
		String echelon = (String)identity.getAttribute(IdentityAttribute.ECHELON);
		if(BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			logger.debug(CLASS_NAME + METHOD_NAME + " echelon : " + echelon);
		}else
		logger.debug(CLASS_NAME + METHOD_NAME + " positionCode : " + positionCode);

		if(requesterType!=null && BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			requesterType =BCAApprovalMatrixRule.getIdentityRequesterType(context,echelon,
					(String)identity.getAttribute(IdentityAttribute.ECHELON));
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity Requester Type ec: "
					+ requesterType);

		}else if(requesterType!=null && !BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			requesterType = BCAApprovalMatrixRule.getIdentityRequesterType(context,positionCode,
					(String) identity.getAttribute(IdentityAttribute.POSITION_CODE));
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity Requester Type: "
					+ requesterType);
			
		}
		
		Custom workflowCustom = null;
		
		String varBranchCode = "$branchCode";
		String varMainBranch = "$mainBranch";
		String varDivisionCode = "$divisionCode";
		String branchCode = (String)identity.getAttribute(IdentityAttribute.BRANCH_CODE);
		String mainBranchCode = CommonUtil.getMainBranchCode(context, branchCode);
		String divisionCode = (String)identity.getAttribute(IdentityAttribute.DIVISION_CODE);
		boolean isKp = false;
		if(BCAApproverMatrixConstant.BRANCH_TYPE_KCP.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KCP_WORKFLOW_MAP);
			
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KCU.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KCU_WORKFLOW_MAP);
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KANWIL.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_REGION_WORKFLOW_MAP);
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KP_WORKFLOW_MAP);
			isKp = true;
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KFCC.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KFCC_WORKFLOW_MAP);
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_SOA.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_SOA_WORKFLOW_MAP);
		}
		
		if(workflowCustom == null){
			logger.error(CLASS_NAME + METHOD_NAME + " Custom Object for " + branchType + " is not found");
			return myApproverMap;
		}
		
		Attributes attrCustom = workflowCustom.getAttributes();
		
		Map mapAttrCustom = attrCustom.getMap();
		
        String requesterTypeKey = "";
		
		if(requesterTypeKey!=null){
			requesterTypeKey = IdentityRequesterType.REQUESTER_TYPE_STAFF;
		}
		
		if(mapAttrCustom.get(echelon) != null && BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase("KP")){
			requesterTypeKey = echelon;
			logger.debug(CLASS_NAME + METHOD_NAME + " requesterTypeKey: " + requesterTypeKey );
		}
		else if(mapAttrCustom.get(positionCode) != null){
			requesterTypeKey = positionCode;
			logger.debug(CLASS_NAME + METHOD_NAME + " requesterTypeKey: " + requesterTypeKey );
		}
			
		Map approverMatriks = (Map)mapAttrCustom.get(requesterTypeKey);
		
		if("Direct Manager".equalsIgnoreCase((String)approverMatriks.get(BCAApproverMatrixConstant.APPROVER1_KEY)))
			approver1 = getApprover1(context, identity);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " preparation to get main branch Code");
					
		logger.debug(CLASS_NAME + METHOD_NAME + " mainBranchCode is " + mainBranchCode);
		
		if(approverMatriks.get(BCAApproverMatrixConstant.APPROVER2_KEY) != null){
			
			String workgroupApprover2Name = "";
			
			if(isKp){
				workgroupApprover2Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.APPROVER2_KEY)).replace(varDivisionCode, divisionCode);
			}else{
				workgroupApprover2Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.APPROVER2_KEY)).replace(varBranchCode, branchCode);
			}
			
			logger.debug(CLASS_NAME + METHOD_NAME + " workgroupApprover2Name : " + workgroupApprover2Name);
			approver2 = CommonUtil.getWorkgroupFromWorkGroupName(context, workgroupApprover2Name);
		}
		
		if(isSameApproval(approver1, approver2)){
			approver2 = null; //Jika approver 1 = approver 2, skip approver 2.
		}
		
		if(approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY) != null){
			String workgroupChecker1Name = "";
			
			if(isKp){
				workgroupChecker1Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY)).replace(varDivisionCode,divisionCode);
			}else{
				workgroupChecker1Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY)).replace("KCP".equalsIgnoreCase(branchType) ? varMainBranch : varBranchCode,mainBranchCode);
			}
					
			logger.debug(CLASS_NAME + METHOD_NAME + " workgroupChecker1Name : " + workgroupChecker1Name);
			checker1 = CommonUtil.getWorkgroupFromWorkGroupName(context, workgroupChecker1Name);
		}
		
		if(approverMatriks.get(BCAApproverMatrixConstant.CHECKER2_KEY) != null){
			
			String workGroupChecker2Name = "";
			
			if(isKp){
				workGroupChecker2Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER2_KEY)).replace(varDivisionCode,divisionCode);
			}else{
				workGroupChecker2Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER2_KEY)).replace("KCP".equalsIgnoreCase(branchType) ? varMainBranch : varBranchCode,branchCode);
			}
			
			logger.debug(CLASS_NAME + METHOD_NAME + " workGroupChecker2Name : " + workGroupChecker2Name);
			checker2 = CommonUtil.getWorkgroupFromWorkGroupName(context, workGroupChecker2Name);
		}
		
		if(approverMatriks.get(BCAApproverMatrixConstant.SA_APLIKASI_KEY) != null){
			if(applicationName != subApplicationName) {
				List links = launcher.getLinks();
				Iterator it = links.iterator();
				String bcaApplicationName = ""; 
				while(it.hasNext()) {
					Link link = (Link)it.next();
					Attributes att = (Attributes) link.getAttributes();
					att = (Attributes) link.getAttributes();
					try{
						att.getList("groups");
						String userIdReq = att.get("USER_ID").toString();
						logger.debug("lokal userId : " + userIdReq);
						if(userIdReq.trim().equalsIgnoreCase(USER_ID.trim())) {
							bcaApplicationName = IdentityUtil.getRoleNameFromEntitlementAccountId(context, att);
						}
						logger.debug(CLASS_NAME + METHOD_NAME + "BCa aplikasnya : " + bcaApplicationName);
						String workGroupSaApplicationName = ((String)approverMatriks.get(BCAApproverMatrixConstant.SA_APLIKASI_KEY)).replaceAll("aplikasi", subApplicationName);
						String groupSa = workGroupSaApplicationName + bcaApplicationName.trim();
						logger.debug(CLASS_NAME + METHOD_NAME + " workGroupSaApplicationName : " + groupSa);
						saAplikasi = CommonUtil.getWorkgroupFromWorkGroupName(context, groupSa);
					}catch (Exception e) {
						logger.debug("group tidak ada ...");
						// TODO: handle exception
					}
				}
			}
		}
		
		if("IdentityCreateRequest".equalsIgnoreCase(flow)){
			checker2 = null;
		}
			
		myApproverMap = generateApproverMap(approver1, approver2, checker1, checker2, saAplikasi);
		
		return myApproverMap;

	}
	
	public static Map getApproverListWithOutApp2Checker2(SailPointContext context,
			Identity identity, String applicationName, String subApplicationName, Identity launcher, String flow)
			throws GeneralException {
		String METHOD_NAME = "::getApproverListWithOutApp2Checker2::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Map myApproverMap = new HashMap();

		Identity approver1 = null;
		Identity approver2 = null;
		Identity checker1 = null;
		Identity checker2 = null;
		Identity saAplikasi = null;

		String branchType = "";
		String requesterType = "";

		branchType = BCAApprovalMatrixRule.getBranchType(context, identity);
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Type: " + branchType);
		
		if (branchType.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "No Branch Type is found... Cannot proceed....");
			return myApproverMap;
		}
		
		String positionCode = (String)identity.getAttribute(IdentityAttribute.POSITION_CODE);
		String echelon = (String)identity.getAttribute(IdentityAttribute.ECHELON);
		if(BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			logger.debug(CLASS_NAME + METHOD_NAME + " echelon : " + echelon);
		}else
		logger.debug(CLASS_NAME + METHOD_NAME + " positionCode : " + positionCode);

		if(requesterType!=null && BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			requesterType =BCAApprovalMatrixRule.getIdentityRequesterType(context,echelon,
					(String)identity.getAttribute(IdentityAttribute.ECHELON));
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity Requester Type ec: "
					+ requesterType);

		}else if(requesterType!=null && !BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			requesterType = BCAApprovalMatrixRule.getIdentityRequesterType(context,positionCode,
					(String) identity.getAttribute(IdentityAttribute.POSITION_CODE));
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity Requester Type: "
					+ requesterType);
			
		}
		
		Custom workflowCustom = null;
		
		String varBranchCode = "$branchCode";
		String varMainBranch = "$mainBranch";
		String varDivisionCode = "$divisionCode";
		String branchCode = (String)identity.getAttribute(IdentityAttribute.BRANCH_CODE);
		String mainBranchCode = CommonUtil.getMainBranchCode(context, branchCode);
		String divisionCode = (String)identity.getAttribute(IdentityAttribute.DIVISION_CODE);
		boolean isKp = false;
		if(BCAApproverMatrixConstant.BRANCH_TYPE_KCP.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KCP_WORKFLOW_MAP);
			
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KCU.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KCU_WORKFLOW_MAP);
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KANWIL.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_REGION_WORKFLOW_MAP);
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KP_WORKFLOW_MAP);
			isKp = true;
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KFCC.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KFCC_WORKFLOW_MAP);
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_SOA.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_SOA_WORKFLOW_MAP);
		}
		
		if(workflowCustom == null){
			logger.error(CLASS_NAME + METHOD_NAME + " Custom Object for " + branchType + " is not found");
			return myApproverMap;
		}
		
		Attributes attrCustom = workflowCustom.getAttributes();
		
		Map mapAttrCustom = attrCustom.getMap();
		
        String requesterTypeKey = "";
		
		if(requesterTypeKey!=null){
			requesterTypeKey = IdentityRequesterType.REQUESTER_TYPE_STAFF;
		}
		
		if(mapAttrCustom.get(echelon) != null && BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase("KP")){
			requesterTypeKey = echelon;
			logger.debug(CLASS_NAME + METHOD_NAME + " requesterTypeKey: " + requesterTypeKey );
		}
		else if(mapAttrCustom.get(positionCode) != null){
			requesterTypeKey = positionCode;
			logger.debug(CLASS_NAME + METHOD_NAME + " requesterTypeKey: " + requesterTypeKey );
		}
			
		Map approverMatriks = (Map)mapAttrCustom.get(requesterTypeKey);
		
		if("Direct Manager".equalsIgnoreCase((String)approverMatriks.get(BCAApproverMatrixConstant.APPROVER1_KEY)))
			approver1 = getApprover1(context, identity);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " preparation to get main branch Code");
					
		logger.debug(CLASS_NAME + METHOD_NAME + " mainBranchCode is " + mainBranchCode);
		
		if(approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY) != null){
			String workgroupChecker1Name = "";
			
			if(isKp){
				workgroupChecker1Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY)).replace(varDivisionCode,divisionCode);
			}else{
				workgroupChecker1Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY)).replace("KCP".equalsIgnoreCase(branchType) ? varMainBranch : varBranchCode,mainBranchCode);
			}
					
			logger.debug(CLASS_NAME + METHOD_NAME + " workgroupChecker1Name : " + workgroupChecker1Name);
			checker1 = CommonUtil.getWorkgroupFromWorkGroupName(context, workgroupChecker1Name);
		}
		
		if(approverMatriks.get(BCAApproverMatrixConstant.SA_APLIKASI_KEY) != null){
			String workGroupSaApplicationName = ((String)approverMatriks.get(BCAApproverMatrixConstant.SA_APLIKASI_KEY)).replaceAll("aplikasi", subApplicationName);
			logger.debug(CLASS_NAME + METHOD_NAME + " workGroupSaApplicationName : " + workGroupSaApplicationName);
			saAplikasi = CommonUtil.getWorkgroupFromWorkGroupName(context, workGroupSaApplicationName);
		}
		
		myApproverMap = generateApproverMap(approver1, approver2, checker1, checker2, saAplikasi);
		
		return myApproverMap;

	}

	public static Map getApproverListWithOutChecker2(SailPointContext context,
			Identity identity, String applicationName, String subApplicationName, Identity launcher, String flow)
			throws GeneralException {
		String METHOD_NAME = "::getApproverListWithOutChecker2::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Map myApproverMap = new HashMap();

		Identity approver1 = null;
		Identity approver2 = null;
		Identity checker1 = null;
		Identity checker2 = null;
		Identity saAplikasi = null;

		String branchType = "";
		String requesterType = "";

		branchType = BCAApprovalMatrixRule.getBranchType(context, identity);
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Type: " + branchType);
		
		if (branchType.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "No Branch Type is found... Cannot proceed....");
			return myApproverMap;
		}
		
		String positionCode = (String)identity.getAttribute(IdentityAttribute.POSITION_CODE);
		String echelon = (String)identity.getAttribute(IdentityAttribute.ECHELON);
		if(BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			logger.debug(CLASS_NAME + METHOD_NAME + " echelon : " + echelon);
		}else
		logger.debug(CLASS_NAME + METHOD_NAME + " positionCode : " + positionCode);

		if(requesterType!=null && BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			requesterType =BCAApprovalMatrixRule.getIdentityRequesterType(context,echelon,
					(String)identity.getAttribute(IdentityAttribute.ECHELON));
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity Requester Type ec: "
					+ requesterType);

		}else if(requesterType!=null && !BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			requesterType = BCAApprovalMatrixRule.getIdentityRequesterType(context,positionCode,
					(String) identity.getAttribute(IdentityAttribute.POSITION_CODE));
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity Requester Type: "
					+ requesterType);
			
		}
		
		Custom workflowCustom = null;
		
		String varBranchCode = "$branchCode";
		String varMainBranch = "$mainBranch";
		String varDivisionCode = "$divisionCode";
		String branchCode = (String)identity.getAttribute(IdentityAttribute.BRANCH_CODE);
		String mainBranchCode = CommonUtil.getMainBranchCode(context, branchCode);
		String divisionCode = (String)identity.getAttribute(IdentityAttribute.DIVISION_CODE);
		boolean isKp = false;
		if(BCAApproverMatrixConstant.BRANCH_TYPE_KCP.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KCP_WORKFLOW_MAP);
			
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KCU.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KCU_WORKFLOW_MAP);
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KANWIL.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_REGION_WORKFLOW_MAP);
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KP_WORKFLOW_MAP);
			isKp = true;
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_KFCC.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_KFCC_WORKFLOW_MAP);
		}else if(BCAApproverMatrixConstant.BRANCH_TYPE_SOA.equalsIgnoreCase(branchType)){
			workflowCustom = CommonUtil.getCustomObject(context, CustomObject.BCA_SOA_WORKFLOW_MAP);
		}
		
		if(workflowCustom == null){
			logger.error(CLASS_NAME + METHOD_NAME + " Custom Object for " + branchType + " is not found");
			return myApproverMap;
		}
		
		Attributes attrCustom = workflowCustom.getAttributes();
		
		Map mapAttrCustom = attrCustom.getMap();
		
        String requesterTypeKey = "";
		
		if(requesterTypeKey!=null){
			requesterTypeKey = IdentityRequesterType.REQUESTER_TYPE_STAFF;
		}
		
		if(mapAttrCustom.get(echelon) != null && BCAApproverMatrixConstant.BRANCH_TYPE_KP.equalsIgnoreCase("KP")){
			requesterTypeKey = echelon;
			logger.debug(CLASS_NAME + METHOD_NAME + " requesterTypeKey: " + requesterTypeKey );
		}
		else if(mapAttrCustom.get(positionCode) != null){
			requesterTypeKey = positionCode;
			logger.debug(CLASS_NAME + METHOD_NAME + " requesterTypeKey: " + requesterTypeKey );
		}
			
		Map approverMatriks = (Map)mapAttrCustom.get(requesterTypeKey);
		
		if("Direct Manager".equalsIgnoreCase((String)approverMatriks.get(BCAApproverMatrixConstant.APPROVER1_KEY)))
			approver1 = getApprover1(context, identity);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " preparation to get main branch Code");
					
		logger.debug(CLASS_NAME + METHOD_NAME + " mainBranchCode is " + mainBranchCode);
		
		if(approverMatriks.get(BCAApproverMatrixConstant.APPROVER2_KEY) != null){
			
			String workgroupApprover2Name = "";
			
			if(isKp){
				workgroupApprover2Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.APPROVER2_KEY)).replace(varDivisionCode, divisionCode);
			}else{
				workgroupApprover2Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.APPROVER2_KEY)).replace(varBranchCode, branchCode);
			}
			
			logger.debug(CLASS_NAME + METHOD_NAME + " workgroupApprover2Name : " + workgroupApprover2Name);
			approver2 = CommonUtil.getWorkgroupFromWorkGroupName(context, workgroupApprover2Name);
		}
		
		if(isSameApproval(approver1, approver2)){
			approver2 = null;
		}
		
		if(approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY) != null){
			String workgroupChecker1Name = "";
			
			if(isKp){
				workgroupChecker1Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY)).replace(varDivisionCode,divisionCode);
			}else{
				workgroupChecker1Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY)).replace("KCP".equalsIgnoreCase(branchType) ? varMainBranch : varBranchCode,mainBranchCode);
			}
					
			logger.debug(CLASS_NAME + METHOD_NAME + " workgroupChecker1Name : " + workgroupChecker1Name);
			checker1 = CommonUtil.getWorkgroupFromWorkGroupName(context, workgroupChecker1Name);
		}
		
		if(approverMatriks.get(BCAApproverMatrixConstant.SA_APLIKASI_KEY) != null){
			String workGroupSaApplicationName = ((String)approverMatriks.get(BCAApproverMatrixConstant.SA_APLIKASI_KEY)).replaceAll("aplikasi", subApplicationName);
			logger.debug(CLASS_NAME + METHOD_NAME + " workGroupSaApplicationName : " + workGroupSaApplicationName);
			saAplikasi = CommonUtil.getWorkgroupFromWorkGroupName(context, workGroupSaApplicationName);
		}
			
		myApproverMap = generateApproverMap(approver1, approver2, checker1, checker2, saAplikasi);
		
		return myApproverMap;

	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static boolean doKerjaHaveChecker1(SailPointContext context,
			Identity identity) {
		// Unit kerja do not have Checker 1.....
		// Checker 1 approval is
		// skipped and goes directly to Checker 2.
		return false;
	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static boolean isApproverGreaterThenEchelon3(
			SailPointContext context, Identity identity) {

		// Approver 1 greater than or equals to echelon 3 Approver 2 is
		// skipped (else Approver 2 is needed as per matrix)
		return false;
	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static boolean getRequesterType(SailPointContext context,
			Identity identity) {
		return false;
	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static Identity getSecondaryApprover2(SailPointContext context,
			Identity identity) {

		return null;
	}

	/**
	 * @param context
	 * @param approver1
	 * @return
	 */
	public static Identity getApprover1Manager(SailPointContext context,
			Identity approver1) {

		String METHOD_NAME = "::getApprover1Manager::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Identity approverManager = null;

		approverManager = approver1.getManager();
		if (approverManager == null) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Approver Manager is Null... hence returning null....");
			return approverManager;
		} else {

			logger.debug(CLASS_NAME + METHOD_NAME + "Approver Manager: "
					+ approverManager.getName());
			return approverManager;
		}

	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static boolean isApprover1Active(SailPointContext context,
			Identity identity) {

		String METHOD_NAME = "::isApprover1Active::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		boolean isApprover1Active = false;

		String approverStatus = (String) identity
				.getAttribute(IdentityAttribute.STATUS);

		logger.debug(CLASS_NAME + METHOD_NAME + "approverStatus: "
				+ approverStatus);

		if (approverStatus == null || approverStatus.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "approverStatus is either null or blank... hence returning false.... "
					+ approverStatus);
			return isApprover1Active;
		}

		if (approverStatus.equalsIgnoreCase(IdentityStatus.ACTIVE_EMPLOYEE)) {

			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Active Employee hence returning true.... ");
			isApprover1Active = true;
			return isApprover1Active;
		} else {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Not an Active Employee hence returning false.... ");
			return isApprover1Active;
		}
	}

	/**
	 * This method will decide which checker group it should return.... is it
	 * "KCU0319 Security Administrators" or is it
	 * "KANWIL8 Security Administrators"
	 * 
	 * KCU0319 Security Administrators -- Currently the member is u905099, all
	 * member of this group will act as Checker 1
	 * 
	 * 
	 * KANWIL8 Security Administrators -- all member of this group will act as
	 * Checker 1 for Regional staff (at KANWIL level)
	 * 
	 * @param context
	 * @param identity
	 * @return
	 * @throws GeneralException
	 */
	public static Identity getChecker1(SailPointContext context,
			Identity identity) throws GeneralException {
		String METHOD_NAME = "::getChecker1ExactGroup::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");

		Identity checker1 = null;

		boolean isRegionalStaff = isRegionalStaff(context, identity);

		if (isRegionalStaff) {
			checker1 = CommonUtil.getWorkgroupFromWorkGroupName(
					context,
					BCAApproverMatrixConstant.CHECKER1_KANWIL8_SECURITY_ADMINISTRATORS);
			logger.debug(CLASS_NAME + METHOD_NAME + "Checker 1 found: "
					+ checker1.getName());
			return checker1;
		} else {
			checker1 = CommonUtil.getWorkgroupFromWorkGroupName(
					context,
					BCAApproverMatrixConstant.CHECKER1_KCU0319_SECURITY_ADMINISTRATORS);
			logger.debug(CLASS_NAME + METHOD_NAME + "Checker 1 found: "
					+ checker1.getName());
			return checker1;
		}

	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isRegionalStaff(SailPointContext context,
			Identity identity) throws GeneralException {
		String METHOD_NAME = "::isRegionalStaff::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");

		String branchCode = (String) identity
				.getAttribute(IdentityAttribute.BRANCH_CODE);
		
		String regionCode = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);

		if (branchCode == null || branchCode.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Branch Code is null or blank for Identity: "
					+ identity.getName());
		}

		boolean isRegionalStaff = false;

		isRegionalStaff = CommonUtil.isRegionalBranchCode(branchCode, regionCode);
		if (isRegionalStaff) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity: "
					+ identity.getName()
					+ " is a regional employee with branch code: " + branchCode);
			return isRegionalStaff;
		} else {
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity: "
					+ identity.getName()
					+ " is not a regional employee. The branch code is: "
					+ branchCode);

			return isRegionalStaff;
		}
	}

	/**
	 * @param approver1
	 * @param approver2
	 * @param checker1
	 * @param checker2
	 * @param saAplikasi
	 * @return
	 */
	public static Map generateApproverMap(Identity approver1,
			Identity approver2, Identity checker1, Identity checker2,
			Identity saAplikasi) {

		// Now Generate the approver map....
		Map myMap = new HashMap();

		if (approver1 != null)
			myMap.put(BCAApproverMatrixConstant.APPROVER1_KEY, approver1);

		if (approver2 != null)
			myMap.put(BCAApproverMatrixConstant.APPROVER2_KEY, approver2);

		if (checker1 != null)
			myMap.put(BCAApproverMatrixConstant.CHECKER1_KEY, checker1);

		if (checker2 != null)
			myMap.put(BCAApproverMatrixConstant.CHECKER2_KEY, checker2);

		if (saAplikasi != null)
			myMap.put(BCAApproverMatrixConstant.SA_APLIKASI_KEY, saAplikasi);

		return myMap;

	}

	/**
	 *
	 * 
	 * @param context
	 * @param applicationName
	 * @return
	 * @throws GeneralException
	 */
	public static Identity getApplicationSAAdministrator(
			SailPointContext context, String applicationName)
			throws GeneralException {
		String METHOD_NAME = "::getApplicationSAAdministrator::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Identity applicationSAAdministrator = null;

		logger.debug(CLASS_NAME + METHOD_NAME + "Application Name: "
				+ applicationName);
		Application application = context.getObjectByName(Application.class,
				applicationName);

		if (application == null) {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "Application Definition Not found...  Hence returning blank....");

			return applicationSAAdministrator;
		}

		Identity applicationAdministratorIdentity = application.getOwner();

		if (applicationAdministratorIdentity == null) {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "Application :"
					+ application.getName()
					+ "do not have any Administrator defined.... hence returning blank....");
			return applicationSAAdministrator;
		}

		applicationSAAdministrator = applicationAdministratorIdentity;
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Application Administrator Identity found....: "
				+ applicationSAAdministrator.getName());

		return applicationSAAdministrator;
	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 * @throws GeneralException
	 */
	public static Identity getApprover1(SailPointContext context,
			Identity identity) throws GeneralException {
		String METHOD_NAME = "::getApprover1::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		Identity approver1 = null;

		approver1 = identity.getManager();
		if (approver1 == null) {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "Manager ID is either null or blank... hence returning blank...");

			approver1 = context.getObjectByName(Identity.class,
					CommonUtil.IIQ_ADMINISTRATOR);

			return approver1;
		}
		logger.debug(CLASS_NAME + METHOD_NAME + "Returning approver1: "
				+ approver1.getName());

		return approver1;
	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 * @throws GeneralException
	 */
	public static String getBranchType(SailPointContext context,
			Identity identity) throws GeneralException {
		String METHOD_NAME = "::getBranchType::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String branchType = "";

		String branchCode = (String) identity
				.getAttribute(IdentityAttribute.BRANCH_CODE);
		
		String regionCode = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);

		if (branchCode == null || branchCode.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Branch Code for the identity: " + identity.getName()
					+ " is null hence returning blank value....");
			return branchType;

		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code: " + branchCode);
		
		if (CommonUtil.isHQBranchCode(branchCode)) {
			branchType = BCAApproverMatrixConstant.BRANCH_TYPE_KP;
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Branch Type: "
					+ branchType);
			return branchType;
		}else if (CommonUtil.isRegionalBranchCode(branchCode, regionCode)) {
			branchType = BCAApproverMatrixConstant.BRANCH_TYPE_KANWIL;
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Branch Type: "
					+ branchType);
			return branchType;

		} else if(CommonUtil.isSOABranchCode(branchCode)){
			branchType = BCAApproverMatrixConstant.BRANCH_TYPE_SOA;
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Branch Type: "
					+ branchType);
			return branchType;
		} else if(CommonUtil.isKFCCBranchCode(branchCode)){
			branchType = BCAApproverMatrixConstant.BRANCH_TYPE_KFCC;
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Branch Type: "
					+ branchType);
			return branchType;
		}else if (CommonUtil.isSubBranchCode(context, branchCode)) {
			branchType = BCAApproverMatrixConstant.BRANCH_TYPE_KCP;
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Branch Type: "
					+ branchType);
			return branchType;

		} else if(CommonUtil.isMainBranchCode(context, branchCode)){
			branchType = BCAApproverMatrixConstant.BRANCH_TYPE_KCU;
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning Branch Type: "
					+ branchType);
			return branchType;
		}
		
		
		logger.debug(CLASS_NAME + METHOD_NAME + branchType);
		
		return branchType;
	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static String getIdentityRequesterType(SailPointContext context,
			String positionCode, String echelon) {
		String METHOD_NAME = "::getIdentityRequesterType::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside..., with positionCode " + positionCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside..., with echelon " + echelon);
		String identityType = "";


		if(isMemberOf(BCAApproverMatrixConstant.KABAG_POSITION_CODE, positionCode))
			identityType = IdentityRequesterType.REQUESTER_TYPE_KABAG;
		else if(isMemberOf(BCAApproverMatrixConstant.WAPIM_KCP_POSITION_CODE, positionCode))
			identityType = IdentityRequesterType.REQUESTER_TYPE_WAPIM;
		else if(isMemberOf(BCAApproverMatrixConstant.PIMPINAN_KCP_POSITION_CODE, positionCode))
			identityType = IdentityRequesterType.REQUESTER_TYPE_PIMPINAN;
		else if(isMemberOf(BCAApproverMatrixConstant.KADIV_ECHELON_1, echelon))
			identityType = IdentityRequesterType.REQUEST_TYPE_ECHELON_1;
		else if(isMemberOf(BCAApproverMatrixConstant.ECHELON_2, echelon))
			identityType = IdentityRequesterType.REQUEST_TYPE_ECHELON_2;
		else if(isMemberOf(BCAApproverMatrixConstant.ECHELON_3, echelon))
			identityType = IdentityRequesterType.REQUEST_TYPE_ECHELON_3;
		else if(isMemberOf(BCAApproverMatrixConstant.ECHELON_7,echelon))
			identityType = IdentityRequesterType.REQUESTER_TYPE_STAFF_KP;
		else
			identityType = IdentityRequesterType.REQUESTER_TYPE_STAFF;
		
		logger.debug(CLASS_NAME + METHOD_NAME + "IdentityRequesterType: " + identityType);

		return identityType;
	}

	/**
	 * Approver 2
	 * 
	 * KCP0075 Head Approvers -- SABANG KCP approvers (Pemimpin and Wapim)
	 * 
	 * KCU0319 Head Approvers -- CITY TOWER KCU approvers (Kepala, KOC, KPBCÉ)
	 * 
	 * KANWIL8 Head Approvers -- WILAYAH VIII approvers (Kakanwil)
	 * 
	 * 
	 * @param context
	 * @param identity
	 * @return
	 * @throws GeneralException
	 */
	public static Identity getApprover2(SailPointContext context,
			Identity identity, String requesterType) throws GeneralException {
		String METHOD_NAME = "::getApprover2::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		Identity approver2 = null;

		String branchCode = (String) identity
				.getAttribute(IdentityAttribute.BRANCH_CODE);
		
		String regionCode = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);
		

		if (CommonUtil.isHQBranchCode(branchCode)) {

			approver2 = CommonUtil.getWorkgroupFromWorkGroupName(
					context,
					BCAApproverMatrixConstant.BCA_CITY_TOWER_KCU_APPROVERS);
			logger.debug(CLASS_NAME + METHOD_NAME + "Approver2: "
					+ approver2.getName());

		} else if (CommonUtil.isRegionalBranchCode(branchCode, regionCode)) {

			approver2 = CommonUtil.getWorkgroupFromWorkGroupName(
					context,
					BCAApproverMatrixConstant.BCA_WILAYAH_VIII_APPROVERS);
			logger.debug(CLASS_NAME + METHOD_NAME + "Approver2: "
					+ approver2.getName());

		} else if(CommonUtil.isMainBranchCode(context, branchCode)){
			approver2 = CommonUtil.getWorkgroupFromWorkGroupName(context, BCAApproverMatrixConstant.BCA_CITY_TOWER_KCU_APPROVERS);
			
			logger.debug(CLASS_NAME + METHOD_NAME + "Approver2: "
					+ approver2.getName());
			/*logger.debug(METHOD_NAME + " get Approver 2 with requester type : " + requesterType + " branch type" + "KCU");
			approver2 = CommonUtil.searchActiveIdentityByBranchByPosition
					(context, branchCode, getApprover2Position(requesterType, BCAApproverMatrixConstant.BRANCH_TYPE_KCU));
			logger.debug(METHOD_NAME + " position " + getApprover2Position(requesterType, BCAApproverMatrixConstant.BRANCH_TYPE_KCU));
			
			logger.error(CLASS_NAME + METHOD_NAME + "Approver2: "
					+ approver2.getName());*/
		}
		else if(CommonUtil.isSubBranchCode(context, branchCode)){
			approver2 = CommonUtil
					.getWorkgroupFromWorkGroupName(context,
							BCAApproverMatrixConstant.BCA_SABANG_KCP_APPROVERS);
			
			/*logger.debug(METHOD_NAME + " get Approver 2 with requester type : " + requesterType + " branch type " + "KCP");
			approver2 = CommonUtil.searchActiveIdentityByBranchByPosition
					(context, branchCode, getApprover2Position(requesterType, BCAApproverMatrixConstant.BRANCH_TYPE_KCP));
			logger.debug(METHOD_NAME + " position " + getApprover2Position(requesterType, BCAApproverMatrixConstant.BRANCH_TYPE_KCP));*/
			
			logger.debug(CLASS_NAME + METHOD_NAME + "Approver2: "
					+ approver2.getName());

		}

		return approver2;
	}

	/**
	 * @param conetxt
	 * @param identity
	 * @return
	 * @throws GeneralException
	 */
	public static Identity getSAAplikasi(SailPointContext context,
			Identity identity, String applicationName) throws GeneralException {
		String METHOD_NAME = "::getSAAplikasi::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Application application = context.getObjectByName(Application.class,
				applicationName);
		Identity owner = application.getOwner();

		if (owner.isWorkgroup()) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Owner: " + owner.getName()
					+ " is a workgroup.....");
		}

		// Below 4 if conditions can be removed... as its not necessary....
		if (owner.getName().equalsIgnoreCase(
				BCAApproverMatrixConstant.SA_APLIKASI_BCA_AD_OWNER))
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "BCA AD Application Owner Group....");
		if (owner.getName().equalsIgnoreCase(
				BCAApproverMatrixConstant.SA_APLIKASI_BCA_EXCHANGE_OWNER))
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "BCA Exchange Application Owner Group....");
		if (owner.getName().equalsIgnoreCase(
				BCAApproverMatrixConstant.SA_APLIKASI_BCA_MF_OWNER))
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "BCA Mainframe Application Owner Group....");
		if (owner.getName().equalsIgnoreCase(
				BCAApproverMatrixConstant.SA_APLIKASI_BCA_BASE24_OWNER))
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "BCA Base 24 Application Owner Group....");

		return owner;
	}

	
	public static String getApprover2Position(String requesterType, String branchType){
		
		if(IdentityRequesterType.REQUESTER_TYPE_STAFF.equalsIgnoreCase(requesterType) && BCAApproverMatrixConstant.BRANCH_TYPE_KCP.equalsIgnoreCase(branchType))
			return BCAApproverMatrixConstant.KEPALA_KCP_A_POSITION_CODE; //+ ", " + CommonUtil.WAPIM_KCP_A  + ", " + CommonUtil.KEPALA_KCP_B_C  + ", " + CommonUtil.WAPIM_KCP_B_C;	
		else if(IdentityRequesterType.REQUESTER_TYPE_STAFF.equalsIgnoreCase(requesterType) && BCAApproverMatrixConstant.BRANCH_TYPE_KCU.equalsIgnoreCase(branchType))
			return BCAApproverMatrixConstant.KEPALA_KCU_A_POSITION_CODE;// + ", " + CommonUtil.WAPIM_KCU_A  + ", " + CommonUtil.KEPALA_KCU_B_C + ", " + CommonUtil.WAPIM_KCU_B_C;
		/*else if(IdentityRequesterType.REQUESTER_TYPE_STAFF.equalsIgnoreCase(requesterType) && BCAApproverMatrixConstant.BRANCH_TYPE_KANWIL.equalsIgnoreCase(branchType))
			return CommonUtil.;*/
		
		return BCAApproverMatrixConstant.KEPALA_KCP_B_C_POSITION_CODE;// + " , " + CommonUtil.WAPIM_KCP_B_C;
	}
	
	private static boolean isMemberOf(String listOfPositionCode, String positionCode){
		
		String[] tesArr = listOfPositionCode.split(";");
		
		int len = tesArr.length;
		
		for(int i=0; i<len; i++){
			if(tesArr[i].equalsIgnoreCase(positionCode))
				return true;
		}
		
		return false;
	}
	
	private static boolean isSameApproval(Identity approver1, Identity approver2){
		
		if(approver1 != null & approver2 != null){
			
			if(approver1.isWorkgroup()){
				if(approver2.isWorkgroup()){
					return approver1.getId().equalsIgnoreCase(approver2.getId());
				}else{
					return IdentityUtil.isMemberOfWorkGroup(approver1, approver2);
				}
			}else{
				if(approver2.isWorkgroup()){
					return IdentityUtil.isMemberOfWorkGroup(approver2, approver1);
				}else{
					return approver2.getId().equalsIgnoreCase(approver1.getId());
				}
			}
			
			
		}
		
		return false;
	}
}
