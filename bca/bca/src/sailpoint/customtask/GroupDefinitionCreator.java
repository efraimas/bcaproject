package sailpoint.customtask;

import sailpoint.object.GroupDefinition;

public class GroupDefinitionCreator {
	
	public static void createGroupDefinition(String groupName, String desc){
		
		GroupDefinition groupDef = new GroupDefinition();
		
		groupDef.setName(groupName);
		
		groupDef.setDescription(desc);
		
		groupDef.setPrivate(true);
		
		groupDef.setIndexed(true);
		
		//groupDef.setF

	}

}
