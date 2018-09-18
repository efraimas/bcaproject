package sailpoint.customtask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.Base24Attribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.object.ApprovalItem;
import sailpoint.object.ApprovalSet;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.Filter.MatchMode;
import sailpoint.object.IdentityRequest;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.object.WorkflowSummary.ApprovalSummary;
import sailpoint.provisioningpolicy.rule.Base24Provisioning;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class GenerateBase24OutputFile extends AbstractTaskExecutor {

	public static String CLASS_NAME = "::GenerateBase24OutputFile::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.GenerateBase24OutputFile");

	private static String DATE_FORMAT = "yyyyMMdd";
	private static String DATE_FORMAT_TEMPPASSWORD = "dd-MMM-yyyy";
	private static String DELIMITER = ",";

	@Override
	public void execute(SailPointContext context, TaskSchedule arg1,
			TaskResult arg2, Attributes<String, Object> attributes) throws Exception {
		
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String fileName = "";
		String fileNameBak = "";
		String identityRequestId = "";
		List inputAttributeListKey = attributes.getKeys();
		
		logger.debug("map attribute argument : " + attributes);
		logger.debug("map attribute argument : " + attributes.getKeys());
		Iterator it = inputAttributeListKey.iterator();
		while (it.hasNext()) {
			String keyName = (String) it.next();
			String keyValue = attributes.getString(keyName);

			if (keyName.equalsIgnoreCase("outputFileLocation")) {
				fileName = keyValue;
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "File Location Found...." + fileName);
			}
			
			if (keyName.equalsIgnoreCase("outputBakFileLocation")) {
				
				fileNameBak = keyValue;
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "File Location Found...." + fileNameBak);
			}
			
			if (keyName.equalsIgnoreCase("identityRequest")) {
				identityRequestId = keyValue;
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "Request ID : " + identityRequestId);
			}
		}
		
		
		Map<String, String> newUserIdBase24Map = new HashMap<String, String>();			
		
		QueryOptions localQueryOptions = new QueryOptions();			

		localQueryOptions.addFilter(Filter.gt("modified", new Date(getLastExecutionTime(context))));
		
		logger.debug(CLASS_NAME + " Last Modified Date will be " + getLastExecutionTime(context));
		
		localQueryOptions.addFilter(Filter.or(Filter.eq("application", (String)"Base24 File Feed"), Filter.like("value", "Base24", MatchMode.START)));
		
		localQueryOptions.addFilter(Filter.eq("approvalState", (String)"Finished"));
		
		
		///comment. cause in item have no detailed id will find it later at loop.
