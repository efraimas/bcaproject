package sailpoint.bca.web;

public class LinkRole {
	
	String roleName;
	
	String assignmentId;
	
	boolean checked;

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the assignmentId
	 */
	public String getAssignmentId() {
		return assignmentId;
	}

	/**
	 * @param assignmentId the assignmentId to set
	 */
	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public LinkRole(String roleName, String assignmentId) {
		super();
		this.roleName = roleName;
		this.assignmentId = assignmentId;
	}

	public LinkRole() {
		super();
	}
	
}
