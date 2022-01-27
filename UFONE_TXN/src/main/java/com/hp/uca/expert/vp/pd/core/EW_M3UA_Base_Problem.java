package com.hp.uca.expert.vp.pd.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.uca.common.trace.LogHelper;
import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.group.Group;
import com.hp.uca.expert.vp.common.interfaces.CommonActions;
import com.hp.uca.expert.vp.pd.Const;
import com.hp.uca.expert.vp.pd.services.PD_Service_Action;
import com.hp.uca.expert.vp.pd.util.CalculateUtil;
import com.hp.uca.expert.vp.util.string.parameters.StringParameterUtil;
import com.hp.uca.mediation.action.client.Action;

public class EW_M3UA_Base_Problem extends PTCL_Base_Problem {

	private static final Logger LOG = LoggerFactory
			.getLogger(EW_M3UA_Base_Problem.class);
/*
	@Override
	public String calculateProblemAlarmAdditionalText(Group group) throws Exception {
		// TODO Auto-generated method stub
		if (LOG.isTraceEnabled()) {
			LogHelper.enter(LOG, "calculateProblemAlarmAdditionalText()" + " Group : "
					+ group.getName());
		}
		
		String rt = "";
		int alarmSize = CalculateUtil.countAlarmsSizeWithoutTrigger(group,"Customized.CountSubAlarms");
		Alarm trigger = (Alarm)group.getTriggerEvent();
		group.getVar().put("size", alarmSize);
		
		if(alarmSize > 0) {
			String template = CalculateUtil.getPassingFilterParamValue(trigger, 
					"Customized.AT.withNumOfLinks", group.getProblemContext().getName());
			
			if(!StringUtils.isEmpty(template)) {
				Map<String, Object> attachParameters = new HashMap<String, Object>();
				attachParameters.put("size", alarmSize+1);
				
				rt = StringParameterUtil.calculateStringValue(template, 
						trigger, group, this, attachParameters);
			}
			
		} else {
			String template = CalculateUtil.getPassingFilterParamValue(trigger, 
					"Customized.AT.withoutNumOfLinks", group.getProblemContext().getName());
			
			if(!StringUtils.isEmpty(template)) {
				rt = StringParameterUtil.calculateStringValue(template, 
						trigger, group, this, null);
			}
		}
		
		LOG.debug(String
				.format("calculateProblemAlarmAdditionalText returns [%s] for Group [%s(%s)], trigger alarm [%s],  ProblemAlarmAdditionalText [%s]",
						String.valueOf(rt),
						group.getProblemContext().getName(),
						group.getProblemEntity(),
						trigger.getIdentifier()	,
						rt
						));
		
		if (LOG.isTraceEnabled()) {
			LogHelper.exit(LOG, "calculateProblemAlarmAdditionalText()",
					"result : " + rt);
		}
		
		if(rt.equalsIgnoreCase("Problem Default additional text...")) {
			rt = trigger.getAdditionalText();
		}
		String AT=rt;
		int index1=AT.indexOf("@Priority_SM=");
		int index2=AT.indexOf("@NW_CLASS");
		int priority=40;
		String Add_Text=AT.substring(1, index1+13)+priority+AT.substring(index2);
		return Add_Text;
	}
*/
	@Override
	public void calculateProblemAlarmOtherAttribute(Group group, Action action) throws Exception {
		// TODO Auto-generated method stub
		String an = "";
		Alarm trigger = (Alarm) group.getTriggerEvent();
		
		super.calculateProblemAlarmOtherAttribute(group, action);
		int alarm_Size = CalculateUtil.countAlarmsSizeWithoutTrigger(group,"Customized.CountSubAlarms");
		group.getVar().put("size", alarm_Size);	

		try {
			int alarmSize = (int) group.getVar().get("size");
			
			if(alarmSize > 0) {
				String template = CalculateUtil.getPassingFilterParamValue(trigger, 
						"Customized.alarmName.withNumOfLinks", group.getProblemContext().getName());
					
				if(!StringUtils.isEmpty(template)) {
					Map<String, Object> attachParameters = new HashMap<String, Object>();
					attachParameters.put("size", alarmSize+1);
							
					an = StringParameterUtil.calculateStringValue(template, 
							trigger, group, this, attachParameters);
				}
			} else {
				String template = CalculateUtil.getPassingFilterParamValue(trigger, 
						"Customized.alarmName.withoutNumOfLinks", group.getProblemContext().getName());
					
				if(!StringUtils.isEmpty(template)) {
						an = StringParameterUtil.calculateStringValue(template, 
								trigger, group, this, null);
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		if(!StringUtils.isEmpty(an)) {
			action.addCommand(Const.Attr_NATIVE_CAUSE_Write, an);
			group.getVar().put("nativeCause", an);
		}
				
		calculateProblemAlarmCustomizedAttribute(group, action);
	}
	
	
	@Override
	public void whatToDoWhenProblemAlarmIsAttachedToGroup(Group group) throws Exception {
		// TODO Auto-generated method stub
		//send notification
		String externalScript = CalculateUtil.getPassingFilterParamValue(group.getTriggerEvent(), 
				"Customized.ExternalScript", group.getProblemContext().getName());
		String scriptParameter = CalculateUtil.getPassingFilterParamValue(group.getTriggerEvent(), 
				"Customized.ExternalScriptParameter", group.getProblemContext().getName());
			
		String alarmName = group.getVar().getString("nativeCause");		
		if(!StringUtils.isEmpty(externalScript) 
				&& !StringUtils.isEmpty(scriptParameter)
				&& !StringUtils.isEmpty(alarmName)
				&& group.getProblemAlarm()!=null) {
			
				String userText = group.getProblemAlarm().getCustomFieldValue(Const.Attr_User_Text_Read);
			
				Map<String, Object> attachParameters = new HashMap<String, Object>();
				attachParameters.put("nativeCause", alarmName);

				String alarmsList = StringParameterUtil.calculateStringValue(scriptParameter, 
						group.getProblemAlarm(), group, this, attachParameters);
				
				if(alarmsList!=null 
						&& !StringUtils.isEmpty(userText) 
						&& !userText.contains(Const.ALREADY_SEND_SCRIPT_TEXT)) {
						
					executeScript(group, externalScript, alarmsList);				
						
					//Add indication mark, so that after VP restart this script will not be called again					
					if(!StringUtils.isEmpty(userText)) {
						userText += Const.ALREADY_SEND_SCRIPT_TEXT;
						
						List<CommonActions.KV> toRefresh = new ArrayList();
						
						toRefresh.add(new CommonActions.KV(Const.Attr_User_Text_Write, userText)); 
						
						LOG.debug(String
								.format("whatToDoWhenProblemAlarmIsAttachedToGroup for Group [%s(%s)], PA alarm [%s] will update PA's userText,  userText is [%s]",
										group.getProblemContext().getName(),
										group.getProblemEntity(),
										group.getProblemAlarm().getIdentifier()	,
										userText
										));
						PD_Service_Action.updateAlarm(getScenario(), group.getProblemAlarm(), 
								this, toRefresh);
					}
				}
		}
	}
	
	
}
