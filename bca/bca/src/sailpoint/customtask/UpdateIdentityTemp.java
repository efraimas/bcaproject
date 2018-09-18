package sailpoint.customtask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class UpdateIdentityTemp extends AbstractTaskExecutor{
	public static String CLASS_NAME = "::updateIdentityTemp::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.updateIdentityTemp");
	private static String lineHeader = "NIP, NAMA, MANAGER, BRANCHCODE, BRANCHNAME, REGIONCODE, REGIONNAME, TANGGALSELESAI";
	@Override
	public void execute(SailPointContext context, TaskSchedule taskSchedule, TaskResult taskResult, Attributes<String, Object> attributes)
			throws Exception {
		// TODO Auto-generated method stub
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String filePath = (String) attributes.get("fileIdentity");
		logger.debug(CLASS_NAME + METHOD_NAME + "will be processed input file : " + filePath);
		
		String OutfilePath = (String) attributes.get("outfileIdentity");
		logger.debug(CLASS_NAME + METHOD_NAME + "will be processed output file : " + OutfilePath);
		
		if (filePath != null) {
			logger.debug(CLASS_NAME + METHOD_NAME + "file path is not null");
			
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			    String line;
			    int clearFirstLine = 0;
			    String[] temp = null;
			    String employeeId = "";
			    String nama = "";
			    String managerId = "";
			    String branchCode = "";
			    String branchName = "";
			    String regionCode = "";
			    String regionName = "";
			    String tanggalSelesai = "";
			    String lineFile = "";
			    String EmployeeID = "";
	    		String Nama = "";
	    		String ManagerID = "";
	    		String BranchCode = "";
	    		String BranchName = "";
	    		String RegionCode = "";
	    		String RegionName = "";
	    		String TanggalSelesai = "";
			    int timeNow;
			    Identity identity = null;
			    BufferedWriter bw = new BufferedWriter(new FileWriter(OutfilePath));
			    HashMap rootmap = new HashMap();
			    List tempList = new ArrayList();
			    
			    Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, 0);
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
				String getTimeNow = dateFormat.format(cal.getTime()).replace("/", "");
			    
				bw.write(lineHeader);
	    		bw.write("\n");
			    while ((line = br.readLine()) != null) {
			    	logger.debug(CLASS_NAME + METHOD_NAME + "line :" + line);
			    	if(clearFirstLine == 0){
			    		clearFirstLine++;
			    	}else{
			    		line = line.replace("\"", "");
			    		temp = line.split(",");
			    		employeeId = temp[0];
			    		nama = temp[1];
			    		managerId = temp[2];
			    		branchCode = temp[3];
			    		branchName = temp[4];
			    		regionCode = temp[5];
			    		regionName = temp[6];
			    		tanggalSelesai = temp[7];
			    		HashMap map = new HashMap();
			    		map.put("Employee ID", employeeId);
			    		map.put("Nama", nama);
			    		map.put("Manager ID", managerId);
			    		map.put("BranchCode", branchCode);
			    		map.put("BranchName", branchName);
			    		map.put("RegionCode", regionCode);
			    		map.put("RegionName", regionName);
			    		map.put("Tanggal Selesai", tanggalSelesai);
			    		
			    		tempList.add(map);
			    		try{
			    			identity = context.getObjectByName(Identity.class , employeeId.trim());
			    		}catch (Exception e) {
			    			e.printStackTrace();
							// TODO: handle exception
						}
			    	}
			    }
			    rootmap.put("DATA", tempList);
			    logger.debug(CLASS_NAME + METHOD_NAME + "Isi map : " + rootmap);
			    List localList = (List) rootmap.get("DATA");
			    Iterator it = localList.iterator();
			    br.close();
			    File file = new File(filePath);
			    file.delete();
			    
			    if(null != identity){
			    	logger.debug(CLASS_NAME + METHOD_NAME + "Inside update identity ...");
			    	timeNow = Integer.parseInt(getTimeNow);
			    	logger.debug(CLASS_NAME + METHOD_NAME + "Tanggal hari ini dalam integer : " + timeNow + "Tanggal selesai karyawan sementara " + Integer.parseInt(tanggalSelesai.trim()));
				    	while(it.hasNext()){
					    	Map maplist = (Map) it.next();
					    	
					    	 EmployeeID = (String) maplist.get("Employee ID");
				    		 Nama = (String) maplist.get("Nama");
				    		 ManagerID = (String) maplist.get("Manager ID");
				    		 BranchCode = (String) maplist.get("BranchCode");
				    		 BranchName = (String) maplist.get("BranchName");
				    		 RegionCode = (String) maplist.get("RegionCode");
				    		 RegionName = (String) maplist.get("RegionName");
				    		 TanggalSelesai = (String) maplist.get("Tanggal Selesai");
				    		 
					    		 if(Integer.parseInt(TanggalSelesai.trim()) <= timeNow){
							    		logger.debug(CLASS_NAME + METHOD_NAME + "Karyawan sementara tidak diupdate karena tanggal selesai sama atau lebih kecil dari tanggal hari ini ");
							    	}else{
							    		logger.debug(CLASS_NAME + METHOD_NAME + "Update Identity");
							    		
							    		 String val = EmployeeID + ", " + Nama + ", " + ManagerID + ", " +
							    			     BranchCode + ", " + BranchName + ", " + RegionCode + ", " +
							    			     RegionName + ", " + TanggalSelesai;
								    	
							    		 bw.write(val);
							    		 bw.write("\n");
							    		 
							    		identity.setAttribute(IdentityAttribute.MANAGER, ManagerID.trim());
								    	identity.setAttribute(IdentityAttribute.BRANCH_CODE, BranchCode.trim());
								    	identity.setAttribute(IdentityAttribute.BRANCH_NAME, BranchName.trim());
								    	identity.setAttribute(IdentityAttribute.REGION_CODE, RegionCode.trim());
								    	identity.setAttribute(IdentityAttribute.REGION_NAME, RegionName.trim());
								    	
								    	context.saveObject(identity);
								    	context.commitTransaction();
								    	context.decache();
							    	}
					    }
			    }
		    		if (bw != null) {
						try {
							bw.flush();
							bw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		    		}
			}catch (Exception e) {
				e.printStackTrace();
			}
				// TODO: handle exception
		}
	}

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		// TODO Auto-generated method stub
		return false;
	}
	
	/*public static void main(String[]args){
		String filePath = "C:/Users/aspadmin/Desktop/Backup/IdKaryawanSementara.csv";
		
		if(filePath != null){
			
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				 String line = "";
				 String identity = "";
				 String managerId = "";
				 String tanggalSelesai = "";
				 int timeNow = 0;
				 String[] temp = null;
				 HashMap map = new HashMap();
				
				 
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, 0);
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
					String getTimeNow = dateFormat.format(cal.getTime()).replace("/", "");
					
				 int clearFirstLine = 0;
				 String val = "";
				    while ((line = br.readLine()) != null) {
				    	//System.out.println("line" + line);
				    	if(clearFirstLine == 0){
				    		clearFirstLine++;
				    	}else{
				    		line = line.replace("\"", "");
					    	temp = line.split(",");
							
					    	identity = temp[0];
					    	managerId = temp[2];
					    	tanggalSelesai = temp[7];
					    	timeNow = Integer.parseInt(getTimeNow);
					    	
					    	map.put("Identity", identity);
					    	map.put("Manager Id", managerId);
					    	map.put("Tanggal Selesai", tanggalSelesai);
					    	
					    
					    	//System.out.println(tanggalSelesai + "    " + getTimeNow);
					    	//System.out.println(tanggalselesaiInt + "   " + timeNow);
					    	//System.out.println("test" + Integer.parseInt(tanggalSelesai.trim()));
					    	//System.out.println("isi map nya : " + map.toString());
					    	if(Integer.parseInt(tanggalSelesai.trim()) <= timeNow){
					    		System.out.println("Tidak Update Identity");
					    	}else{
					    		System.out.println("isi map : " + map);
					    		//System.out.println("Update Identity");
					    		val = (String) map.get("Manager Id");
					    	}
					    	
				    	}
				    }
				    System.out.println("isi map yang diget : " + val);
			}catch (Exception e) {
				System.out.println("File Path Tidak Tersedia");
			}
		}else{
			System.out.println("File Path Tidak Ditemukan");
		}
	}*/
}
