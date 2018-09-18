package sailpoint.customtask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.sun.xml.bind.v2.runtime.reflect.ListIterator;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import openconnector.SystemOutLog;
import sailpoint.api.PasswordGenerator;
import sailpoint.api.SailPointContext;
import sailpoint.common.BcaConstantCode;
import sailpoint.common.BranchUtil;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.password.RandomPassword;
import sailpoint.task.AbstractTaskExecutor;

@SuppressWarnings({ "unused", "rawtypes" })
public class GenerateIdentityCadangan extends AbstractTaskExecutor{
	
	private static Logger logger = Logger.getLogger("sailpoint.customtask.IdentityCadanganGenerator");
	static String className = "::IdentityCadanganGenerator::";

	@Override
	public void execute(SailPointContext context, TaskSchedule schedule,
			TaskResult taskResult, Attributes<String, Object> attributes) throws Exception {
		
		String password = "";
		
		String methodName = "::execute::";
		String filePath = "";
		logger.debug(className + methodName + " EXECUTING ... ");
		List branches = BranchUtil.getAllBranchCode(context);
		ArrayList<String> al=new ArrayList<String>();
		String identitySailpoint = IdentityAttribute.NAME;
		logger.debug(className + methodName + "identity sailpoint : " + identitySailpoint);
		Identity managerPusat = CommonUtil.getWorkgroupFromWorkGroupName(context, "Manager Cadangan Kantor Pusat");
		String cadanganKp = "";
		Sheet s = null;
		int rows = 0;
		try{
			filePath = (String)attributes.get("fileInputPath");
			logger.debug(className + methodName + " file path : " + filePath);
			Workbook book = Workbook.getWorkbook(new File(filePath));
			
			s = book.getSheet(0);
			rows = s.getRows();
		}catch (Exception e) {
			logger.debug(className + methodName + " file path Tidak Tersedia");
			
		}
		
		for(int i=1; i<rows; i++){
			cadanganKp = s.getCell(1, i).getContents();
			al.add(cadanganKp);
		}
		
		 al.add("BHB");		al.add("DCS");		al.add("IBS");		al.add("PYC");		al.add("SPC");
		 al.add("BLD");		al.add("DKA");		al.add("ITS1");		al.add("SLPI");		al.add("UBKK");
		 al.add("BLR");		al.add("DPDJ");		al.add("KLA");		al.add("SLPP");		
		 al.add("BPO1");	al.add("DTR");		al.add("PAP");		al.add("SOC");
		 al.add("DAI");		al.add("HCM");		al.add("PSC");		al.add("SOPD");
		 
		 for(int i=0; i<al.size(); i++){
			 	Identity oldkPIdentity = null;
			 	String identityName = "CADANGAN " + al.get(i);
			 	logger.debug(className + methodName + "identity cadangan dengan nama" + identityName);
			 	try{
			 		oldkPIdentity = CommonUtil.searchIdentity(context, identitySailpoint, identityName);
			 	}catch (Exception e) {
					
				}
			 	if(oldkPIdentity == null){
			 		Identity identity = new Identity();
					identity.setDisplayName(identityName);
					identity.setName(identityName);
					identity.setFirstname("CADANGAN");
					identity.setLastname(al.get(i));
					identity.setManager(managerPusat);
					
					password = RandomPassword.password(8);
					password = context.encrypt(password);
					
					identity.setPassword(password);
					identity.setPasswordExpiration(new Date());
					context.saveObject(identity);
					context.commitTransaction();
			 	}else{
			 		logger.debug(className + methodName + "Identity cadangan Kp sudah tersedia");
			 		oldkPIdentity.setManager(managerPusat);
			 	}	
		 }	
		 
		if(branches != null && branches.size() > 0){
			logger.debug(className + methodName + " branch tidak kosong");
			
			int listLength = branches.size();
			
			for(int i=0; i<listLength; i++){
				
				String branchCode  = (String)branches.get(i);
				
				if(branchCode != "0988"){
					logger.debug("Inside");
					String branchType = BranchUtil.getBranchType(context, branchCode);
					logger.debug(className + methodName + "type branccode : " + branchType);
					
					if(!branchType.contains("KP")){
						logger.debug("Inside");
						Iterator searchIdentity = null;
						Identity managerIdentity = null;
						String regionCode = "";
						String regionName = "";
						String branchName = "";
						String divisionCode = "";
						String positionCode = "00000000";
						String positionName = "";
						
						String wapimName = "Manager Cadangan " + branchCode;
						
						String identityName = "CADANGAN " + branchCode;
						logger.debug(className + methodName + "Identity namenya : " + identityName);
						
						Identity wapim = CommonUtil.getWorkgroupFromWorkGroupName(context, wapimName);
						Identity oldIdentity = CommonUtil.searchIdentity(context, identitySailpoint, identityName);
						
						if(wapim == null){
							//Jika wapim tidak ada, maka otomatis manager di set ke SA SKES
							wapim = CommonUtil.getWorkgroupFromWorkGroupName(context, "SKES Security Administrators");
						}
						try{
							if("KCP".equalsIgnoreCase(branchType)){
								logger.debug("Inside");
								searchIdentity = CommonUtil.searchActiveIdentityByBranchByPosition(context, branchCode, BcaConstantCode.KABAG_KCP_TYPE_A);
								managerIdentity = (Identity) searchIdentity.next();
								logger.debug(className + methodName + "Display Name Managernya : " + managerIdentity.getDisplayName());
							
								try{
									regionCode = (String) managerIdentity.getAttribute(IdentityAttribute.REGION_CODE);
									regionName = (String) managerIdentity.getAttribute(IdentityAttribute.REGION_NAME);
									branchName = (String) managerIdentity.getAttribute(IdentityAttribute.BRANCH_NAME);
									divisionCode = (String) managerIdentity.getAttribute(IdentityAttribute.DIVISION_CODE);
									
								}catch (Exception e) {
								
								}
							
								logger.debug(className + methodName + "Region Code manager : " + regionCode);
								logger.debug(className + methodName + "Region Name manager : " + regionName);
								logger.debug(className + methodName + "Region BranchName manager : " + branchName);
								logger.debug(className + methodName + "Region DivisionCode manager : " + divisionCode);
						
							}else if("KCU".equalsIgnoreCase(branchType)){
								logger.debug("Inside");
								searchIdentity = CommonUtil.searchActiveIdentityByBranchByPosition(context, branchCode, BcaConstantCode.KALAY_KPO_KCU);
								managerIdentity = (Identity) searchIdentity.next();
								logger.debug(className + methodName + "Display Name Managernya : " + managerIdentity.getDisplayName());
						
								try{
									regionCode = (String) managerIdentity.getAttribute(IdentityAttribute.REGION_CODE);
									regionName = (String) managerIdentity.getAttribute(IdentityAttribute.REGION_NAME);
									branchName = (String) managerIdentity.getAttribute(IdentityAttribute.BRANCH_NAME);
									divisionCode = (String) managerIdentity.getAttribute(IdentityAttribute.DIVISION_CODE);
									
								}catch (Exception e) {
							
								}
						
								logger.debug(className + methodName + "Region Code manager : " + regionCode);
								logger.debug(className + methodName + "Region Name manager : " + regionName);
								logger.debug(className + methodName + "Region BranchName manager : " + branchName);
								logger.debug(className + methodName + "Region DivisionCode manager : " + divisionCode);
					
							}else if("Region".equalsIgnoreCase(branchType)){
								logger.debug("Inside");
								searchIdentity = CommonUtil.searchActiveIdentityByBranchByPosition(context, branchCode, BcaConstantCode.KAUR_KEU_HR_KANWIL);
								managerIdentity = (Identity) searchIdentity.next();
								logger.debug(className + methodName + "Display Name Managernya : " + managerIdentity.getDisplayName());
						
								try{
									regionCode = (String) managerIdentity.getAttribute(IdentityAttribute.REGION_CODE);
									regionName = (String) managerIdentity.getAttribute(IdentityAttribute.REGION_NAME);
									branchName = (String) managerIdentity.getAttribute(IdentityAttribute.BRANCH_NAME);
									divisionCode = (String) managerIdentity.getAttribute(IdentityAttribute.DIVISION_CODE);
									
								}catch (Exception e) {
							
								}
						
								logger.debug(className + methodName + "Region Code manager : " + regionCode);
								logger.debug(className + methodName + "Region Name manager : " + regionName);
								logger.debug(className + methodName + "Region BranchName manager : " + branchName);
								logger.debug(className + methodName + "Region DivisionCode manager : " + divisionCode);
					
							}else{
								logger.debug(className + methodName + "branch type tidak ditemukan" + branchType);
							}
							
					}catch (Exception e) {
						
					}
					
					if(wapim != null && oldIdentity == null){
						logger.debug(className + methodName + " wapim ditemukan");
						
						Identity identity = new Identity();
						
						identity.setDisplayName(identityName);
						identity.setName(identityName);
						identity.setAttribute(IdentityAttribute.BRANCH_CODE, branchCode);
						identity.setFirstname("CADANGAN");
						identity.setLastname(branchCode);
						identity.setAttribute(IdentityAttribute.SALUTATION_NAME, identityName);
						identity.setManager(wapim);
						
						password = RandomPassword.password(8);
						password = context.encrypt(password);
						
						identity.setPassword(password);
						identity.setPasswordExpiration(new Date());
						try{
							identity.setAttribute(IdentityAttribute.REGION_CODE, regionCode);
							identity.setAttribute(IdentityAttribute.REGION_NAME, regionName);
							identity.setAttribute(IdentityAttribute.BRANCH_NAME, branchName);
							identity.setAttribute(IdentityAttribute.POSITION_CODE, positionCode);
							identity.setAttribute(IdentityAttribute.POSITION_NAME, "CADANGAN");
							
						}catch (Exception e) {
							logger.debug("Gagal insert attribute");
							
						}

						context.saveObject(identity);
						context.commitTransaction();
						
					}
					
					else if(wapim != null && oldIdentity != null){
						
						logger.debug(className + methodName + " wapim ditemukan, dengan identity " + oldIdentity.toXml());
						
						oldIdentity.setAttribute(IdentityAttribute.DIVISION_CODE, divisionCode);
						if(oldIdentity.getAttribute(IdentityAttribute.FIRST_NAME) == null)
						{
							oldIdentity.setAttribute(IdentityAttribute.FIRST_NAME, "CADANGAN");
						}else if(oldIdentity.getAttribute(IdentityAttribute.LAST_NAME) == null)
						{
							oldIdentity.setAttribute(IdentityAttribute.LAST_NAME, branchCode);
						}else if(oldIdentity.getAttribute(IdentityAttribute.SALUTATION_NAME) == null){
							oldIdentity.setAttribute(IdentityAttribute.SALUTATION_NAME, identityName);
						}
						oldIdentity.setManager(wapim);
						
						password = RandomPassword.password(8);
						password = context.encrypt(password);
						
						oldIdentity.setPassword(password);
						oldIdentity.setPasswordExpiration(new Date());
						
						try{
							oldIdentity.setAttribute(IdentityAttribute.REGION_CODE, regionCode);
							oldIdentity.setAttribute(IdentityAttribute.REGION_NAME, regionName);
							oldIdentity.setAttribute(IdentityAttribute.BRANCH_NAME, branchName);
							oldIdentity.setAttribute(IdentityAttribute.POSITION_CODE, positionCode);
							oldIdentity.setAttribute(IdentityAttribute.POSITION_NAME, "CADANGAN");
						}catch (Exception e) {
							logger.debug("Gagal insert attribute old identity");
						}
						
						context.saveObject(oldIdentity);
						context.commitTransaction();					
					}else{
						logger.debug(className + methodName + " wapim dengan branchcode " + branchCode + " tidak ditemukan");
					}
				}
			}
		}
			
		}else{
			logger.debug(className + methodName + " list is empty");
		}
		
	}

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(className + METHOD_NAME + "Inside...");
		return false;
	}
	
	/*public static void main(String[]args) throws Exception, IOException{
		String filepath = "H:\\BernekelBOB\\cadangan kp.xls";
		Workbook book = Workbook.getWorkbook(new File(filepath));
		Sheet s = book.getSheet(0);
		int rows = s.getRows();
		String cadanganKp = "";
		for(int i=1; i<rows; i++){
			cadanganKp = s.getCell(0, i).getContents();
			System.out.println(cadanganKp);
		}
	}*/

}
