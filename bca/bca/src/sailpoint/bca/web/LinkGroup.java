package sailpoint.bca.web;

public class LinkGroup {
	
	String groupIdentity;
	
	String groupId;
	
	String groupName;
	
	boolean checked;
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupIdentity() {
		return groupIdentity;
	}

	public void setGroupIdentity(String groupIdentity) {
		this.groupIdentity = groupIdentity;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public LinkGroup(String groupIdentity, String groupId, String groupName) {
		super();
		this.groupIdentity = groupIdentity;
		this.groupId = groupId;
		this.groupName = groupName;
	}

	public LinkGroup() {
		super();
	}
	
}
