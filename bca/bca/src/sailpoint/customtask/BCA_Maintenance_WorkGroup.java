package sailpoint.customtask;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

public class BCA_Maintenance_WorkGroup extends AbstractTaskExecutor{
	public static String CLASS_NAME = "::BCA_Maintenance_WorkGroup::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.BCA_Maintenance_WorkGroup");

	@Override
	public void execute(SailPointContext context, TaskSchedule taskSchedule, TaskResult taskResult, Attributes<String, Object> attributes)
			throws Exception {
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
	}

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		return false;
	}

}
