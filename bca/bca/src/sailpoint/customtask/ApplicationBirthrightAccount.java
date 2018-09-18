package sailpoint.customtask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.Provisioner;
import sailpoint.api.SailPointContext;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.provisioningpolicy.rule.BCAMasterProvisioningLibrary;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class ApplicationBirthrightAccount extends AbstractTaskExecutor{
	
	Logger logger = Logger.getLogger("sailpoint.customtask.ApplicationBirthrightAccount");

	@Override
	public void execute(SailPointContext context, TaskSchedule arg1,
			TaskResult arg2, Attributes<String, Object> arg3) throws Exception {
		
		String methodName = "::execute::";
		
		QueryOptions localQueryOptions = new QueryOptions();
		
		localQueryOptions.addFilter(Filter.ge("created", new Date(getLastExecutionTime(context))));
		
		logger.debug(methodName + "Preparation to search");
		
		setLastExecutionTime(context);

		Iterator localIterator = context.search(Identity.class, localQueryOptions);
		
		int i = 1;
		
		while(localIterator.hasNext()){
			
			Identity identity = (Identity)localIterator.next();
			
			logger.debug(methodName + " identity no : " + i++ + " : ID : " + (String)identity.getAttribute(IdentityAttribute.EMPLOYEE_ID) + "::" + identity.getAttribute(IdentityAttribute.FIRST_NAME));
		
			List links = identity.getLinks();
			
			boolean hasAd = false;
			
			if(links != null && links.size() > 0){
				Iterator linksIterator = links.iterator();
				
				while(linksIterator.hasNext()){
					Link link = (Link)linksIterator.next();
					
					if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
					//	logger.debug(methodName + " this user already has AD application, with name " + link.getDisplayName());
						hasAd = true;
					}
				}
			}
			
			if(!hasAd && identity.getAttribute(IdentityAttribute.EMPLOYEE_ID) != null){
				logger.debug(methodName + " Employee with ID : " + (String)identity.getAttribute(IdentityAttribute.EMPLOYEE_ID)  + " doesn't have AD account, hence his account domain will be created");
				birthrightToAd(context, identity);
			}
		}
		
	}

	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void birthrightToAd(SailPointContext ctx, Identity identity){
		
		String methodName = "::birthrightToAd::";
		
		String op = "Create";
		
		logger.debug(methodName + " preparation to create account to Active Directory for employee " + identity.getDisplayName());
		
		ProvisioningPlan provisioningPlan = new ProvisioningPlan();
		
		AccountRequest accReq = new AccountRequest();
		
		accReq.setApplication(CommonUtil.AD_APPLICATION);
		accReq.setOperation(AccountRequest.Operation.Create);
								
		AttributeRequest attrObjectType = new AttributeRequest();
		attrObjectType.setName("ObjectType");
		attrObjectType.setValue("User");
		attrObjectType.setOp(ProvisioningPlan.Operation.Add); 
		
		AttributeRequest distinguishedName = new AttributeRequest();
		distinguishedName.setName(ActiveDirectoryAttribute.DISTINGUISHED_NAME);
		distinguishedName.setValue(BCAMasterProvisioningLibrary.getFV_AD_BCA_distinguishedName_Rule(ctx, identity, op));
		distinguishedName.setOp(ProvisioningPlan.Operation.Add);
		
		AttributeRequest sAMAccountName = new AttributeRequest();
		sAMAccountName.setName(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME);
		sAMAccountName.setValue(BCAMasterProvisioningLibrary.getFV_AD_BCA_sAMAccountName_Rule(ctx, identity, op));
		sAMAccountName.setOp(ProvisioningPlan.Operation.Add);
		
		AttributeRequest forceChangePasswdNextLogon = new AttributeRequest();
		
		forceChangePasswdNextLogon.setName(ActiveDirectoryAttribute.PWD_LAST_SET);

		forceChangePasswdNextLogon.setValue(true);

		forceChangePasswdNextLogon.setOp(ProvisioningPlan.Operation.Set);
		
		/*Attributes forceChange = new Attributes();
		
		forceChange.put("generatedPass", true);
		
		forceChange.put("preExpire", true);
		
		password.setArgs(forceChange);*/
		
		
		AttributeRequest memberOf = new AttributeRequest();
		memberOf.setName(ActiveDirectoryAttribute.MEMBER_OF);
		memberOf.setValue("Domain Users");
		
		logger.debug(methodName + " preparation to set attribute request list");
		
		List attrReqList = new ArrayList<AttributeRequest>();
		
		attrReqList.add(attrObjectType);
		
		attrReqList.add(distinguishedName);
		
		attrReqList.add(sAMAccountName);
		
		attrReqList.add(memberOf);
		
		attrReqList.add(forceChangePasswdNextLogon);
	
		accReq.setAttributeRequests(attrReqList);		
		
		provisioningPlan.add(accReq);
		
		provisioningPlan.setIdentity(identity);
		
		try {
			logger.debug(methodName + " plan has been setup, plan should be like this " + provisioningPlan.toXml());
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Provisioner p = new Provisioner(ctx);
		
		logger.debug( methodName + " preparation to compile the plan");
		
		ProvisioningProject project;
		try {
			
			project = p.compile(provisioningPlan);
			
			logger.debug(methodName + " preparation to execute the project");
			
			p.execute(project);
			
		} catch (GeneralException e) {

			e.printStackTrace();
		}
		
		
	}
	
	private static long getLastExecutionTime(SailPointContext context) throws GeneralException{
		
		Custom custom = null;
		
		long lastExecute = 0;
		
		custom = CommonUtil.getCustomObject(context, "BCA Birthright Execution Time");
		
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
		
		return lastExecute;
		
	}
	
	private static void setLastExecutionTime(SailPointContext context) throws GeneralException{
		
		Calendar cal = Calendar.getInstance();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		Custom custom = null;
		
		String lastExecute = "";
		
		custom = CommonUtil.getCustomObject(context, "BCA Birthright Execution Time");
		
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
		
		CommonUtil.updateCustomObject(context, "BCA Birthright Execution Time", attr);
		
	}

}