/*		if(!"".equalsIgnoreCase(identityRequestId) && identityRequestId != null){
			localQueryOptions.addFilter(Filter.eq("identityRequest", identityRequestId));
		}*/
		
		
		//localQueryOptions.addFilter(Filter.eq("provisioningState", (String)"Commited"));				
		
		logger.debug(METHOD_NAME + "Preparation to search");
		
		//setLastExecutionTime(context);

		logger.debug(METHOD_NAME + "Your Query Option will be like this " + localQueryOptions);
		
		Iterator localIterator = context.search(IdentityRequestItem.class, localQueryOptions);
		
		logger.debug(METHOD_NAME + "Preparation to enter loop 1");
		
		String content = "";
		boolean fromWorkflow = false;
		if(!"".equalsIgnoreCase(identityRequestId) && identityRequestId != null) {
				fromWorkflow = true;
		}
		
		while(localIterator.hasNext()){
			
			IdentityRequestItem rqItem = (IdentityRequestItem)localIterator.next();
			logger.debug(CLASS_NAME + METHOD_NAME + "request item xml : " + rqItem.toXml());
			IdentityRequest rq = rqItem.getIdentityRequest();
			logger.debug(CLASS_NAME + METHOD_NAME + "request xml :" + rq.toXml());
			String value = (String)rqItem.getValue();
			String status = rqItem.getProvisioningState().toString();
			String operation = rqItem.getOperation();
			String userId = "";
			logger.debug(METHOD_NAME + " employeeid " + rq.getTargetDisplayName() + " value or apps : " + value + " status : " + status + " operation : " + operation + " last modified : " + rq.getModified());
			
			String employeeId = rq.getTargetDisplayName();
			String group = "";
			
			if("add".equalsIgnoreCase(operation) || "remove".equalsIgnoreCase(operation)){
				
				group = rqItem.getValue() != null ? rqItem.getValue().toString().substring(7) : ""; // ex, Role name = Base24 CSO1, group = CSO1
				
				logger.debug(METHOD_NAME + " operation " + operation + " group " + group);
				
				group = (group.length() > 0 && group.indexOf("-") >= 0) ? group.substring(0, group.indexOf("-")).trim() : group; //ex : CSO1 - KP => CSO1 
			}else{
				
				group = rqItem.getNativeIdentity() != null ? rqItem.getNativeIdentity().substring(0, rqItem.getNativeIdentity().indexOf(".")) : ""; // ex, Role name = Base24 CSO1, group = CSO1
				
				logger.debug(METHOD_NAME + " operation " + operation + " group " + group);
				
				group = (group.length() > 0 && group.indexOf("-") >= 0) ? group.substring(0, group.indexOf("-")).trim() : group; //ex : CSO1 - KP => CSO1 
			}
			
			if(fromWorkflow) {
				if(identityRequestId.equalsIgnoreCase(rq.getName())) {
					//Add Last Action Date
					logger.debug(METHOD_NAME + " add last action date to content");
					
					if("set".equalsIgnoreCase(operation)){
						content += addDoubleQuotes("Reset" + "-" + new SimpleDateFormat(DATE_FORMAT).format(rq.getModified()));
					}else{
						content += addDoubleQuotes(operation + "-" + new SimpleDateFormat(DATE_FORMAT).format(rq.getModified()));
					}
					
					//Add Wilayah
					logger.debug(METHOD_NAME + " add wilayah to content");
					content += DELIMITER + addDoubleQuotes(Base24Provisioning.getWilayahFromEmployeeId(context, employeeId));
					
					//Add Cabut
					logger.debug(METHOD_NAME + " add cabut to content");
					content += DELIMITER + addDoubleQuotes(Base24Provisioning.getCabutByEmployeeId(context, employeeId));
					
					//Add Kodec
					logger.debug(METHOD_NAME + " kodec to content");
					String kodec = Base24Provisioning.getKodecByEmployeeId(context, employeeId);
					content += DELIMITER + addDoubleQuotes(kodec);
					
					//Add USER-ID
					logger.debug(METHOD_NAME + " add user-id to content");
					
					//For user KP, with branch code = 0998
					if("add".equalsIgnoreCase(operation) || "remove".equalsIgnoreCase(operation)){
						
						if(kodec != null && CommonUtil.isNotEmptyString(kodec) && "0998".equalsIgnoreCase(kodec)){
							userId = group + ".u" + employeeId.substring(2);
							content += DELIMITER + addDoubleQuotes(userId);
						}else{
							String initialBranch = Base24Provisioning.getInitialFromBranchCode(context, kodec);
							logger.debug(METHOD_NAME + "branch code = " + kodec + ", initial branch " + initialBranch);
							
							userId = rqItem.getNativeIdentity();
							
							if(userId == null || "".equalsIgnoreCase(userId) || !CommonUtil.isUniqueApplicationAttributeValue(context,
						CommonUtil.BASE24_FILE_FEED_APPLICATION, Base24Attribute.USER_ID, userId)){
								userId = Base24Provisioning.getNextSequenceByGroup(context, group , initialBranch);
							}
							
							content += DELIMITER + addDoubleQuotes(userId);
						}
						
						if("add".equalsIgnoreCase(operation) && !userId.equalsIgnoreCase(rqItem.getNativeIdentity())){
							newUserIdBase24Map.put(rqItem.getId(), userId);
						}
						
					}else{
						content += DELIMITER + addDoubleQuotes(rqItem.getNativeIdentity());
					}
					
					
					//Add group
					logger.debug(METHOD_NAME + " add group to content");
					content += DELIMITER + addDoubleQuotes(Base24Provisioning.getGroupNumberByGroupCode(context, group));
					
					//Add User
					logger.debug(METHOD_NAME + " add user to content");		
					/*if("add".equalsIgnoreCase(operation)){*/
					content += DELIMITER + addDoubleQuotes(Base24Provisioning.getUserFromBase24Sequence(context, group));
					/*}else{
						content += DELIMITER + addDoubleQuotes(null);
					}*/
					
					//Add NIP
					logger.debug(METHOD_NAME + " add NIP to content");
					if (employeeId.contains("CADANGAN") || employeeId.contains("cadangan")){
						content += DELIMITER + addDoubleQuotes(employeeId);
					} else {
						content += DELIMITER + addDoubleQuotes(employeeId.substring(2));
					}
					
					
					//Add Nama
					logger.debug(METHOD_NAME + " add NAMA to content");
					content += DELIMITER + addDoubleQuotes(Base24Provisioning.getNamaUserByEmployeeId(context, employeeId));
					
					//Add File Akses
					logger.debug(METHOD_NAME + " add File Hal Akses to content");
					content += DELIMITER + ("Add".equalsIgnoreCase(operation) ? addDoubleQuotes(Base24Provisioning.getFileHalAksesByGroupCode(context, group, kodec)) : addDoubleQuotes(""));
					
					//Add Password
					String password = "Add".equalsIgnoreCase(operation) || "Set".equalsIgnoreCase(operation) ? Base24Provisioning.getBase24Password(context) : "";
						content += DELIMITER + addDoubleQuotes(password);
					
					
					
					logger.debug(METHOD_NAME + " add Password to content :" + password);
					
					//Add Tanggal for TempPassword
					String tanggal = new SimpleDateFormat(DATE_FORMAT_TEMPPASSWORD).format(rq.getModified());
					
					//Add identity for TempPassword
					Identity identity = context.getObjectByName(Identity.class, employeeId.trim());
					
					if(password != null && !"".equalsIgnoreCase(password)){
						try{
							if ("set".equalsIgnoreCase(operation)) {
								//get application name in temp password
								String application = null;
								List <Map> listMap = null;
								Map <Map, Map> tempMap = new HashMap<>();
								
								try{
									listMap = (List) identity.getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
									for (Map nm : listMap) {
										tempMap.putAll(nm);
									}
								}catch (Exception e) {
									tempMap = (Map) identity.getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
								}
								
								Map userMap = tempMap.get(rqItem.getNativeIdentity());
								
								if(userMap != null) {
									application = (String) userMap.get("aplikasi");
								}
								
								logger.debug(CLASS_NAME + METHOD_NAME + "Inside try to update to TempPassword for Reset Password");
								IdentityUtil.updateTempPasswordIdentityForResetPassword(context, identity, application, rqItem.getNativeIdentity(), tanggal, password);
							} else {
								logger.debug(CLASS_NAME + METHOD_NAME + "Inside try to update to TempPassword for Get Password");
								IdentityUtil.updateTempPasswordIdentityForGetPassword(context, identity, value, userId, tanggal, password);
							}
						}catch (Exception e) {
							logger.debug(CLASS_NAME + METHOD_NAME + "Failed to update to TempPassword ");
						}
					}
					
					//Add revoke date dan resume date untuk disabled
					String revokeDate = "";
					String resumeDate = "";
					
					if("disable".equalsIgnoreCase(operation)){
						
						Attributes attr = rqItem.getAttributes();
						
						 revokeDate = attr.get("REVOKE_DATE") != null ? attr.get("REVOKE_DATE").toString() : "";
						 resumeDate = attr.get("RESUME_DATE") != null ? attr.get("RESUME_DATE").toString() : "";
					}
					
					content += DELIMITER + addDoubleQuotes(revokeDate);
					content += DELIMITER + addDoubleQuotes(resumeDate) + "\n";
					break;
				}
			} else {
				//Add Last Action Date
				logger.debug(METHOD_NAME + " add last action date to content");
				
				if("set".equalsIgnoreCase(operation)){
					content += addDoubleQuotes("Reset" + "-" + new SimpleDateFormat(DATE_FORMAT).format(rq.getModified()));
				}else{
					content += addDoubleQuotes(operation + "-" + new SimpleDateFormat(DATE_FORMAT).format(rq.getModified()));
				}
				
				//Add Wilayah
				logger.debug(METHOD_NAME + " add wilayah to content");
				content += DELIMITER + addDoubleQuotes(Base24Provisioning.getWilayahFromEmployeeId(context, employeeId));
				
				//Add Cabut
				logger.debug(METHOD_NAME + " add cabut to content");
				content += DELIMITER + addDoubleQuotes(Base24Provisioning.getCabutByEmployeeId(context, employeeId));
				
				//Add Kodec
				logger.debug(METHOD_NAME + " kodec to content");
				String kodec = Base24Provisioning.getKodecByEmployeeId(context, employeeId);
				content += DELIMITER + addDoubleQuotes(kodec);
				
				//Add USER-ID
				logger.debug(METHOD_NAME + " add user-id to content");
				
				//For user KP, with branch code = 0998
				if("add".equalsIgnoreCase(operation) || "remove".equalsIgnoreCase(operation)){
					
					if(kodec != null && CommonUtil.isNotEmptyString(kodec) && "0998".equalsIgnoreCase(kodec)){
						userId = group + ".u" + employeeId.substring(2);
						content += DELIMITER + addDoubleQuotes(userId);
					}else{
						String initialBranch = Base24Provisioning.getInitialFromBranchCode(context, kodec);
						logger.debug(METHOD_NAME + "branch code = " + kodec + ", initial branch " + initialBranch);
						
						userId = rqItem.getNativeIdentity();
						
						if(userId == null || "".equalsIgnoreCase(userId) || !CommonUtil.isUniqueApplicationAttributeValue(context,
					CommonUtil.BASE24_FILE_FEED_APPLICATION, Base24Attribute.USER_ID, userId)){
							userId = Base24Provisioning.getNextSequenceByGroup(context, group , initialBranch);
						}
						
						content += DELIMITER + addDoubleQuotes(userId);
					}
					
					if("add".equalsIgnoreCase(operation) && !userId.equalsIgnoreCase(rqItem.getNativeIdentity())){
						newUserIdBase24Map.put(rqItem.getId(), userId);
					}
					
				}else{
					content += DELIMITER + addDoubleQuotes(rqItem.getNativeIdentity());
				}
				
				
				//Add group
				logger.debug(METHOD_NAME + " add group to content");
				content += DELIMITER + addDoubleQuotes(Base24Provisioning.getGroupNumberByGroupCode(context, group));
				
				//Add User
				logger.debug(METHOD_NAME + " add user to content");		
				/*if("add".equalsIgnoreCase(operation)){*/
				content += DELIMITER + addDoubleQuotes(Base24Provisioning.getUserFromBase24Sequence(context, group));
				/*}else{
					content += DELIMITER + addDoubleQuotes(null);
				}*/
				
				//Add NIP
				logger.debug(METHOD_NAME + " add NIP to content");
				if (employeeId.contains("CADANGAN") || employeeId.contains("cadangan")){
					content += DELIMITER + addDoubleQuotes(employeeId);
				} else {
					content += DELIMITER + addDoubleQuotes(employeeId.substring(2));
				}
				
				
				//Add Nama
				logger.debug(METHOD_NAME + " add NAMA to content");
				content += DELIMITER + addDoubleQuotes(Base24Provisioning.getNamaUserByEmployeeId(context, employeeId));
				
				//Add File Akses
				logger.debug(METHOD_NAME + " add File Hal Akses to content");
				content += DELIMITER + ("Add".equalsIgnoreCase(operation) ? addDoubleQuotes(Base24Provisioning.getFileHalAksesByGroupCode(context, group, kodec)) : addDoubleQuotes(""));
				
				//Add Password
				
				String password = "Add".equalsIgnoreCase(operation) || "Set".equalsIgnoreCase(operation) ? Base24Provisioning.getBase24Password(context) : "";
				content += DELIMITER + addDoubleQuotes(password);
				
				logger.debug(METHOD_NAME + " add Password to content :" + password);
				
				//Add Tanggal for TempPassword
				String tanggal = new SimpleDateFormat(DATE_FORMAT_TEMPPASSWORD).format(rq.getModified());
				
				//Add identity for TempPassword
				Identity identity = context.getObjectByName(Identity.class, employeeId.trim());
				
				if(password != null && !"".equalsIgnoreCase(password)){
					try{
						if ("set".equalsIgnoreCase(operation)) {
							//get application name in temp password
							String application = null;
							List <Map> listMap = null;
							Map <Map, Map> tempMap = new HashMap<>();
							
							try{
								listMap = (List) identity.getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
								for (Map nm : listMap) {
									tempMap.putAll(nm);
								}
							}catch (Exception e) {
								tempMap = (Map) identity.getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
							}
							
							Map userMap = tempMap.get(rqItem.getNativeIdentity());
							
							if(userMap != null) {
								application = (String) userMap.get("aplikasi");
							}
							
							logger.debug(CLASS_NAME + METHOD_NAME + "Inside try to update to TempPassword for Reset Password");
							IdentityUtil.updateTempPasswordIdentityForResetPassword(context, identity, application, rqItem.getNativeIdentity(), tanggal, password);
						} else {
							logger.debug(CLASS_NAME + METHOD_NAME + "Inside try to update to TempPassword for Get Password");
							IdentityUtil.updateTempPasswordIdentityForGetPassword(context, identity, value, userId, tanggal, password);
						}
					}catch (Exception e) {
						logger.debug(CLASS_NAME + METHOD_NAME + "Failed to update to TempPassword ");
						// TODO: handle exception
					}
				}
				
				//Add revoke date dan resume date untuk disabled
				String revokeDate = "";
				String resumeDate = "";
				
				if("disable".equalsIgnoreCase(operation)){
					
					Attributes attr = rqItem.getAttributes();
					
					 revokeDate = attr.get("REVOKE_DATE") != null ? attr.get("REVOKE_DATE").toString() : "";
					 resumeDate = attr.get("RESUME_DATE") != null ? attr.get("RESUME_DATE").toString() : "";
				}
				
				content += DELIMITER + addDoubleQuotes(revokeDate);
				content += DELIMITER + addDoubleQuotes(resumeDate) + "\n";
				
			}

		}
		//Untuk menambahkan header
		//content = Base24FileFeedConverter.lineHeader + "\n" + content;
		if(writeFile(fileName, content)){
			writeFile(fileNameBak, content);
			updateIdentityRequestItem(context, newUserIdBase24Map);
		}
		
		setLastExecutionTime(context);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " Base 24 Last Execution Time will be " + getLastExecutionTime(context));
	}
	
	private void updateIdentityRequestItem(SailPointContext ctx, Map<String, String> map){
		
		String methodName = "::updateIdentityRequestItem::";
		
		Set<String> set = map.keySet();
		
		Iterator<String> it = set.iterator();
		
		while(it.hasNext()){
			
			String id = (String)it.next();
			String userId = (String)map.get(id);
			
			IdentityRequestItem rItem = null;
			try {
				rItem = ctx.getObjectById(IdentityRequestItem.class, id);
				rItem.setNativeIdentity(userId);
				
				logger.debug(CLASS_NAME + methodName + " rItemId " + id + " userid " + userId);
				
				ctx.saveObject(rItem);
				
				ctx.commitTransaction();
				
				logger.debug(CLASS_NAME + methodName + " the object has been saved");
			} catch (GeneralException e) {
				
				e.printStackTrace();
			}
			
		}
		
	}
	
	public boolean writeFile(String filePath, String content){
		File outputFile = new File(filePath);
		
		logger.debug(CLASS_NAME + " mulai menulis file dengan content : " + content + "file path " + outputFile.toString());
		
		FileWriter fw = null;
			try {
				if(!outputFile.exists()){
					outputFile.createNewFile();
					content = Base24FileFeedConverter.lineHeader + "\n" + content;
					fw = new FileWriter(outputFile.getAbsoluteFile());
				}else{
					fw = new FileWriter(outputFile.getAbsoluteFile(), true);
				}
				  
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		logger.debug(CLASS_NAME + " selesai menulis file ");
		System.out.println("Done");
		
		return true;
	}

	@Override
	public boolean terminate() {

		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		return false;
	}

	private static String addDoubleQuotes(String var) {
		return "\"" + var + "\"";
	}
	
	private static long getLastExecutionTime(SailPointContext context) throws GeneralException{
		
		Custom custom = null;
		
		long lastExecute = 0;
		
		custom = CommonUtil.getCustomObject(context, "BASE 24 Execution Time");
		
		if(custom != null){
			Map keyMap = custom.getAttributes().getMap();
			if(keyMap != null && keyMap.get("timeInMilis") != null){
				lastExecute = Long.parseLong((String)keyMap.get("timeInMilis"));
			}
		}else{
			
			Calendar cal = Calendar.getInstance();
			
			cal.add(Calendar.DATE, -60);
			
			lastExecute = cal.getTimeInMillis();
		}
		
		//Untuk sementara dibuat seperti ini dulu
		/*
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DATE, -60);
		
		lastExecute = cal.getTimeInMillis();*/
		
		return lastExecute;
		
	}
	
	@SuppressWarnings("unchecked")
	private static void setLastExecutionTime(SailPointContext context) throws GeneralException{
		
		Calendar cal = Calendar.getInstance();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		Custom custom = null;
		
		custom = CommonUtil.getCustomObject(context, "BASE 24 Execution Time");
		
		Attributes attr = null;
		
		Map keyMap = null;
		
		if(custom != null){
			
			attr = custom.getAttributes();
			
			keyMap = attr.getMap();
			
		}else{
			
			custom = new Custom();
			
			attr = new Attributes();
			
			keyMap = new HashMap();

		}	
		
		long timeInMilis = cal.getTimeInMillis();
		
		keyMap.put("timeInMilis", String.valueOf(timeInMilis));
		keyMap.put("dateFormat", dateFormat.format(timeInMilis)); 
		
		attr.putAll(keyMap);
		
		CommonUtil.updateCustomObject(context, "BASE 24 Execution Time", attr);
		
	}
	
	/*public static void main(String args[]){
		
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DATE, -1);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		System.out.println("Date : " + dateFormat.format(cal.getTime()) + " in milis " + cal.getTimeInMillis());
		
		String uid = "CSO1.GDA01";
		
		String group = uid.substring(0, uid.indexOf("."));
		
		System.out.println(group);
		
	}*/
}
