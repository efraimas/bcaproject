package sailpoint.customtask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import sailpoint.api.Provisioner;
import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityStatus;
import sailpoint.common.IdentityUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unchecked" })
public class NonEmployeeDomainChecker extends AbstractTaskExecutor{
	
	public static String className = "::NonEmployeeDomainChecker::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.NonEmployeeDomainChecker");

	@Override
	public void execute(SailPointContext ctx,
			TaskSchedule paramTaskSchedule, TaskResult paramTaskResult,
			Attributes<String, Object> attr) throws Exception {
		
		String methodName = "::execute::";
		
		logger.debug(className + methodName + " invoke the method ");
		
		//Initialize Filter
		
		Date now = new Date();
		
		SimpleDateFormat df = new SimpleDateFormat("YYYYMMdd");
		
		String todayString = df.format(now);
		
		logger.debug(className + methodName + " filter identity with end date less than " + todayString);
		
		QueryOptions qo = new QueryOptions();
		
		qo.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.INACTIVE_EMPLOYEE));
		qo.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.TERMINATED_EMPLOYEE));
		qo.add(Filter.eq(IdentityAttribute.IS_HR_MANAGED, "false"));
		qo.add(Filter.le(IdentityAttribute.END_DATE, String.valueOf(now.getTime())));
		//Filter.le
		
		Iterator<Identity> it = IdentityUtil.searchIdentityByQueryOptions(ctx, qo);
		
		
		
		while(it.hasNext()){
			Identity identity = it.next();
			
			logger.debug(className + methodName + " will update identity with id :" + identity.getDisplayName() 
					+ " because his/her enddate is at :" + identity.getAttribute(IdentityAttribute.END_DATE));
			
			boolean status = disabledAdUser(ctx, identity);
			
			if(status){
				logger.debug(className + methodName + " disabled success");
			}else{
				logger.debug(className + methodName + " disabled not success");
			}
		}
		
	}
	
	private boolean disabledAdUser(SailPointContext ctx, Identity identity) throws GeneralException{
		
		String methodName = "::disabledAdUser::";
		
		ProvisioningPlan plan = new ProvisioningPlan();
		
		Link link = IdentityUtil.getAdLink(identity);
		
		if(link == null){
			logger.debug(className + methodName + " AD account is not exists");
			
			return false;
		}
		
		if(link.isDisabled()){
			logger.debug(className + methodName + " AD account already disabled, no followup action needed");
			
			return false;
		}
		
		AccountRequest accReq = new AccountRequest();
		
		accReq.setApplication(CommonUtil.AD_APPLICATION);
		accReq.setNativeIdentity(link.getNativeIdentity());
		accReq.setOperation(AccountRequest.Operation.Disable);
		accReq.setInstance(link.getInstance());
		
		plan.setIdentity(identity);
		
		plan.add(accReq);
		
		logger.debug(className + methodName + " plan should be like this " + plan.toXml());
		
		Provisioner p = new Provisioner(ctx);
		
		logger.debug(className + methodName + " preparation to compile the plan");
		
		ProvisioningProject project = p.compile(plan);
		
		logger.debug(className + methodName + " preparation to execute the project");
		
		p.execute(project);
		
		logger.debug(className + methodName + " project has been executed for link " + link.getDisplayName());
		
		return true;
		
	}

	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*public static void main(String args[]){
		
		Date today = new Date();
		
		System.out.println("Today "  + today);
		System.out.println("in milis : "  + today.getTime());
		
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DATE, -1);
		
		Date yesterday = cal.getTime();
		
		System.out.println("Yesterday "  + yesterday);
		System.out.println("Yesterday in milis : "  + yesterday.getTime());
	}*/

}
