package sailpoint.provisioningpolicy.rule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import sailpoint.object.Filter;
import sailpoint.api.Provisioner;
import sailpoint.api.SailPointContext;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.BcaCalendar;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.common.MainframeUtil;
import sailpoint.common.RACFAttribute;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.QueryOptions;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.tools.GeneralException;
@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class BcaProvisioningPlanInterceptor {
	
	public static String className = "::BcaProvisioningPlanInterceptor::";

	public static Logger logger = Logger
			.getLogger("sailpoint.provisioningpolicy.rule.BcaProvisioningPlanInterceptor");
	
	static String lineHeader = "Tanggal#NIP-Name#Aplikasi#Account Id#Password";
	
	public static void updatePinMailerDb(SailPointContext context, ProvisioningPlan plan) throws GeneralException, IOException{

		String methodName = "::updatePinMailerDb::";
		
		logger.debug(className + methodName + " Enter the method");
		
		String tanggal;
		
		String accountId = "";
		
		String employeeId = "";
		
		String employeeName = "";
		
		String password = "";
		
		String email = "";
		
		String application = "";
		
		String mainBranchCode = "";
		
		String operation = "";
		
		String tempRole = "";
		
		Date today = new Date();
		
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-YYYY");	
		
		tanggal = df.format(today);
		
		employeeId = plan.getNativeIdentity();
		
		logger.debug(className + methodName + " get the identity");
		Identity identity = null;
		
		if(employeeId.contains("CADANGAN")){
			logger.debug(className + methodName + "Inside... cadangan");
			
			QueryOptions qo = new QueryOptions();

			Filter localFilter = Filter.eq(IdentityAttribute.NAME, employeeId);

			qo.addFilter(localFilter);
			
			Iterator localIterator = context.search(Identity.class, qo);
			
			identity = (Identity) localIterator.next();

		}else{
			logger.debug(className + methodName + "Inside...bukan cadangan");
			identity = IdentityUtil.searchActiveIdentityById(context, employeeId);
		}
		
		logger.debug(className + methodName + " get the employee name" + identity.toXml());
		
		employeeName = (String)identity.getAttribute(IdentityAttribute.SALUTATION_NAME);
		
		logger.debug(className + methodName + " generate pin mailer for " + employeeId + " at " + tanggal);
		
		logger.debug(className + methodName + " The Plan : " + plan.toXml());
		
		List<AccountRequest> lst = plan.getAccountRequests();
		
		Calendar kal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String tglHariIni = dateFormat.format(kal.getTime());
		
		String comment = plan.getComments();
		String temp = "";
		try{
			temp = comment.substring(comment.indexOf("REVOKE_DATE :") + 14, comment.length());
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		String temp2 = "";
		try{
			temp2 = comment.substring(comment.indexOf("RESUME_DATE :") + 14, comment.length());
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		String revokeDate = "";
		try{
			revokeDate = temp.trim().substring(0, 8);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		String resumeDate = "";
		try{
			resumeDate = temp2.trim().substring(0, 8);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		logger.debug(className + methodName + "ambil tanggal revoke nya : " + revokeDate  
				+ "Tanggal hari ini : " + tglHariIni + " ambil tanggal Resume : " + resumeDate);
		
		int skr = Integer.parseInt(tglHariIni);
		int rvk = 0;
		try{
			rvk = Integer.parseInt(revokeDate);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		int rsm = 0;
		try{
			rsm = Integer.parseInt(resumeDate);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		Iterator it = lst.iterator();
		
		String filePath = "";
		
		while(it.hasNext()){
			
			AccountRequest req = (AccountRequest)it.next();
			logger.debug(className + methodName + "request xml nya : " + req.toXml());
			operation = req.getOp().toString();
			
			if(rvk != 0 && rvk <= skr){
				
				logger.debug(className + methodName + "Inside Force Revoke");
				String account = req.getNativeIdentity();
				
				AccountRequest accReq = new AccountRequest();
				ProvisioningPlan newplan = new ProvisioningPlan();
				
				accReq.setOperation(AccountRequest.Operation.Disable);
				
				accReq.addArgument("APP_NAME", req.getApplication());
				
				accReq.setApplication(req.getApplication());
				
				accReq.setInstance(req.getInstance());
				
				accReq.setNativeIdentity(account);
				
				if(rsm > skr){
					logger.debug("Inside update resume date");
					accReq.addArgument("RESUME_DATE", resumeDate);
				}
												
				List attrReqList = new ArrayList();
				if(attrReqList != null && attrReqList.size() > 0){
					accReq.setAttributeRequests(attrReqList);
				}		
				
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
				}
					
			}
			
			if("Create".equalsIgnoreCase(operation)){
				
				logger.debug(className + methodName + " The Request operation is " + operation);
				
				if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(req.getApplicationName())){ //untuk aplikasi AD
					
					String content = "";
					
					content += "NIP" + " : "+ employeeId + "\r\n" + "Name" + " : " +employeeName.toUpperCase() + "\r\n";
					
					application = CommonUtil.AD_APPLICATION;
					if("AD BCA".equalsIgnoreCase(application)){
						application = "DOMAIN";
					}
					logger.debug(className + methodName + " Application : " + application);
					
					accountId = (String)((AttributeRequest)req.getAttributeRequest(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME)).getValue();
					
					String branchCode = (String)identity.getAttribute(IdentityAttribute.BRANCH_CODE);
					
					content += "User ID" + " : " + accountId.toUpperCase() + "\r\n" + "Branch Code" + " : " + branchCode + "\r\n";
					
					if(!CommonUtil.isMainBranchCode(context, branchCode)){
						mainBranchCode = CommonUtil.getMainBranchCode(context, branchCode);
					}else{
						mainBranchCode = branchCode;
					}
					
					try{
						password = (String)((AttributeRequest)req.getAttributeRequest(IdentityAttribute.PASSWORD)).getValue();
					}catch(Exception e){
						System.out.println(e);
						logger.debug(className + methodName + "Password Tidak Bisa Di Ambil");
					}
					
					logger.debug(className + methodName + " Nilai Password : " + password);
					content += "Main Branch Code" + " : " + mainBranchCode + "\r\n" + "Password" + " : " + password + "\r\n";
					try{
						email = identity.getAttribute(IdentityAttribute.EMAIL) == null ? "" : (String)identity.getAttribute(IdentityAttribute.EMAIL);
					}catch (Exception e) {
						System.out.println(e);
						logger.debug(className + methodName + "Email Tidak Bisa Di Ambil");
					}
					
					if(email == null || "".equalsIgnoreCase(email)){
						email = "";
					}
					
					content += "E-Mail Address" + " : " + email + "\r\n" + "Application" + " : " + application + "\r\n\r\n";
					
					logger.debug(className + methodName + " Application name is : " + application + " with account id " + accountId + " and password " + password);
					
					BcaCalendar cal = new BcaCalendar();
					
					@SuppressWarnings("static-access")
					String hariIni = cal.getTanggal() + "-" + cal.getBulan() + "-" + cal.getTahun();
					
					try{
						filePath = CommonUtil.getBcaSystemConfig(context, "pin mailer prefix") + hariIni + ".txt";
					}catch(Exception e){
						logger.debug("failed to get filepath ");
					}
				
					
					logger.debug(className + methodName + " Preparation write " + content + " to " + filePath);
					
					CommonUtil.writeFile(filePath, content, "");
					
					logger.debug(className + methodName + " Done write pin mailer file");
					
					/*
					 * Test to force change password. Start
					 * */
					
					
				/*	ProvisioningPlan updatePassPlan = new ProvisioningPlan();
					
					AccountRequest accReqPass = new AccountRequest();
					
					accReqPass.setApplication(CommonUtil.AD_APPLICATION);
					accReqPass.setNativeIdentity(identity.getName());
					accReqPass.setOperation(AccountRequest.Operation.Modify);
											
					AttributeRequest attrPass = new AttributeRequest();
					attrPass.setName(IdentityAttribute.PASSWORD);
					attrPass.setValue(context.decrypt(password));
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
					
					p.execute(project);*/
					
					
					/*
					 * Test to force change password. End
					 * */
					
				}else if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(req.getApplicationName())){ //untuk aplikasi di Mainframe
					ArrayList<String> al=new ArrayList<String>();
					al.add(operation);
					for(int i=0; i<al.size(); i++){
						logger.debug("Total Looping update tempPassword : " + i);
						String roleName = req.getSourceRole();
						logger.debug(className + methodName + "ini xml rolenya : " + req.toXml());
						tempRole = req.getSourceRole();
						if(roleName != null){
							try{
								application = MainframeUtil.getBcaMainframeApplication(context, roleName);
							}catch(Exception e){
								logger.debug("failed to application");
							}
							accountId = req.getNativeIdentity();			
						}
						
						try {
							password = (String) req.getAttributeRequest(RACFAttribute.PASSWORD).getValue();
						}catch (Exception e) {
							logger.debug("failed to get attribute password");
						}

						
						
						if(password != null && !"".equalsIgnoreCase(password)){
							try{
								IdentityUtil.updateTempPasswordIdentityForGetPassword(context, identity, application, accountId, tanggal, password);
							}catch(Exception e){
								logger.debug("failed to update temp password");
							}
						}
					}
				}else{
					logger.debug(className + methodName + " Application name is : " + req.getApplication());
				}
					
				
				// filePath = "E:\\project\\BCA\\source\\flat file\\output\\BCA_pinmailer_pembuatan" + df.format(today) + ".txt";
				
			}else{ // start request ad
				
				logger.debug(className + methodName + " The Request operation is " + operation);
				
				if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(req.getApplicationName())){ //untuk aplikasi AD
					
					application = CommonUtil.AD_APPLICATION;
					String mailAddress = null;
					try{
						 mailAddress = (String)((AttributeRequest)req.getAttributeRequest(ActiveDirectoryAttribute.MAIL)).getValue();
						
					}catch(Exception e){
						logger.debug("Mail Address not found");
					}
					String mailNickName = null;
					try{
						mailNickName = (String)((AttributeRequest)req.getAttributeRequest("proxyAddresses")).getValue();
					}catch (Exception e) {
						logger.debug("mail nick Name tidak ditemukan");
					}
					accountId = req.getNativeIdentity();
					String tempnickname = mailNickName.substring(mailNickName.indexOf("smtp:") + 5, mailNickName.indexOf("smtp:") + 12);
					logger.debug(className + methodName + "mail nickname nya : " + tempnickname);
					String pathBat = "C:\\Users\\aspadmin\\Desktop\\testscript\\" + tempnickname + "_" + tglHariIni + ".bat";
					String scriptBat = "Powershell.exe -executionpolicy remotesigned -File  C:\\Users\\aspadmin\\Desktop\\testscript\\" + tempnickname + "_" + tglHariIni + ".ps1" + " \nexit";
					String pathps = "C:\\Users\\aspadmin\\Desktop\\testscript\\" + tempnickname + "_" + tglHariIni + ".ps1";
					String scriptPs = "";
					Custom custom = CommonUtil.getCustomObject(context, CustomObject.BCA_SYSTEM_CONFIG);
					Attributes attr = custom.getAttributes();
					Map map = new HashMap();
					map = attr.getMap();
					String user = (String )map.get("UserSP2AD");
					String pass = context.decrypt((String )map.get("PassSP2AD"));
					logger.debug(className + methodName + " user powershell : " + user + " Pass Powershell : " + pass);
					logger.debug(className + methodName + " native identity " + accountId);
					
					if("Delete".equalsIgnoreCase(operation)) {
						try{
							logger.debug(className + methodName + " Inside ... Penghapusan Entitlements ");
							IdentityUtil.deleteAssignments(context, identity, accountId);
							
						scriptPs =  "$username='" + user + "'\n" +
									"$password='" + pass + "'\n" +
									"$securePassword = ConvertTo-SecureString $password -AsPlainText -Force\n" +
									"$credential = New-Object System.Management.Automation.PSCredential $username, $securePassword\n" +
									"$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://DEVDOMMAILCAS02.dti.co.id/PowerShell/ -Authentication Kerberos -Credential $credential\n" +
									"Import-PSSession $Session\n" +
									"Disable-Mailbox" + mailAddress + "'\n" +
									"Remove-PSSession $Session";
							
							FileWriter bat = new FileWriter(pathBat);
							bat.write(scriptBat);
							bat.close();
							
							FileWriter ps = new FileWriter(pathps);
							ps.write(scriptPs);
							ps.close();
							
							Runtime.getRuntime().exec("cmd /C start " + pathBat);
							
						}catch (Exception e) {
							// TODO: handle exception
						}
					}
					
					if(req.getAttributeRequest(ActiveDirectoryAttribute.PASSWORD) != null){
						
						try{
							password = (String)((AttributeRequest)req.getAttributeRequest(ActiveDirectoryAttribute.PASSWORD)).getValue();
						}catch(Exception e){
							logger.debug("password tidak ditemukan");
						}
						logger.debug(className + methodName + " Application name is : " + application + " with account id " + accountId + " and password " + password);
						
						if(password != null || password == ""){
							identity.setPassword(context.decrypt(password));
						}
						
						ProvisioningPlan updatePassPlan = new ProvisioningPlan();
						
						AccountRequest accReqPass = new AccountRequest();
						
						accReqPass.setApplication(CommonUtil.IIQ_APPLICATION);
						accReqPass.setNativeIdentity(identity.getName());
						accReqPass.setOperation(AccountRequest.Operation.Modify);
												
						AttributeRequest attrPass = new AttributeRequest();
						attrPass.setName(IdentityAttribute.PASSWORD);
						if(password != null || password == ""){
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
						
					}else if(req.getAttributeRequest(ActiveDirectoryAttribute.MAIL) != null){
						logger.debug(className + methodName + " request for email");
						
						String memberOf = "";
						List links = null;
						try{
							links = identity.getLinks();
						}catch (Exception e) {
							// TODO: handle exception
						}
						Iterator itLinks = links.iterator();
						while(itLinks.hasNext()){
							Link link = (Link)itLinks.next();
							try{
								memberOf = (String) link.getAttribute("memberOf");
							}catch (Exception e) {
								// TODO: handle exception
							}
						logger.debug(className + methodName + " Member Of identity : " + memberOf);
						try {
							logger.debug(className + methodName + "plan nya :" + plan.toXml() + "request xml nya :" + req.toXml());
						}catch (Exception e) {
							e.printStackTrace();
							// TODO: handle exception
						}
					
							String typeRequest = "";
							try{
								typeRequest = req.getSourceRole();
								logger.debug(className + methodName + "request mengambil tipe request email");
							}catch (Exception e) {
									e.printStackTrace();
							}
							
							if("".equalsIgnoreCase(typeRequest) || typeRequest == null) {
								String tryCatchType = (String)((AttributeRequest)req.getAttributeRequest("proxyAddresses")).getValue();
								if(tryCatchType.contains("dti.co.id")) {
									typeRequest ="Exchange IT External Email";
								}else {
									typeRequest = "Exchange IT Internal Email";
								}
							}
							
							logger.debug(className + methodName + "Request Type :" + typeRequest);
							logger.debug(className + methodName + " Application name is : " + application + " with account id " + accountId + " and email address " + mailAddress);
							
							String tempnicknamedomain = mailNickName.substring(mailNickName.indexOf("smtp:") + 5, mailNickName.length());
							logger.debug(className + methodName + "TempNickName : " + tempnickname + " TempNickNameDomain : " + tempnicknamedomain);
							
							if(typeRequest.equalsIgnoreCase("Exchange IT Internal Email")){
							logger.debug(className + methodName + "Run Powershell Email Internal");	
							
							// $username = $args[0]
							// $password = $args[1]
							
							scriptPs =  "$username='" + user + "'\n" +
										"$password='" + pass + "'\n" +
										"$securePassword = ConvertTo-SecureString $password -AsPlainText -Force\n" +
										"$credential = New-Object System.Management.Automation.PSCredential $username, $securePassword\n" +
										"$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://DEVDOMMAILCAS02.dti.co.id/PowerShell/ -Authentication Kerberos -Credential $credential\n" +
										"Import-PSSession $Session\n" +
										"set-mailbox " + tempnickname + "@dti -emailaddresses 'SMTP:" + mailAddress + "','smtp:" + tempnicknamedomain + "'\n" +
										"Remove-PSSession $Session";
								
							}else if (typeRequest.equalsIgnoreCase("Exchange IT External Email") && memberOf.contains("CN=Intramail")){
								logger.debug(className + methodName + "Run Powershell Email Eksternal For identity already have InternalMail");
								
							scriptPs =  "$username='" + user + "'\n" +
										"$password='" + pass + "'\n" +
										"$securePassword = ConvertTo-SecureString $password -AsPlainText -Force\n" +
										"$credential = New-Object System.Management.Automation.PSCredential $username, $securePassword\n" +
										"$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://DEVDOMMAILCAS02.dti.co.id/PowerShell/ -Authentication Kerberos -Credential $credential\n" +
										"Import-PSSession $Session\n" +
										"set-mailbox " + tempnickname + "@dti -emailaddresses 'SMTP:" + mailAddress + "','smtp:" + tempnickname + "@intra.bca" + "'\n" +
										"Remove-PSSession $Session";
								
							}else if (typeRequest.equalsIgnoreCase("Exchange IT External Email") && !memberOf.contains("CN=Intramail")) {
								logger.debug(className + methodName + "Run Powershell Email Eksternal For identity not have InternalMail");
								
							scriptPs =  "$username='" + user + "'\n" +
										"$password='" + pass + "'\n" +
										"$securePassword = ConvertTo-SecureString $password -AsPlainText -Force\n" +
										"$credential = New-Object System.Management.Automation.PSCredential $username, $securePassword\n" +
										"$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://DEVDOMMAILCAS02.dti.co.id/PowerShell/ -Authentication Kerberos -Credential $credential\n" +
										"Import-PSSession $Session\n" +
										"set-mailbox " + tempnickname + "@dti -emailaddresses 'SMTP:" + mailAddress + "','smtp:" + tempnicknamedomain + "'\n" +
										"set-mailbox " + tempnickname + "@dti -emailaddresses 'SMTP:" + mailAddress + "','smtp:" + tempnickname + "@intra.bca" + "'\n" +
										"Remove-PSSession $Session";
							}
							
							FileWriter bat = new FileWriter(pathBat);
							bat.write(scriptBat);
							bat.close();
							
							FileWriter ps = new FileWriter(pathps);
							ps.write(scriptPs);
							ps.close();
							
							Runtime.getRuntime().exec("cmd /C start " + pathBat /*+" "+user +" "+pass*/);
							logger.debug("Script powershell has been run");
//							File file = new File(pathBat);
//							file.delete();
//							
//							File file1 = new File(pathps);
//							file.delete();
							
						}
						ProvisioningPlan updateMailPlan = new ProvisioningPlan();
						
						AccountRequest accReqMail = new AccountRequest();
						
						accReqMail.setApplication(CommonUtil.IIQ_APPLICATION);
						accReqMail.setNativeIdentity(identity.getName());
						accReqMail.setOperation(AccountRequest.Operation.Modify);
												
						AttributeRequest attrMail = new AttributeRequest();
						attrMail.setName(IdentityAttribute.EMAIL);
						if(mailAddress != null || mailAddress == ""){
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
					} else if(req.getAttributeRequest(ActiveDirectoryAttribute.ACCOUNT_EXPIRES) != null) {
						try {
							if(req.getArgument("flow").toString().equalsIgnoreCase("ExtendAccount")) {
								DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
								SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
								
								Date date = inputFormat.parse(req.getArgument("EXTEND_DATE").toString());
								
								String value = sdf.format(date);
								logger.debug("value to update " + value);
								IdentityUtil.updateIdentityAttribute(context, identity, IdentityAttribute.END_DATE, value);
							}
						}catch(Exception e) {
							logger.debug("failed to update identity attribute");
						}
					}
						
					
				}else if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(req.getApplicationName())){ //untuk aplikasi di Mainframe
								
					accountId = req.getNativeIdentity();
					
					logger.debug(methodName + " reset password for user id Mainframe" + accountId);
					
					String roleName = "";
					if (!tempRole.isEmpty()){
						 roleName = tempRole;
						 logger.debug(className + methodName + "Inside tempRole");
					} else {
						 roleName = IdentityUtil.getAssignedRoleFromAccountId(identity, accountId);
						 logger.debug(className + methodName + "Inside search Role");
					}
					
												
					try {
						application = MainframeUtil.getBcaMainframeApplication(context, roleName);
					} catch (GeneralException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//remediation for delete account @ 20 / 07 / 2017
					
					//operation delete
					if("Delete".equalsIgnoreCase(operation)){
						//delete account from tempPassword Map
						try{
							
							IdentityUtil.updateTempPasswordIdentityForDeletePassword(context, identity, accountId);
						}catch(Exception e){
							logger.debug("failed delete cause di Attribute tempPassword is null");
						}
						
						
						try{
							logger.debug(className + methodName + " Inside ... Penghapusan Entitlements ");
							IdentityUtil.deleteAssignments(context, identity, accountId);
						}catch (Exception e) {
							// TODO: handle exception
						}
						
						
						/*try{
							
							IdentityUtil.updateIdentityAfterDelete(context, identity, plan, accountId, roleName);
						}catch(Exception e){
							logger.debug("failed to update identity after delete complete");
						}*/
						
					}
					
					if("Disable".equalsIgnoreCase(operation)) {
						//try modified the account request if the processed is trying to modified 
						
						ProvisioningPlan updatePassPlan = new ProvisioningPlan();
						
						AccountRequest accReqPass = new AccountRequest();
						
						accReqPass.setApplication(CommonUtil.AD_APPLICATION);
						accReqPass.setNativeIdentity(identity.getName());
						accReqPass.setOperation(AccountRequest.Operation.Modify);
												
						AttributeRequest attrPass = new AttributeRequest();
						attrPass.setName(IdentityAttribute.PASSWORD);
						attrPass.setValue(context.decrypt(password));
						attrPass.setOp(ProvisioningPlan.Operation.Set); 
						
						
						List attrReqList = new ArrayList<AttributeRequest>();
						
						attrReqList.add(attrPass);
						
						accReqPass.setAttributeRequests(attrReqList);
						
						updatePassPlan.add(accReqPass);
						
						updatePassPlan.setIdentity(identity);
						
						Provisioner p = new Provisioner(context);
					}
					
					logger.debug(methodName + "role Name " + roleName + " and application name : " + application);
					AttributeRequest attr = null;
					try{
						attr = req.getAttributeRequest(RACFAttribute.PASSWORD);
					}catch(Exception e){
						logger.debug("Attribute Password not found");
					}
					
					if(attr != null){
						
						logger.debug(methodName + " attribute request is not null");
						
						password = attr.getValue().toString(); //Default password
						
						logger.debug(methodName + " default password is " + password);
						
						List lstPassword = req.getAttributeRequests(RACFAttribute.PASSWORD);
						
						logger.debug(methodName + " list password size is " + lstPassword.size());
						
						Iterator itPassword = lstPassword.iterator();
						
						logger.debug(methodName + " get the iterator");
						
						while(itPassword.hasNext()){
							AttributeRequest attrReq = (AttributeRequest)itPassword.next();
							if("Add".equalsIgnoreCase(attrReq.getOp().toString())){
								password = attrReq.getValue().toString();
							}
						}
																
						logger.debug(methodName + "password " + password + " get value " + attr.getValue().toString());
						
						if(password != null && !"".equalsIgnoreCase(password)){
							try{
								IdentityUtil.updateTempPasswordIdentityForResetPassword(context, identity, application, accountId, tanggal, password);
							}catch(Exception e){
								logger.debug("failed to update temp password");
								e.printStackTrace();
							}
							
						}
					}	
				} else{
					logger.debug(className + methodName + " Application name is : " + req.getApplication());
				}		
								
			}   // akhir dari kondisi request ad
					
		}
		
		logger.debug(methodName + "Temp DB " + filePath + " has been updated");
		
	}
	
	private static String generateLine(String tanggal, String employee, String application, String accountId, String password){
		return tanggal + "#" + employee + "#" + application + "#" + accountId + "#" + password + "\n";
	}
	public static void main(String []args) throws IOException{
		Calendar kal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String tglHariIni = dateFormat.format(kal.getTime());
		//System.out.println(tglHariIni);
		
		ProcessBuilder p = new ProcessBuilder();
		//System.out.println("Start EXE");
//		p.command("C:\\Users\\aspadmin\\Desktop\\test\\New Text Document.bat");
//		p.start();
		//System.out.println("MULAI MENJALANKAN EXE");
		
		
		String sub = "USER ID : C325068T&#xD;&#xA;REVOKE_DATE : 20171101&#xD;&#xA;RESUME_DATE : 20171121";
		String au = sub.substring(sub.indexOf("REVOKE_DATE :") + 14, sub.length());
	//	System.out.println(au.substring(0, 8));
		
//		FileWriter file = new FileWriter("C:\\Users\\aspadmin\\Desktop\\test\\Test.ps1");
//		file.write("test");
//		file.close();
		
		String testSub = "smtp:u636604@dti.co.id";
		String mainNickname = testSub.substring(testSub.indexOf("smtp:") + 5, testSub.length());
		String testpath = "C:\\Users\\aspadmin\\Desktop\\test\\";
		//System.out.println(testpath + mainNickname);
		
		String scriptPowerShell =  "powershell.exe \n" +
				  "$username='" + "user" + "' \n" +
				  "$password='" + "pass" + "' \n" +
				  "$securePassword = ConvertTo-SecureString $password -AsPlainText -Force \n" +
				  "$credential = New-Object System.Management.Automation.PSCredential $username, $securePassword \n" +
				  "$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://DEVDOMMAILCAS02.dti.co.id/PowerShell/ -Authentication Kerberos -Credential $credential \n" +
				  "Import-PSSession $Session \n" +
				  "set-mailbox " + "uvhueheh" + "@dti " + "-emailaddresses " + "'SMTP:" + "test_sailpoint@dti.co.id" + "','smtp:" + "u123456@dti.co.id" + "' \n" +
				  "Remove-PSSession $Session ";
		String scriptBat = "Powershell.exe -executionpolicy remotesigned -File  C:\\Users\\bcamaster\\Desktop\\ScriptForEmailInternal\\ps1test2.ps1 \nexit";
		System.out.println(scriptPowerShell);
		
		
	}
}
