package sailpoint.provisioningpolicy.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ActiveDirectoryBeforeProvisioningRule{
		
		String className = "::ActiveDirectoryBeforeProvisioningRule::";
		
		public static Logger logger = Logger
				.getLogger("sailpoint.provisioningpolicy.rule.ActiveDirectoryBeforeProvisioningRule");
		
		
		
		public static void interceptRequest(SailPointContext context, ProvisioningPlan plan){
			
			try {
				logger.debug("Original plan is " + plan.toXml());
			} catch (GeneralException e) {
				e.printStackTrace();
			}
			
			List reqList = plan.getAccountRequests();
			
			boolean isDisabledExchange = false;
			
			Iterator it = reqList.iterator();
			
			try {
				logger.debug("Before Provisioning Policy::" + plan.toXml());
			} catch (GeneralException e) {
				e.printStackTrace();
			}
			
			while(it.hasNext()){
				AccountRequest accReqIt = (AccountRequest)it.next();
				logger.debug("AccReqOp::" + accReqIt.getOperation());
				logger.debug("AccReqOpString::" + accReqIt.getOperation().toString());
				
				List attrReqListTemp = accReqIt.getAttributeRequests();
				List attrReqListUpd = new ArrayList();
				
				Iterator itAttrReq = attrReqListTemp.iterator();
				
				logger.debug("Before itAttrReq iteration");
				
				while(itAttrReq.hasNext()){
					AttributeRequest attrReq = (AttributeRequest)itAttrReq.next();
					logger.debug("Get object");
					if("Remove".equalsIgnoreCase(attrReq.getOp().toString())){
						isDisabledExchange = true;
					}
					if(isDisabledExchange && ActiveDirectoryAttribute.MAIL_NICK_NAME.equalsIgnoreCase(attrReq.getName())){
						logger.debug("Remove mail nickname");
						AttributeRequest mailNickNameAttr = new AttributeRequest();
						mailNickNameAttr.setName(ActiveDirectoryAttribute.MAIL_NICK_NAME);
						mailNickNameAttr.setValue("");
						mailNickNameAttr.setOp(ProvisioningPlan.Operation.Set);
						attrReqListUpd.add(mailNickNameAttr);
						logger.debug("Add mailNickName attr request");
					}else{
						attrReqListUpd.add(attrReq);
						logger.debug("Add attrReqAttr original");
					}
				}
				
				accReqIt.setAttributeRequests(attrReqListUpd);
				
				if(isDisabledExchange && reqList.size() > 0){
					logger.debug("Remove original object");
					reqList.remove(0);
					
					logger.debug("Add attrReqListUpd");
					
					reqList.add(accReqIt);
					
					logger.debug("Add accReqIt");
					
					plan.setAccountRequests(reqList);
					
					logger.debug("Add reqList");
				}
				
				if(isDisabledExchange){
					logger.debug("The operation is disabled exchange");	
				}else{
					logger.debug("The operation is not disabled exchange");
				}
				
				try {
					logger.debug("New plan is " + plan.toXml());
				} catch (GeneralException e) {
					e.printStackTrace();
				}
			}
		}
	}