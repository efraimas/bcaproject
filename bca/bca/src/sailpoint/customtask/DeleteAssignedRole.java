package sailpoint.customtask;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.BranchUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.RoleAssignment;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;
@SuppressWarnings({"rawtypes"})
public class DeleteAssignedRole extends AbstractTaskExecutor {
	public static String CLASS_NAME = "::DeleteAssignedRole::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.DeleteAssignedRole");
	
	@Override
	public void execute(SailPointContext context, TaskSchedule arg1, TaskResult arg2, Attributes<String, Object> arg3)
			throws Exception {
		
		// TODO Auto-generated method stub
		String role = "";
		if(arg3.get("role") != null){
			role = (String)arg3.get("role");
			logger.debug(CLASS_NAME + " role that will be processed " + role);
		}else{
			logger.debug(CLASS_NAME + " role argument's not found");
		}
		
		if(arg3.get("branchCode") != null){
			String input = (String)arg3.get("branchCode");
			logger.debug(CLASS_NAME + " branchcode that will be processed " + input);
			
			String branchCode = "";
			if("all".equalsIgnoreCase(input)){
				
				logger.debug(CLASS_NAME + " will process all branch");
				
					List branches = BranchUtil.getAllBranchCode(context);
					
					if(branches != null && branches.size() > 0){
						logger.debug("branch tidak kosong");
						
						int listLength = branches.size();
						for(int bob=0; bob<listLength; bob++){
							branchCode  = (String)branches.get(bob);
							logger.debug(CLASS_NAME + " process branch" + branchCode);
							
							deleteroleAssignments(context, branchCode, role);
						}
					}
			}else{
				String[] arrBranchCode = input.split(",");
				int len = arrBranchCode.length;
				for(int i=0; i<len; i++){
					branchCode = arrBranchCode[i].trim();
					logger.debug(CLASS_NAME + " Branch Code : " + branchCode);
					
					deleteroleAssignments(context, branchCode, role);
				}
			}
		}else{
			logger.debug(CLASS_NAME + " branchCode argument's not found");
		}
	}
	public void deleteroleAssignments(SailPointContext context, String branchCode, String role) throws GeneralException{
		String METHOD_NAME = "::deleteroleAssignments::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside Delete AssignedRole");
		Identity identity = null;
		QueryOptions qo = new QueryOptions();
		List<RoleAssignment> assignedList = null;
		qo.addFilter(Filter.eq(IdentityAttribute.BRANCH_CODE, branchCode));
		Iterator it = context.search(Identity.class, qo);
		while(it.hasNext()){
			identity = (Identity) it.next();
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity : " + identity.getDisplayName());
			try{
				assignedList = identity.getRoleAssignments();
				for(int i = 0; i<assignedList.size(); i++){
					RoleAssignment ra = assignedList.get(i);
					logger.debug(CLASS_NAME + METHOD_NAME + "List of assigned Role : " + ra.getRoleName());
					if(ra.getRoleName().equalsIgnoreCase(role)){
						logger.debug(CLASS_NAME + METHOD_NAME + "Inside Penghapusan Role assigments");
						assignedList.subList(i, i + 1).clear();
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
		identity.setRoleAssignments(assignedList);
		context.saveObject(identity);
		context.commitTransaction();
		context.decache();
	}
	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		// TODO Auto-generated method stub
		return false;
	}

}
