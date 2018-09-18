package sailpoint.customtask;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Identity;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

@SuppressWarnings({ "rawtypes" })
public class SystemApplicationOwner extends AbstractTaskExecutor{
	
	static String className = "::SystemApplicationOwner::";
	
	private static Logger logger = Logger.getLogger("sailpoint.customtask.SystemApplicationOwner");

	@Override
	public void execute(SailPointContext context, TaskSchedule arg1,
			TaskResult arg2, Attributes<String, Object> arg3) throws Exception {
		// TODO Auto-generated method stub
		
		String methodName = "::execute::";
		
		Custom custom = CommonUtil.getCustomObject(context, CustomObject.BCA_SYSTEM_APPLICATION_OWNER);
		
		if(custom != null){
			
			logger.debug(className + methodName + " custom object ditemukan");
			
			Attributes attr = custom.getAttributes();
			
			List lst = attr.getList("application list");
			
			Iterator it = lst.iterator();
			
			while(it.hasNext()){
				
				Map map = (Map)it.next();
				
				String wgManager = (String)map.get("manager");
				
				Identity manager = CommonUtil.getWorkgroupFromWorkGroupName(context, wgManager);
				
				if(manager != null){
					
					logger.debug(className + methodName + " workgroup untuk nama " + wgManager + " ditemukan");
					
					Identity systemOwner = null;
					
					String identityName = (String)map.get("name");
					
					String branchCode = (String)map.get("branchCode");
					
					systemOwner = CommonUtil.searchIdentity(context, IdentityAttribute.NAME, identityName);
					
					if(systemOwner == null){
						systemOwner = new Identity();
						systemOwner.setName(identityName);
						systemOwner.setDisplayName(identityName);
					}
					
					systemOwner.setManager(manager);
					
					systemOwner.setAttribute(IdentityAttribute.BRANCH_CODE, branchCode);
					
					context.saveObject(systemOwner);
					
					context.commitTransaction();
					
				}else{
					logger.debug(className + methodName + " workgroup untuk nama " + wgManager + " tidak ditemukan");
				}
				
			}
		}else{
			logger.debug(className + methodName + " custom object tidak ditemukan");
		}
				
	}

	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		return false;
	}

}
