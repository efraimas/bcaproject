package sailpoint.customtask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Capability;
import sailpoint.object.Identity;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

public class GenerateDivisionKabiroAdvisor extends AbstractTaskExecutor{
	
	public static String CLASS_NAME = "::GenerateDivisionKabiroAdvisor::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.GenerateDivisionKabiroAdvisor");

	@Override
	public void execute(SailPointContext arg0, TaskSchedule arg1,
			TaskResult arg2, Attributes<String, Object> arg3) throws Exception {
		
		String METHOD_NAME = "::execute::";
		
		@SuppressWarnings("unchecked")
		Iterator<Identity> echelon3Emp = IdentityUtil.searchIdentity(arg0, setKantorPusatFilterAttribute("S3"), true);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " preparation to processed echelon S3");
		String groupMail = null;
		Capability capability = null;
		createUpdateWorkGroup(echelon3Emp, arg0, groupMail, capability);
		
		@SuppressWarnings("unchecked")
		Iterator<Identity> echelon2Emp = IdentityUtil.searchIdentity(arg0, setKantorPusatFilterAttribute("S2"), true);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " preparation to processed echelon S2");
		
		createUpdateWorkGroup(echelon2Emp, arg0, groupMail, capability);
		
		@SuppressWarnings("unchecked")
		Iterator<Identity> echelon1Emp = IdentityUtil.searchIdentity(arg0, setKantorPusatFilterAttribute("S1"), true);
		
		
		logger.debug(CLASS_NAME + METHOD_NAME + " preparation to processed echelon S1");
		
		createUpdateWorkGroup(echelon1Emp, arg0, groupMail, capability);
		
			
	}
	
	private static void createUpdateWorkGroup(Iterator<Identity> it, SailPointContext context, String groupMail, Capability capability) throws GeneralException{
		
		String METHOD_NAME = "::createUpdateWorkGroup::";
		
		int counter = 0;
		
		if(it.hasNext()){
					
			Identity identity = (Identity)it.next();
			
			String divisionCode = (String)identity.getAttribute(IdentityAttribute.DIVISION_CODE);
			String divisionName = (String)identity.getAttribute(IdentityAttribute.DIVISION_NAME);
			
			logger.debug(CLASS_NAME + METHOD_NAME + " Employee : " + identity.getAttribute(IdentityAttribute.SALUTATION_NAME) + " echelon " + identity.getAttribute(IdentityAttribute.ECHELON));
						
			String workGroupName = "Divisi " + divisionCode + " Echelon 3-2-1";
			String workGroupDescription = "Group approver divisi " + divisionName + " dengan eselon minimal 3";
			
			logger.debug(CLASS_NAME + METHOD_NAME + " Workgroupname : " + workGroupName + " desc " + workGroupDescription);
						
			WorkGroupCreator.createUpdateWorkGroup(identity, context, workGroupName, workGroupDescription, groupMail, capability);
			
			counter++;
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + " jumlah employee yang di proses " + counter);
	}

	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private static Map<String, String> setKantorPusatFilterAttribute(String echelon){
		Map<String, String> args = new HashMap<String, String>();
		
		args.put(IdentityAttribute.BRANCH_CODE, "0998");
		args.put(IdentityAttribute.ECHELON, echelon);
		
		return args;
	}

}
