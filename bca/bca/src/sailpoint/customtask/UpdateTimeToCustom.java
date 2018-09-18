package sailpoint.customtask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

public class UpdateTimeToCustom extends AbstractTaskExecutor{
	public static String CLASS_NAME = "::UpdateTimeToCustom::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.UpdateTimeToCustom");
	@Override
	public void execute(SailPointContext context, TaskSchedule arg1, TaskResult arg2, Attributes<String, Object> arg3)
			throws Exception {
		String METHOD_NAME = "::execute::";
		String customObjectName = "BASE 24 Execution Time";
		String dt = "dateFormat";
		String dtMilis = "timeInMilis";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		String timeDateValue = dateFormat.format(cal.getTime());
		String timeMillisValue = String.valueOf(cal.getTimeInMillis());
	
		Custom custom = CommonUtil.getCustomObject(context, customObjectName);
		Attributes attr = custom.getAttributes();
		Map map = new HashMap();
		map = attr.getMap();
		logger.debug(CLASS_NAME + METHOD_NAME + "date : " + timeDateValue);
		logger.debug(CLASS_NAME + METHOD_NAME + "date milis : " + timeMillisValue);
		if(map != null){
			map.clear();
			attr.putAll(map);
			custom.setAttributes(attr);
			context.startTransaction();
			context.saveObject(custom);
			context.commitTransaction();
			context.decache();
		}
		
		map.put(dt, timeDateValue);
		map.put(dtMilis, timeMillisValue);
		
		attr.putAll(map);
		custom.setAttributes(attr);
		context.startTransaction();
		context.saveObject(custom);
		context.commitTransaction();
		context.decache();
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		// TODO Auto-generated method stub
		return false;
	}

}
