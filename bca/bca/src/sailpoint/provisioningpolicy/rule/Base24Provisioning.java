package sailpoint.provisioningpolicy.rule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.BCAPasswordPolicyName;
import sailpoint.common.Base24Attribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Identity;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class Base24Provisioning {
	
	public static String CLASS_NAME = "::Base24Provisioning::";

	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.Base24Provisioning");
	
	public static String getInitialFromBranchCode(SailPointContext context,
			String branchCode) throws GeneralException {
		String initial = "";

		String METHOD_NAME = "::getInitialFromBranchCode::";
		String ATTRIBUTE_NAME = "branchTable";
		String keyCode = "subBranchCode";
		String keyName = "initial";

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
				initial = (String) branchMap.get(keyName);
			}
		}

		return initial;
	}
	
	/**
	 * 
	 * Example for userid Base24 : CSO1.BDG01
	 * CSO1 => Group name, get from Identity Requst Item Object
	 * BDG => Initial Branch Code
	 * 01 => Sequence Number 
	 * 
	 * Sequence number each group and branch are stored in 1 custom object file
	 * format of custom object is like this
	 * <Map>
	 * <entry key="CSO1.BDG_nextseq" value="1">
	 * <entry key="CSO1.BDG_maxseq" value="99">
	 * <entry key="CSO1.JKT_nextseq" value="1">
	 * <entry key="CSO1.JKT_maxseq" value="99">
	 * <entry key="base24_nextseq value="1">
	 * 
	 * 
	 * </Map>
	 * 
	 * 
	 * 
	 * 
	 * 
	 * */
	
	public static String getNextSequenceByGroup(SailPointContext context, String group, String initialBranch) throws GeneralException{
		
		String METHOD_NAME = "::getNextSequenceByGroup::";
		
		String val = "";
		
		int nextVal = 1;
		
		int maxVal = 99;
		
		int counter = 1;
		
		Map seqMap = null;
		
		String suffixKeyNext = group + "." + initialBranch  + "_nextseq";
		
		logger.debug(METHOD_NAME + " suffixKeyNext " + suffixKeyNext);
		
		String suffixMaxKey = group + "." + initialBranch  + "_maxseq";
		
		logger.debug(METHOD_NAME + " suffixMaxKey " + suffixMaxKey);
		
		Custom seqCustomObject = CommonUtil.getCustomObject(context, CustomObject.BCA_BASE24_GROUP_SEQUENCE);
		
		Attributes base24Group = null;
		
		if(seqCustomObject != null){
			
			logger.debug(METHOD_NAME + " " + CustomObject.BCA_BASE24_GROUP_SEQUENCE + " is found");
			
			base24Group = seqCustomObject.getAttributes();	
			
			seqMap = base24Group.getMap();
			
			if(seqMap.containsKey(suffixKeyNext)){
				logger.debug(METHOD_NAME + suffixKeyNext + " is found");
				nextVal = Integer.parseInt((String)seqMap.get(suffixKeyNext));
			}
			
			if(seqMap.containsKey(suffixMaxKey)){
				logger.debug(METHOD_NAME + suffixMaxKey + " is found");
				maxVal = Integer.parseInt((String)seqMap.get(suffixMaxKey));
			}	
		}
			
		boolean isUnique = false;

		// Generating the first value....
		val = generateBase24UserIDString(group, initialBranch, nextVal);

		// Checking the uniqueness for the first time...
		isUnique = CommonUtil.isUniqueApplicationAttributeValue(context,
				CommonUtil.BASE24_FILE_FEED_APPLICATION, Base24Attribute.USER_ID, val);
		
		logger.debug(METHOD_NAME + " first try uniqueness check user id " + val + ", result: " + isUnique);
		
		
		while(!isUnique){
			int i = nextVal + 1;
			boolean alreadyReachMax = false;
			for(; i<= maxVal && !isUnique; i++){
				counter++;
				val = generateBase24UserIDString(group, initialBranch, i);
				
				isUnique = CommonUtil.isUniqueApplicationAttributeValue(context,
						CommonUtil.BASE24_FILE_FEED_APPLICATION, Base24Attribute.USER_ID, val);
				
				logger.debug(METHOD_NAME +  nextVal + " try uniqueness check user id " + val + ", result: " + isUnique);
				
				if(i == maxVal && !alreadyReachMax){
					i = 0;
					alreadyReachMax = true;
				}else if(i == maxVal && alreadyReachMax){
					return "No Id Slot";
				}
				logger.debug(METHOD_NAME + "Total looping nya : " + counter);
				//if(counter> 2) throw new GeneralException("SEQUENCE IS ALREADY FULL");
			}
			if (i == 0) {
				nextVal = i;
			} else {
				nextVal = i - 1;
			}
			
		}
		
		if(seqMap == null){
			seqMap = new HashMap();	
		}
		
		if(null == base24Group)
			base24Group = new Attributes();
		
		logger.debug(METHOD_NAME + "Key Next Val : " + suffixKeyNext + " next value : " + nextVal+1 + 
				"Suffix Max Val : " + suffixKeyNext + " max val : " + maxVal);
		
		
			seqMap.put(suffixKeyNext, String.valueOf(nextVal+1));
			seqMap.put(suffixMaxKey, String.valueOf(maxVal));
		
		
		base24Group.putAll(seqMap);
		
		CommonUtil.updateCustomObject(context, CustomObject.BCA_BASE24_GROUP_SEQUENCE, base24Group);
		
		return val;
		
	}
	
	public static String getWilayahFromEmployeeId(SailPointContext ctx, String employeeId) throws GeneralException{
		String wilayah = "";
		
		Identity identity = null;
		if(employeeId.contains("CADANGAN") || employeeId.contains("cadangan")){
			identity = CommonUtil.searchIdentity(ctx, IdentityAttribute.NAME, employeeId);
		}else{
			identity = CommonUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, employeeId);
		}
		
		
		if(null != identity){
			if (identity.getAttribute(IdentityAttribute.REGION_CODE) != null) {
				String regionCode = String.valueOf(Integer.parseInt((String)identity.getAttribute(IdentityAttribute.REGION_CODE)));
				wilayah = CommonUtil.getRegionTypeValueFromRegionCode(ctx, regionCode, "base24RegionName");
			}
		}else{
			wilayah = "";
			logger.debug("Wilayah Kosong");
		}
		return wilayah;
	}
	
	public static String getCabutByEmployeeId(SailPointContext ctx, String employeeId) throws GeneralException{
		String cabut = "";
		
		Identity identity = null;
		if(employeeId.contains("CADANGAN") || employeeId.contains("cadangan")){
			identity = CommonUtil.searchIdentity(ctx, IdentityAttribute.NAME, employeeId);
		}else{
			identity = CommonUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, employeeId);
		}
		
		
		if(null != identity){
			String regionCode = (String)identity.getAttribute("branchCode");
			cabut = CommonUtil.getBranchValueFromBranchCode(ctx, regionCode, "branchCode");
		}else{
			cabut = "";
			logger.debug("Cabut Kosong");
		}
		return cabut;
	}
	
	public static String getKodecByEmployeeId(SailPointContext ctx, String employeeId) throws GeneralException{
		String kodec = "";
		
		Identity identity = null;
		if(employeeId.contains("CADANGAN") || employeeId.contains("cadangan")){
			identity = CommonUtil.searchIdentity(ctx, IdentityAttribute.NAME, employeeId);
		}else{
			identity = CommonUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, employeeId);
		}
		
		if(null != identity){
			kodec = (String)identity.getAttribute("branchCode");
		}else{
			kodec = "";
			logger.debug("Kodec Kosong");
		}
		return kodec;
	}
	
	public static String getGroupNumberByGroupCode(SailPointContext ctx, String groupCode) throws GeneralException{
		
		String METHOD_NAME = "::getGroupNumberFromGroupCode::";
		
		String groupNumber = "";
		
		logger.debug(METHOD_NAME + " get custom object " + CustomObject.BCA_BASE24_GROUP_MAP);
		
		Custom custom = CommonUtil.getCustomObject(ctx, CustomObject.BCA_BASE24_GROUP_MAP);
		
		if(custom == null)
			logger.debug(METHOD_NAME + "Custom Object not found");
		
		Attributes attr = custom.getAttributes();
		
		Map groupMap = attr.getMap();
		
		logger.debug(METHOD_NAME + " try to get value with key " + groupCode);
		
		groupNumber = (String)groupMap.get(groupCode);
		
		return groupNumber;
	}
	
	public static String getUserFromBase24Sequence(SailPointContext ctx, String group) throws GeneralException{
		
		String METHOD_NAME = "::getUserFromBase24Sequence::";
		
		String val = "";
		
		int nextVal = 1;
		
		int maxVal = 999;
		
		Map seqMap = null;
		
		
		String suffixKeyNext = group + "." + "USER"  + "_nextseq";
		
		logger.debug(METHOD_NAME + " suffixKeyNext " + suffixKeyNext);
		
		String suffixMaxKey = group + "." + "USER"  + "_maxseq";
		
		logger.debug(METHOD_NAME + " suffixMaxKey " + suffixMaxKey);
		
		Custom seqCustomObject = CommonUtil.getCustomObject(ctx, CustomObject.BCA_BASE24_GROUP_MAP);
		
		Attributes base24Group = null;
		
		if(seqCustomObject == null)
			logger.debug(METHOD_NAME + "Custom Object not found");
		
		if(seqCustomObject != null){
			
			logger.debug(METHOD_NAME + " " + CustomObject.BCA_BASE24_GROUP_MAP + " is found");
			
			base24Group = seqCustomObject.getAttributes();	
			
			seqMap = base24Group.getMap();
			
			if(seqMap.containsKey(suffixKeyNext)){
				logger.debug(METHOD_NAME + suffixKeyNext + " is found");
				nextVal = Integer.parseInt((String)seqMap.get(suffixKeyNext));
			}
			
			if(seqMap.containsKey(suffixMaxKey)){
				logger.debug(METHOD_NAME + suffixMaxKey + " is found");
				maxVal = Integer.parseInt((String)seqMap.get(suffixMaxKey));
			}	
		}
			
		int i = nextVal + 1;
										
		if(i == maxVal){
			i = 0;
			return "No User Slot";
		}

		if(seqMap == null){
			seqMap = new HashMap();	
		}
		
		if(null == base24Group)
			base24Group = new Attributes();
				
					seqMap.put(suffixKeyNext, String.valueOf(nextVal+1));
					seqMap.put(suffixMaxKey, String.valueOf(maxVal));
					ctx.commitTransaction();
		
		base24Group.putAll(seqMap);
		
		CommonUtil.updateCustomObject(ctx, CustomObject.BCA_BASE24_GROUP_MAP, base24Group);
		/*logger.debug(METHOD_NAME + " get custom object " + CustomObject.BCA_BASE24_GROUP_SEQUENCE);
		
		Custom custom = CommonUtil.getCustomObject(ctx, CustomObject.BCA_BASE24_GROUP_SEQUENCE);
		
		Attributes attr = custom.getAttributes();
		
		Map attrMap = attr.getMap();
		
		if(attrMap.containsKey(BASE24_NEXT_VAL)){
			nextVal = Integer.parseInt(attrMap.get(BASE24_NEXT_VAL).toString());
			
			logger.debug(METHOD_NAME + " key is found, next value " + nextVal);
			
		}
		
		attrMap.put(BASE24_NEXT_VAL, String.valueOf(nextVal + 1));
		
		attr.putAll(attrMap);
		
		logger.debug(METHOD_NAME + " update to custom object, with val " + (nextVal + 1));
				
		CommonUtil.updateCustomObject(ctx, CustomObject.BCA_BASE24_GROUP_SEQUENCE, attr);*/
		
		return String.valueOf(nextVal);
	}
	
	public static String getNamaUserByEmployeeId(SailPointContext ctx, String employeeId) throws GeneralException{
		
		String METHOD_NAME = "::getNamaUserFromEmployeeId::";
		
		String nama = "";
		String firstName = "";
		String middleName = "";
		String lastName = "";
		
		Identity identity = null;
		if(employeeId.contains("CADANGAN") || employeeId.contains("cadangan")){
			identity = CommonUtil.searchIdentity(ctx, IdentityAttribute.NAME, employeeId);
		}else{
			identity = CommonUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, employeeId);
		}
		
		logger.debug(METHOD_NAME + " get identity with id " + employeeId);
		
		firstName = (String) identity.getAttribute(IdentityAttribute.FIRST_NAME);
		middleName = (String) identity.getAttribute(IdentityAttribute.MIDDLE_NAME);
		lastName = (String) identity.getAttribute(IdentityAttribute.LAST_NAME);
		
		if(middleName != null){
			nama = firstName + " " + middleName + " " + lastName;
		}
		
		else if(lastName != null){
			nama = firstName + " " + lastName;
		}
		else{
			nama = firstName;
		}
		
		logger.debug(METHOD_NAME + " return nama " + nama);
		
		return nama;
	}
	
	public static String getFileHalAksesByGroupCode(SailPointContext ctx, String groupCode, String branchCode) throws GeneralException{
		
		String METHOD_NAME = "::getFileHalAksesByGroupCode::";
		
		logger.debug(METHOD_NAME + " trying to get profile with group code " + groupCode);
		
		String fileHalAkses = "";
		
		String groupCodeKey = groupCode;
		
		if(!"0998".equalsIgnoreCase(branchCode) && ("CSO1".equalsIgnoreCase(groupCode) || "OPR1".equalsIgnoreCase(groupCode)))
			groupCodeKey = groupCodeKey + " - CAB";
		
		
		Custom custom = CommonUtil.getCustomObject(ctx, CustomObject.BCA_BASE24_PROFILE_MAP);
		
		if(null == custom){
			logger.debug(METHOD_NAME + " Custom Object " + CustomObject.BCA_BASE24_PROFILE_MAP + " not found");
			return "";
		}
		
		Attributes attr = custom.getAttributes();
		
		Map map = attr.getMap();
		
		fileHalAkses = (String)map.get(groupCodeKey);
		
		if(CommonUtil.isNotEmptyString(fileHalAkses))
			logger.debug(METHOD_NAME + " profile is found, value " + fileHalAkses);
		else
			logger.debug(METHOD_NAME + " profile is not found");
		
		return fileHalAkses;
	}
	
	public static String getBase24Password(SailPointContext ctx) throws GeneralException{
		
		String password = "";
		
		password = CommonUtil.generatePassword(ctx, BCAPasswordPolicyName.BASE24_PASSWORD_POLICY_NAME);
		
		return password != null ? password.toUpperCase() : "";
	}
	
	private static String generateBase24UserIDString(String group, String initialBranch, int seq){
		
		String METHOD_NAME = "::generateBase24UserIDString::";
		
		String delimiter = ".";
		
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String val = "";
		
		val = group + delimiter + initialBranch + getPaddingSequence(seq);
		
		logger.debug(CLASS_NAME + METHOD_NAME + "Value: " + val);

		return val;
	}	
	
	private static String getPaddingSequence(int seq){
		String val = String.valueOf(seq);
		
		if(seq < 10)
			val = "0" + String.valueOf(seq);
		
		return val;
	}
	
	/*public static void createCsvFile(){
		
		File outputFile = new File("/home/spadmin/flatfile/base24output.txt");

		try {
			outputFile.createNewFile();	    
			FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Test content");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		System.out.println("Done");
		
	}*/
}
