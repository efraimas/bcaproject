package sailpoint.customtask;

import java.util.Iterator;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Link;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

@SuppressWarnings({ "rawtypes" })
public class IdentityCustomTask extends AbstractTaskExecutor{
	
	public static String className = "::IdentityCustomTask::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.IdentityCustomTask");

	@Override
	public void execute(SailPointContext ctx,
			TaskSchedule paramTaskSchedule, TaskResult paramTaskResult,
			Attributes<String, Object> paramAttributes) throws Exception {


		logger.debug(className + "Persiapan untuk load identity");
		
		QueryOptions qo = new QueryOptions();

		
		
		Filter linksFilter = Filter.eq("application.name", CommonUtil.HR_APPLICATION);
		Filter.collectionCondition("links", linksFilter);
		
		qo.addFilter(linksFilter);
		
		Iterator it = ctx.search(Link.class, qo);
		
		logger.debug(className + "Identity sudah berhasil di load, persiapan looping");
		
		while(it.hasNext()){
			Link emp = (Link)it.next();
			
			if(emp == null || !CommonUtil.isNotEmptyString(emp.getName()) || emp.isDirty()){
				logger.debug(className + "Identity yang null adalah " + emp.getId() + " dengan name " + emp.getDisplayName());
			}
		}
		
		logger.debug(className + "Looping selesai");
		
		ctx.decache();
		
	}

	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		return false;
	}

}
