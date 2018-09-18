package sailpoint.lcm.event.rule;

import java.util.Map;
import java.util.TreeMap;

import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.object.Custom;
import sailpoint.tools.GeneralException;

@SuppressWarnings("unchecked")
public class BcaRoleFilter {

	String CLASS_NAME = "::BcaRoleFilter::";
	String selectedApps;

	Map<String, String> listApplication;
	Map<String, String> listCabang;
	Map<String, String> listCabangBase24;
	Map<String, String> listCabangBATS;
	Map<String, String> listIbsType;
	Map<String, String> listIbsKpType;
	Map<String, String> listServiceType;
	Map<String, String> listServiceTypeKp;
	Map<String, String> listFunction;
	Map<String, String> listOrmis;
	Map<String, String> listFms;
	Map<String, String> listTs;

	public BcaRoleFilter() {

		this.selectedApps = "";

		try {

			SailPointContext context = SailPointFactory.getCurrentContext();

			Custom custom = CommonUtil.getCustomObject(context, CustomObject.BCA_APPLICATION);

			Map<?, ?> mapCustom = custom.getAttributes().getMap();

			if (listApplication == null)
				listApplication = new TreeMap<String, String>();

			listApplication.putAll((Map<String, String>) mapCustom.get("aplikasi"));

			if (listCabang == null)
				listCabang = new TreeMap<String, String>();

			listCabang.putAll((Map<String, String>) mapCustom.get("ListCabangXML"));

			if (listCabangBase24 == null)
				listCabangBase24 = new TreeMap<String, String>();

			listCabangBase24.putAll((Map<String, String>) mapCustom.get("ListBase24XML"));

			if (listCabangBATS == null)
				listCabangBATS = new TreeMap<String, String>();

			listCabangBATS.putAll((Map<String, String>) mapCustom.get("ListBATSXML"));

			if (listIbsType == null)
				listIbsType = new TreeMap<String, String>();

			listIbsType.putAll((Map<String, String>) mapCustom.get("ListIbsXML"));
			
			if (listIbsKpType == null)
				listIbsKpType = new TreeMap<String, String>();

			listIbsKpType.putAll((Map<String, String>) mapCustom.get("ListIbsKpXML"));

			if (listServiceType == null) {
				listServiceType = new TreeMap<String, String>();
			}

			listServiceType.putAll((Map<String, String>) mapCustom.get("servicetypeids"));
			
			if (listServiceTypeKp == null) {
				listServiceTypeKp = new TreeMap<String, String>();
			}

			listServiceTypeKp.putAll((Map<String, String>) mapCustom.get("servicetypeidskp"));

			if (listFunction == null) {
				listFunction = new TreeMap<String, String>();
			}

			listFunction.putAll((Map<String, String>) mapCustom.get("fungsi"));

			if (listOrmis == null) {
				listOrmis = new TreeMap<String, String>();
			}

			listOrmis.putAll((Map<String, String>) mapCustom.get("ListOrmisXML"));
			
			if (listFms == null) {
				listFms = new TreeMap<String, String>();
			}

			listFms.putAll((Map<String, String>) mapCustom.get("ListFmsXML"));

			if (listTs == null) {
				listTs = new TreeMap<String, String>();
			}

			listTs.putAll((Map<String, String>) mapCustom.get("ListTsXML"));

		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> getListApplication() {
		return listApplication;
	}

	public void setListApplication(Map<String, String> listApplication) {
		this.listApplication = listApplication;
	}

	public String getSelectedApps() {
		return selectedApps;
	}

	public void setSelectedApps(String selectedApps) {
		this.selectedApps = selectedApps;
	}

	public Map<String, String> getListFunction() {
		return listFunction;
	}

	public void setListFunction(Map<String, String> listFunction) {
		this.listFunction = listFunction;
	}

	public Map<String, String> getListServiceType() {
		return listServiceType;
	}

	public void setListServiceType(Map<String, String> listServiceType) {
		this.listServiceType = listServiceType;
	}
	
	public Map<String, String> getListServiceTypeKp() {
		return listServiceTypeKp;
	}

	public void setListServiceTypeKp(Map<String, String> listServiceTypeKp) {
		this.listServiceTypeKp = listServiceTypeKp;
	}

	// Cabang
	public Map<String, String> getListCabang() {
		return listCabang;
	}

	public void setListCabang(Map<String, String> listCabang) {
		this.listCabang = listCabang;
	}

	// Base24
	public Map<String, String> getListCabangBase24() {
		return listCabangBase24;
	}

	public void setListCabangBase24(Map<String, String> listCabangBase24) {
		this.listCabangBase24 = listCabangBase24;
	}

	// BATS
	public Map<String, String> getListCabangBATS() {
		return listCabangBATS;
	}

	public void setListCabangBATS(Map<String, String> listCabangBATS) {
		this.listCabangBATS = listCabangBATS;
	}

	// IBS
	public Map<String, String> getListIbsType() {
		return listIbsType;
	}

	public void setListIbsType(Map<String, String> listIbsType) {
		this.listIbsType = listIbsType;
	}
	
	// IBS KP
	public Map<String, String> getListIbsKpType() {
		return listIbsKpType;
	}

	public void setListIbsKpType(Map<String, String> listIbsKpType) {
		this.listIbsKpType = listIbsKpType;
	}

	// Ormis
	public Map<String, String> getListOrmis() {
		return listOrmis;
	}

	public void setListOrmis(Map<String, String> listOrmis) {
		this.listOrmis = listOrmis;
	}
	
	// FMS
	public Map<String, String> getListFms() {
		return listFms;
	}

	public void setListFms(Map<String, String> listFms) {
		this.listFms = listFms;
	}

	// TS
	public Map<String, String> getListTs() {
		return listTs;
	}

	public void setListTs(Map<String, String> listTs) {
		this.listTs = listTs;
	}

}
