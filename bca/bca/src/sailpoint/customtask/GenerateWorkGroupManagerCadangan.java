package sailpoint.customtask;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.BcaConstantCode;
import sailpoint.common.BranchUtil;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Capability;
import sailpoint.object.Identity;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

@SuppressWarnings({ "unused", "rawtypes" })
public class GenerateWorkGroupManagerCadangan extends AbstractTaskExecutor{
	
	private static Logger logger = Logger.getLogger("sailpoint.customtask.GenerateWorkGroupManagerCadangan");
	static String className = ":::GenerateWorkGroupManagerCadangan:::";
	private static final String KABAG_KCP_TYPE_A = "00005207";
	private static final String KABAG_KCP_TYPE_B_C = "00005207";
	private static final String KALAY_KPO_KCU = "00220260,00005291,00005199";
	private static final String KAUR_KEU_HR_KANWIL = "00001028,00470437";

	@Override
	public void execute(SailPointContext context, TaskSchedule taskSchedule,
			TaskResult taskResult, Attributes<String, Object> attributes) throws Exception {
		
		String methodName = "::execute::";
		logger.debug(className + methodName + " get branch list");

		String groupMail = null ;
		Capability capability = null;
		List branchList = BranchUtil.getAllBranchCode(context);
		
		if(branchList != null && branchList.size() > 0){

			logger.debug(className + methodName + " branchList is greater than zero");
			
			int len = branchList.size();
			
			for(int i=0; i<len; i++){
				
				Iterator it = null;
				
				String branchCode = (String)branchList.get(i);
				String workGroupName = "Manager Cadangan " + branchCode;
				String workGroupDescription = " Merupakan workgroup untuk manager cadangan dengan kode " + branchCode;
				
				if(CommonUtil.getWorkgroupFromWorkGroupName(context, workGroupName) == null){
					
					logger.debug(className + methodName + " memproses kode cabang " + branchCode);
					
					String branchType = BranchUtil.getBranchType(context, branchCode);
					logger.debug(className + methodName + "tipe branch nya : " + branchType);
					
					if("KCP".equalsIgnoreCase(branchType)){
						it = CommonUtil.searchActiveIdentityByBranchByPosition(context, branchCode, KABAG_KCP_TYPE_A);
						if(it != null && it.hasNext()){				
							logger.debug(className + methodName + " wapim untuk KCP " + branchCode + " dengan kode posisi " + KABAG_KCP_TYPE_A + " ditemukan ");
							WorkGroupCreator.createUpdateWorkGroup(it, context, workGroupName, workGroupDescription, groupMail, capability);		
							
						}else{
							it = CommonUtil.searchActiveIdentityByBranchByPosition(context, branchCode, KABAG_KCP_TYPE_B_C);
							if(it != null && it.hasNext()){				
								logger.debug(className + methodName + " pimpinan untuk KCP " + branchCode + " dengan kode posisi " + KABAG_KCP_TYPE_B_C + " ditemukan ");
								WorkGroupCreator.createUpdateWorkGroup(it, context, workGroupName, workGroupDescription, groupMail, capability);			
							}
						}
					}else if("KCU".equalsIgnoreCase(branchType)){
						it = CommonUtil.searchActiveIdentityByBranchByPosition(context, branchCode, KALAY_KPO_KCU);//end
						if(it != null && it.hasNext()){				
							logger.debug(className + methodName + " KOC untuk KCU " + branchCode + " dengan kode posisi " + KALAY_KPO_KCU  + " ditemukan ");
							WorkGroupCreator.createUpdateWorkGroup(it, context, workGroupName, workGroupDescription, groupMail, capability);			
						}
					}else if("Region".equalsIgnoreCase(branchType)){
						it = CommonUtil.searchActiveIdentityByBranchByPosition(context, branchCode, KAUR_KEU_HR_KANWIL);
						if(it != null && it.hasNext()){				
							logger.debug(className + methodName + " KOW untuk Kanwil " + branchCode + " dengan kode posisi " + KAUR_KEU_HR_KANWIL  + " ditemukan ");
							WorkGroupCreator.createUpdateWorkGroup(it, context, workGroupName, workGroupDescription, groupMail, capability);			
						}
					}else{
						Identity identity = CommonUtil.searchIdentity(context, IdentityAttribute.EMPLOYEE_ID, "00054403");
						logger.debug(className + methodName + "Identity untuk jadi manager cadangan ditemukan : " + identity);
						
						workGroupName = "Manager Cadangan Kantor Pusat";
						workGroupDescription = "Merupakan workgroup untuk manager cadangan kantor pusat";
						
						WorkGroupCreator.createUpdateWorkGroup(identity, context, workGroupName, workGroupDescription, groupMail, capability);
					}
				}	
			}
			
		}else{
			logger.debug(className + methodName + " branchList is empty");
		}
	}

	@Override
	public boolean terminate() {
		String methodName = "::terminate::";
		logger.debug(className + methodName + "Inside ...");
		return false;
	}

}
