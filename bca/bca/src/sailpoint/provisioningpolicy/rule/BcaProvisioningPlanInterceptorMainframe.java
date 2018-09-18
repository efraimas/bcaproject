package sailpoint.provisioningpolicy.rule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.Provisioner;
import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.common.MainframeUtil;
import sailpoint.common.RACFAttribute;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;

import sailpoint.object.QueryOptions;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningProject;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BcaProvisioningPlanInterceptorMainframe {

	public static String className = "::BcaProvisioningPlanInterceptorMainframe::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.BcaProvisioningPlanInterceptorMainframe");

	public static void planInterceptor(SailPointContext context, ProvisioningPlan plan) throws GeneralException {

		String methodName = "::planInterceptor::";
		logger.debug(className + methodName + " Enter the method");

		String tanggal;
		String accountId = "";
		String employeeId = "";
		String password = "";
		String application = "";
		String operation = "";
		String tempRole = "";
		String revokeDate = "";
		String resumeDate = "";

		// Date System
		Date today = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-YYYY");
		tanggal = df.format(today);

		employeeId = plan.getNativeIdentity();
		logger.debug(className + methodName + " get the identity");
		Identity identity = null;

		if (employeeId.contains("CADANGAN")) {
			logger.debug(className + methodName + "Inside... cadangan");

			QueryOptions qo = new QueryOptions();
			Filter localFilter = Filter.eq(IdentityAttribute.NAME, employeeId);
			qo.addFilter(localFilter);
			Iterator localIterator = context.search(Identity.class, qo);
			identity = (Identity) localIterator.next();

		} else {
			logger.debug(className + methodName + "Inside...bukan cadangan");
			identity = IdentityUtil.searchActiveIdentityById(context, employeeId);
		}

		//logger.debug(className + methodName + " get the employee name" + identity.toXml());
		//logger.debug(className + methodName + " The Plan : " + plan.toXml());

		List<AccountRequest> lst = plan.getAccountRequests();
		Iterator it = lst.iterator();

		while (it.hasNext()) {

			AccountRequest req = (AccountRequest) it.next();
			operation = req.getOp().toString();

			//Pembuatan
			if ("Create".equalsIgnoreCase(operation)) {
				logger.debug(className + methodName + " The Request operation is " + operation);

				if (CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(req.getApplicationName())) {

					ArrayList<String> al = new ArrayList<String>();
					al.add(operation);
					for (int i = 0; i < al.size(); i++) {
						logger.debug("Total Looping update tempPassword : " + i);
						String roleName = req.getSourceRole();
						logger.debug(className + methodName + "ini xml rolenya : " + req.toXml());

						if (roleName != null) {
							try {
								application = MainframeUtil.getBcaMainframeApplication(context, roleName);
							} catch (Exception e) {
								logger.debug("failed to application");
							}
							accountId = req.getNativeIdentity();
						}

						try {
							password = (String) req.getAttributeRequest(RACFAttribute.PASSWORD).getValue();
						} catch (Exception e) {
							logger.debug("failed to get attribute password");
						}

						if (password != null && !"".equalsIgnoreCase(password)) {
							try {
								IdentityUtil.updateTempPasswordIdentityForGetPassword(context, identity, application, accountId, tanggal, password);
							} catch (Exception e) {
								logger.debug("failed to update temp password");
							}
						}
					}
				} else {
					logger.debug(className + methodName + " Application name is : " + req.getApplication());
				}

			} else if ("Enable".equalsIgnoreCase(operation)){
				logger.debug(className + methodName + " The Request operation is " + operation);
				
				
				/*accountId = req.getNativeIdentity();
				AccountRequest accReq = new AccountRequest();
				ProvisioningPlan newplan = new ProvisioningPlan();
				AttributeRequest attReq = new AttributeRequest();
				List attrReqList = new ArrayList();
				
				accReq.setOperation(AccountRequest.Operation.Modify);
				
				accReq.setApplication(req.getApplication());
				
				accReq.setInstance(req.getInstance());
				
				accReq.setNativeIdentity(accountId);
				
				attReq.setName("RESUME_DATE");
				attReq.setOp(ProvisioningPlan.Operation.Set);
				attReq.setValue("NORESUME");
				
				attrReqList.add(attReq);							
				accReq.setAttributeRequests(attrReqList);
			
				if(accReq != null){
					try{
						logger.debug(className + methodName + "AccessRequest in plan inteceptor" + accReq.toXml());
					}catch (Exception e) {
						// TODO: handle exception
					}
					newplan.add(accReq);
				}
				
				newplan.setIdentity(identity);
				
				try {
					logger.debug(methodName + " plan has been setup, plan should be like this " + newplan.toXml());
				} catch (GeneralException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				Provisioner p = new Provisioner(context);
				
				logger.debug( methodName + " preparation to compile the plan");
				
				ProvisioningProject project;
				try {
					
					project = p.compile(newplan);
					
					logger.debug(methodName + " preparation to execute the project");
					
					p.execute(project);
					
				} catch (GeneralException e) {

					e.printStackTrace();
				}*/
			
			} else if ("Delete".equalsIgnoreCase(operation)) {
				logger.debug(className + methodName + " The Request operation is " + operation);
				
				// delete account from tempPassword Map
				
				accountId = req.getNativeIdentity();
				
				try {
					IdentityUtil.updateTempPasswordIdentityForDeletePassword(context, identity, accountId);
				} catch (Exception e) {
					logger.debug("failed delete cause di Attribute tempPassword is null");
				}

				try {
					logger.debug(className + methodName + " Inside ... Penghapusan Entitlements ");
					IdentityUtil.deleteAssignments(context, identity, accountId);
				} catch (Exception e) {
					logger.debug("failed delete cause di Attribute Entitlements is null");
				}
				
			//Revoke keesokkan harinya di approve dan  Reset Password	
			} else if("Modify".equalsIgnoreCase(operation)){
				logger.debug(className + methodName + " The Request operation is " + operation);
				
				//Reset Password
				accountId = req.getNativeIdentity();
				logger.debug(methodName + " Reset password for user id Mainframe " + accountId);
				
				AttributeRequest attr = null;
				attr = req.getAttributeRequest(RACFAttribute.PASSWORD);
				logger.debug("attr: "+attr);
				
				if (attr != null) {
					logger.debug(methodName + " attribute request is not null");
					
					String roleName = "";
					roleName = req.getSourceRole();
					logger.debug("Role Name:" +roleName);
					
					if (!tempRole.isEmpty()) {
						roleName = tempRole;
						logger.debug(className + methodName + "Inside tempRole");
					} else {
						roleName = IdentityUtil.getAssignedRoleFromAccountId(identity, accountId);
						logger.debug(className + methodName + "Inside search Role");
					}
					
					// Default password
					password = attr.getValue().toString();
					//logger.debug(methodName + " default password is " + password);

					List lstPassword = req.getAttributeRequests(RACFAttribute.PASSWORD);
					logger.debug(methodName + " list password size is " + lstPassword.size());

					Iterator itPassword = lstPassword.iterator();
					logger.debug(methodName + " get the iterator");

					while (itPassword.hasNext()) {
						AttributeRequest attrReq = (AttributeRequest) itPassword.next();
						if ("Add".equalsIgnoreCase(attrReq.getOp().toString())) {
							password = attrReq.getValue().toString();
						}
					}

					//logger.debug(methodName + "password " + password + " get value " + attr.getValue().toString());

					if (password != null && !"".equalsIgnoreCase(password)) {
						
						try {
							application = MainframeUtil.getBcaMainframeApplication(context, roleName);
							logger.debug(methodName + "Masuk");
						} catch (GeneralException e1) {
							e1.printStackTrace();
						}
						
						try {
							IdentityUtil.updateTempPasswordIdentityForResetPassword(context, identity, application,	accountId, tanggal, password);
							
						} catch (Exception e) {
							logger.debug("failed to update temp password");
							e.printStackTrace();
						}
					}
				}
				
				//Revoke
				else{
					logger.debug(methodName + "Inside Specially Revoke");
					
					try {
						logger.debug("Inside");
						revokeDate = (String) req.getAttributeRequest(RACFAttribute.REVOKE_DATE).getValue();
						resumeDate = (String) req.getAttributeRequest(RACFAttribute.RESUME_DATE).getValue();
						logger.debug("Done");
					} catch (Exception e) {
						logger.debug("failed to update temp password");
						e.printStackTrace();
					}
					logger.debug(methodName + "Revoke Date: " +revokeDate);
					logger.debug(methodName + "Resume Date: " +resumeDate);
					
					SimpleDateFormat date = new SimpleDateFormat("YYYYMMdd");
					tanggal = date.format(today);
					
					int rvkDate = 99999999;
					int rsmDate = 99999999;
					int sysDate = Integer.parseInt(tanggal);
					
					if(revokeDate!=null && revokeDate.length()>1){
						rvkDate = Integer.parseInt(revokeDate);
						rsmDate = Integer.parseInt(resumeDate);
						
						logger.debug("Done");
					}
					
					if(rvkDate <= sysDate && sysDate < rsmDate){
						logger.debug(className + methodName + "Inside Force Revoke");
						
						ProvisioningPlan newplan = new ProvisioningPlan();
						AccountRequest accReq = new AccountRequest();
						//Force to Revoke and set Resume Date
						accReq.setOperation(AccountRequest.Operation.Disable);
						accReq.addArgument("RESUME_DATE", resumeDate);
						accReq.setApplication(req.getApplicationName());
						accReq.setNativeIdentity(req.getNativeIdentity());
						
						logger.debug(className + methodName + "Revoke Date to XML : " + accReq.toXml());
						logger.debug(className + "a: " +req.getApplication());
						logger.debug(className + "b: " +req.getApplicationName());
						logger.debug(className + "c: " +req.getNativeIdentity());
						
						List attrReqList = new ArrayList();
						if (attrReqList != null && attrReqList.size() > 0) {
							accReq.setAttributeRequests(attrReqList);
							logger.debug(className + methodName + "Revoke Date to XML : " + accReq.toXml());
						}

						if (accReq != null) {
							newplan.add(accReq);
						}
						newplan.setIdentity(identity);

						Provisioner p = new Provisioner(context);
						logger.debug(methodName + " preparation to compile the plan");

						ProvisioningProject project;
						try {
							project = p.compile(newplan);
							logger.debug(methodName + " preparation to execute the project");
							p.execute(project);

						} catch (GeneralException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			else{
				logger.debug(className + methodName + " The Request operation is " + operation);
				logger.debug(className + methodName + " Application name is : " + req.getApplication());
			}
		}
	}
}
