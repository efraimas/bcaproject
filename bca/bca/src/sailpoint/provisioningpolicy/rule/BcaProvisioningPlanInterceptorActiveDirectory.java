package sailpoint.provisioningpolicy.rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.Provisioner;
import sailpoint.api.SailPointContext;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.BcaCalendar;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.IdentityRequest;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.QueryOptions;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
public class BcaProvisioningPlanInterceptorActiveDirectory {

	public static String className = "::BcaProvisioningPlanInterceptorActiveDirectory::";
	public static Logger logger = Logger
			.getLogger("sailpoint.provisioningpolicy.rule.BcaProvisioningPlanInterceptorActiveDirectory");

	public static void planInterceptor(SailPointContext context, ProvisioningPlan plan)
			throws GeneralException, IOException {
		String methodName = "::planInterceptor::";
		logger.debug(className + methodName + " Enter the method" + "plan itercep " + plan.toXml());

		String tanggal;
		String accountId = "";
		String employeeId = "";
		String employeeName = "";
		String password = "";
		String email = "";
		String application = "";
		String mainBranchCode = "";
		String operation = "";
		String status = "";
		String role = "";

		Date today = new Date();

		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-YYYY");
		tanggal = df.format(today);

		employeeId = plan.getNativeIdentity();
		logger.debug(className + methodName + " get the identity" + employeeId);
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

		logger.debug(className + methodName + " get the employee name" + identity.toXml());

		employeeName = (String) identity.getAttribute(IdentityAttribute.SALUTATION_NAME);
		logger.debug(className + methodName + " generate pin mailer for " + employeeId + " at " + tanggal);

		logger.debug(className + methodName + " The Plan : " + plan.toXml());

		List<AccountRequest> lst = plan.getAccountRequests();
		logger.debug("SIZE: " + lst.size());
		Iterator<AccountRequest> it = lst.iterator();

		String filePath = "";

		while (it.hasNext()) {

			AccountRequest req = (AccountRequest) it.next();
			logger.debug(className + methodName + "akun request " + req.toXml());

			operation = req.getOp().toString();
			status = req.getResult().getStatus();
			logger.debug("Didi: " + status);

			role = req.getSourceRole();
			logger.debug(className + methodName + " The Role is : " + role);

			if ("Create".equalsIgnoreCase(operation)) {
				logger.debug(className + methodName + " The Request operation is " + operation);

				if (CommonUtil.AD_APPLICATION.equalsIgnoreCase(req.getApplicationName())) {

					String content = "";
					content += "NIP" + " : " + employeeId + "\r\n" + "Name" + " : " + employeeName.toUpperCase()
							+ "\r\n";

					application = CommonUtil.AD_APPLICATION;
					if ("AD BCA".equalsIgnoreCase(application)) {
						application = "DOMAIN";
					}
					logger.debug(className + methodName + " Application : " + application);

					accountId = (String) ((AttributeRequest) req
							.getAttributeRequest(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME)).getValue();

					String branchCode = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);

					content += "User ID" + " : " + accountId.toUpperCase() + "\r\n" + "Branch Code" + " : " + branchCode
							+ "\r\n";

					if (!CommonUtil.isMainBranchCode(context, branchCode)) {
						mainBranchCode = CommonUtil.getMainBranchCode(context, branchCode);
					} else {
						mainBranchCode = branchCode;
					}

					try {
						password = (String) ((AttributeRequest) req.getAttributeRequest(IdentityAttribute.PASSWORD))
								.getValue();
					} catch (Exception e) {
						logger.debug(className + methodName + "Password Tidak Bisa Di Ambil");
					}

					logger.debug(className + methodName + " Nilai Password : " + password);
					content += "Main Branch Code" + " : " + mainBranchCode + "\r\n" + "Password" + " : " + password
							+ "\r\n";
					try {
						email = identity.getAttribute(IdentityAttribute.EMAIL) == null ? ""
								: (String) identity.getAttribute(IdentityAttribute.EMAIL);
					} catch (Exception e) {
						logger.debug(className + methodName + "Email Tidak Bisa Di Ambil");
					}

					if (email == null || "".equalsIgnoreCase(email)) {
						email = "";
					}

					content += "E-Mail Address" + " : " + email + "\r\n" + "Application" + " : " + application + "\r\n";

					logger.debug(className + methodName + " Application name is : " + application + " with account id "
							+ accountId + " and password " + password);

					BcaCalendar cal = new BcaCalendar();

					String hariIni = cal.getTanggal() + "-" + cal.getBulan() + "-" + cal.getTahun();

					try {
						filePath = CommonUtil.getBcaSystemConfig(context, "pin mailer prefix") + hariIni + ".txt";
					} catch (Exception e) {
						logger.debug("failed to get filepath ");
					}

					logger.debug(className + methodName + " Preparation write " + content + " to " + filePath);

					CommonUtil.writeFile(filePath, content, null);
					logger.debug(className + methodName + " Done write pin mailer file");

				} else {
					logger.debug(className + methodName + " Application name is : " + req.getApplication());
				}

			} else {

				logger.debug(className + methodName + " The Request operation is " + operation);

				if (CommonUtil.AD_APPLICATION.equalsIgnoreCase(req.getApplicationName())) {
					application = CommonUtil.AD_APPLICATION;

					accountId = req.getNativeIdentity();
					logger.debug(className + methodName + " native identity " + accountId);

					if (req.getAttributeRequest(ActiveDirectoryAttribute.PASSWORD) != null) {
						try {
							password = (String) ((AttributeRequest) req
									.getAttributeRequest(ActiveDirectoryAttribute.PASSWORD)).getValue();
						} catch (Exception e) {
							logger.debug("password tidak ditemukan");
						}
						logger.debug(className + methodName + " Application name is : " + application
								+ " with account id " + accountId + " and password " + password);

						if (password != null || password == "") {
							identity.setPassword(context.decrypt(password));
						}

						ProvisioningPlan updatePassPlan = new ProvisioningPlan();
						AccountRequest accReqPass = new AccountRequest();

						accReqPass.setApplication(CommonUtil.IIQ_APPLICATION);
						accReqPass.setNativeIdentity(identity.getName());
						accReqPass.setOperation(AccountRequest.Operation.Modify);

						AttributeRequest attrPass = new AttributeRequest();
						attrPass.setName(IdentityAttribute.PASSWORD);
						if (password != null || password == "") {
							attrPass.setValue(context.decrypt(password));
						}
						attrPass.setOp(ProvisioningPlan.Operation.Set);

						List attrReqList = new ArrayList<AttributeRequest>();

						attrReqList.add(attrPass);
						accReqPass.setAttributeRequests(attrReqList);
						updatePassPlan.add(accReqPass);
						updatePassPlan.setIdentity(identity);

						Provisioner p = new Provisioner(context);
						logger.debug(className + methodName + " preparation to compile the plan");

						ProvisioningProject project = p.compile(updatePassPlan);
						logger.debug(className + methodName + " preparation to execute the project");

						p.execute(project);

						// Force user to change password
						ProvisioningPlan newplan = new ProvisioningPlan();
						AccountRequest accReq = new AccountRequest();

						accReq.setOperation(AccountRequest.Operation.Modify);
						accReq.setApplication(CommonUtil.AD_APPLICATION);
						accReq.setNativeIdentity(accountId);

						AttributeRequest pwdLastSet = new AttributeRequest();
						pwdLastSet.setName(ActiveDirectoryAttribute.PWD_LAST_SET);
						pwdLastSet.setValue(true);
						pwdLastSet.setOp(ProvisioningPlan.Operation.Set);

						List attrReq = new ArrayList();
						attrReq.add(pwdLastSet);
						accReq.setAttributeRequests(attrReq);

						if (attrReq != null && attrReq.size() > 0) {
							accReq.setAttributeRequests(attrReq);
							logger.debug(className + methodName + "pwdLastSet to XML : " + accReq.toXml());
						}

						if (accReq != null) {
							newplan.add(accReq);
						}
						newplan.setIdentity(identity);

						Provisioner pr = new Provisioner(context);
						logger.debug(methodName + " preparation to compile the plan");

						ProvisioningProject prj;
						try {
							prj = pr.compile(newplan);
							logger.debug(methodName + " preparation to execute the project");
							pr.execute(prj);

						} catch (GeneralException e) {
							e.printStackTrace();
						}

					} else if (req.getAttributeRequest(ActiveDirectoryAttribute.MAIL) != null) {
						logger.debug(className + methodName + "inside .. request mail inteceptor");

						Custom custom = CommonUtil.getCustomObject(context, CustomObject.BCA_SYSTEM_CONFIG);
						Attributes attr = custom.getAttributes();
						Map map = new HashMap();
						map = attr.getMap();
						String user = (String) map.get("UserSP2AD");
						String pass = context.decrypt((String) map.get("PassSP2AD"));
						String mailAddress = null;
						try {
							mailAddress = (String) ((AttributeRequest) req.getAttributeRequest(ActiveDirectoryAttribute.MAIL)).getValue();
							logger.debug(className + methodName + "Mail address : " + mailAddress);
						} catch (Exception e) {
							logger.debug("Mail Address not found");
						}
						String mailNickName = null;
						String smtp = "SMTP:";
						String emailp = (String) BCAEmailAlias.getValidEmailAddressIntra(context,
										(String) identity.getAttribute(IdentityAttribute.FIRST_NAME),
										(String) identity.getAttribute(IdentityAttribute.MIDDLE_NAME),
										(String) identity.getAttribute(IdentityAttribute.LAST_NAME));

						mailNickName = smtp + emailp.toLowerCase() + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL;

						String typeMailRequest = getEmailRequestType(context, plan);
				
						logger.debug(className + methodName + "Request Type :" + typeMailRequest);
						String tempnickname = emailp.toLowerCase();
						String tempnicknamedomain = mailNickName.substring(mailNickName.indexOf("smtp:") + 6, mailNickName.length());
						String scriptPs = "";
						
						String akunAD = "";
						List links = null;
						try{
							links = identity.getLinks();
						}catch (Exception e) {
							e.printStackTrace();
						}
						Iterator itLinks = links.iterator();
						while(itLinks.hasNext()){
							Link link = (Link)itLinks.next();
							try{
								akunAD = (String) link.getAttribute("sAMAccountName");
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
						logger.debug(className + methodName + "tempnickname :" + tempnickname + "tempnicknamedomain" + tempnicknamedomain
								     + "account ad : " + akunAD);
						if (typeMailRequest.equalsIgnoreCase("Exchange IT Internal Email")) {
							logger.debug(className + methodName + "Run Powershell Email Internal");
							scriptPs = "$username='" + user + "'; " + "$password='" + "idg89!" + "'; "
									+ "$securePassword = ConvertTo-SecureString $password -AsPlainText -Force; "
									+ "$credential = New-Object System.Management.Automation.PSCredential $username, $securePassword; "
									+ "$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://DEVDOMMAILCAS02.dti.co.id/PowerShell/ -Authentication Kerberos -Credential $credential; "
									+ "Import-PSSession $Session; " 
									+ "enable-mailbox " + akunAD + " -database \"DB Internal 2\" -primarysmtpaddress " + tempnicknamedomain + "; "
									//+ "set-mailbox " + akunAD + " -emailaddress @{add=\"" + akunAD + "@dti.co.id\"}; "
									+ "Write-Host \"ok berhasil\"; " + "Exit-PSSession; "
									+ "Remove-PSSession $Session";
						} else if (typeMailRequest.equalsIgnoreCase("Exchange IT Eksternal Email")) {
							logger.debug(className + methodName + "Run Powershell Email Eksternal");
						}

						String command = "powershell.exe -Command \"&{" + scriptPs + "}\"";
						logger.debug(className + methodName + "script powershell :" + scriptPs);

						Process powerShellProcess = Runtime.getRuntime().exec(command);
						String line;
						BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
						logger.info(className + methodName + " output Stream : " + stdout);
						while ((line = stdout.readLine()) != null) {
							logger.info(className + methodName + "out stream " + line);
						}
						powerShellProcess.getOutputStream().close();
						stdout.close();

						ProvisioningPlan updateMailPlan = new ProvisioningPlan();
						AccountRequest accReqMail = new AccountRequest();

						accReqMail.setApplication(CommonUtil.AD_APPLICATION);
						accReqMail.setNativeIdentity(identity.getName());
						accReqMail.setOperation(AccountRequest.Operation.Modify);

						AttributeRequest attrMail = new AttributeRequest();
						attrMail.setName(IdentityAttribute.EMAIL);
						if (mailAddress != null || mailAddress == "") {
							attrMail.setValue(context.decrypt(mailAddress));
						}
						attrMail.setOp(ProvisioningPlan.Operation.Set);

						List attrReqList = new ArrayList<AttributeRequest>();

						attrReqList.add(attrMail);
						accReqMail.setAttributeRequests(attrReqList);
						updateMailPlan.add(accReqMail);
						updateMailPlan.setIdentity(identity);

						Provisioner p = new Provisioner(context);
						logger.debug(className + methodName + " preparation to compile the plan");

						ProvisioningProject project = p.compile(updateMailPlan);
						logger.debug(className + methodName + " preparation to execute the project");

						p.execute(project);

					} else if (req.getAttributeRequest(ActiveDirectoryAttribute.ACCOUNT_EXPIRES) != null) {
						try {
							if (req.getArgument("flow").toString().equalsIgnoreCase("ExtendAccount")) {
								DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
								SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

								Date date = inputFormat.parse(req.getArgument("EXTEND_DATE").toString());

								String value = sdf.format(date);
								logger.debug("value to update " + value);
								IdentityUtil.updateIdentityAttribute(context, identity,
										ActiveDirectoryAttribute.END_DATE, value);
							}
						} catch (Exception e) {
							logger.debug("failed to update identity attribute");
						}
					}

				} else {
					logger.debug(className + methodName + " Application name is : " + req.getApplication());
				}
			}
		}
		logger.debug(methodName + "Temp DB " + filePath + " has been updated");
	}

	/**
	 * @author efraimadyandra
	 * 
	 *         Cari type request email (internal/eksternal) melalui IdentityRequest
	 * 
	 * @param sailPointContext
	 * @param provisioningPlan
	 * @return typeMailRequest
	 * @throws GeneralException
	 */
	private static String getEmailRequestType(SailPointContext sailPointContext, ProvisioningPlan provisioningPlan)
			throws GeneralException {
		String typeMailRequest = "";
		Map mapPlan = provisioningPlan.toMap();
		Map mapPlanArgs = (Map) mapPlan.get("args");
		String identityRequestId = (String) mapPlanArgs.get("identityRequestId");
		IdentityRequest myIdentityRequest = sailPointContext.getObject(IdentityRequest.class, identityRequestId);
		List<IdentityRequestItem> questions = myIdentityRequest.getItems();
		for (IdentityRequestItem item : questions) {
			if (item.getApplication().equals("IIQ")) {
				if (item.getProvisioningPlan() != null) {
					List mapList = (List) item.getProvisioningPlan().getIIQAccountRequest().toMap().get("attributes");
					for (Object obj : mapList) {
						Map mp = (Map) obj;
						typeMailRequest = (String) mp.get("value");
					}
				}
			}
		}
		return typeMailRequest;
	}
}
