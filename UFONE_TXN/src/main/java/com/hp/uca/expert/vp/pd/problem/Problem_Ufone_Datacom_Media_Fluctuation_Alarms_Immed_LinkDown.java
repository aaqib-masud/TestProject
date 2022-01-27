package com.hp.uca.expert.vp.pd.problem;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.uca.common.trace.LogHelper;
import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.group.Group;
import com.hp.uca.expert.vp.pd.Const;
import com.hp.uca.expert.vp.pd.core.PTCL_Base_Problem;
import com.hp.uca.expert.vp.pd.util.CalculateUtil;
import com.hp.uca.expert.vp.util.string.parameters.StringParameterUtil;
import com.hp.uca.mediation.action.client.Action;

public class Problem_Ufone_Datacom_Media_Fluctuation_Alarms_Immed_LinkDown extends PTCL_Base_Problem {

//	private static final Logger LOG = LoggerFactory.getLogger(Problem_Ufone_Datacom_Media_Fluctuation_Alarms_Immed_LinkDown.class);
	// Declaring Variables to be processed in this CODE
	static String friendly_name="";
	static String region_name="Null";
	static String region_field="";
	static String ne_model="";
	static String ne_list = "";
	static String alarm_name="";
	static String alarm_name_target = "";
	static String additional_text_new ="";
	static String urgency_impact = "40";
	static String alarm_id="";
	static Map<String,Integer> route_total_count_hm = new HashMap<String,Integer>();
    static Map<String,Integer> route_alarm_count_hm = new HashMap<String, Integer>();
    static Map<String,String> route_alc_hm = new HashMap<String, String>();
    static ArrayList<String> frn_detail = new ArrayList<String>();
//    static public String log="NIL";
	
	public static void calculate_frn(Group group, int index, String frn, String add_text_list,String parsing_logic,String alc,String media_type,String route){
		
//		log = "Alarm Id: (" + alarm_id + ") -- Pasrsing_Logic: " + parsing_logic;
//        LOG.debug(String.format(" JAVA_LOG_RULE[%s]",log));	
		
		//Parsing route if null
		if(route.length() == 0) {
			if(add_text_list.toLowerCase().contains("=pos") || add_text_list.toLowerCase().contains("=ip-trunk")) {
				route = "Ufone Managed";
				alc	= "NA";
				media_type = "STM";
			}
			else if(add_text_list.toLowerCase().contains("_tunnel-")) {
				route = "Ufone Managed";
				alc = "NA";
				media_type = "Tunnel";
			}
			else if(add_text_list.toLowerCase().contains("=gig") || add_text_list.toLowerCase().contains("=eth-trunk")) {
				route = "Ufone Managed";
				alc = "NA";
				media_type = "GE";
			}
				
			else {
				route = "NA";
				alc = "NA";
			}
        }
		
		//Parsing for NE Name from Friendly Name
		String nename0 = frn.substring(frn.indexOf(";") + 1);
		String nename = nename0.substring(0, nename0.indexOf(';')+1);
		String default_details = nename0.substring(nename0.indexOf(";") + 1);
		nename = nename.replace(";","");
		
		String patterns[] = CalculateUtil.grab_patterns(parsing_logic); //Patterns are extracted and converted into array from parsing logic defined in add text
		
		if(patterns[0].equals("Parsing Logic Not Defined Properly")) {
			parsing_logic = default_details;
		}
		else {
	        for(int i =0;i<patterns.length;i++){
	            String result_add_text = "NA";
	            String search_string = patterns[i].substring(1,patterns[i].length()-1);
	
	            if (add_text_list.contains(search_string)) {
	                int start_index = add_text_list.indexOf(search_string)+patterns[i].length()-2;
	                String part_value_from_addtext = add_text_list.substring(start_index);

              	  	part_value_from_addtext = part_value_from_addtext.replace(" If",",If");
                    part_value_from_addtext = part_value_from_addtext.replace(";",",");
                    part_value_from_addtext = part_value_from_addtext.replace(" ",",");
     
	                int end_index = part_value_from_addtext.indexOf(",");          
	                if (end_index == -1)    
	                	end_index = 10;
	                
	                result_add_text = part_value_from_addtext.substring(0,end_index);
	                
	                if(result_add_text.indexOf("|")!=-1)
	                	result_add_text = result_add_text.substring(0,result_add_text.indexOf("|"));
	            }
	            parsing_logic = parsing_logic.replace(patterns[i],result_add_text);
	        }
        }
		
		frn_detail.add(media_type + "(" + route + ") : " + alc + " : " + nename + " : " + parsing_logic);
	}
	
	public static void calculate_region(Group group, int index, String rn){
			
			if(index == 0) {
				region_name = rn;
				region_field = rn;
			}
			else {
				if(!region_name.contains(rn)) {
					region_name = region_name + ", " + rn;
					region_field = "National";
				}
			}
		}
	
	public static void calculate_ne_model(Group group, int index, String nem){
		
		if(index == 0) {
			if(nem.length()==0) {
				ne_model = "Null NE_M";
			}
			else {
				ne_model = nem;
			}
		}
	}
	
	public static void calculate_ne_list(Group group, int index, String add_text_list){
		
		if(add_text_list.contains("NE_Name=") && add_text_list.contains(":b")) {
			String ne = add_text_list.substring(add_text_list.indexOf("NE_Name=")+8, add_text_list.indexOf(":b"));
			
			if(index == 0 || ne_list.length() == 0) {
				ne_list = ne;
			}
			else if(ne.length() > 0) {
				if(!ne_list.contains(ne)) {
					ne_list = ne_list + "," + ne;
				}
			}
		}
	}

	public static void calculate_route_info(Group group, String route,int total_media, String alc, String media_type) {
		
		
		if(route.length() == 0) {
            route = "NA";
            total_media = 0;
            alc = "NA";
        }

        //Working for Total media Count hashmap
    	if (!route_total_count_hm.keySet().contains(media_type + "_" + route)) 		 
		    route_total_count_hm.put(media_type.replace(" Trunk", "") + "_" + route, total_media);
		
    	//Working for Alocation hashmap
    	if(!route_alc_hm.keySet().contains(alc)) {
    		route_alc_hm.put(alc, route);
    		
		//Working for Alarm Count hashmap
    	if(media_type.toLowerCase().contains("trunk")) {
    		route_alarm_count_hm.put(media_type.replace(" Trunk", "") + "_" + route, route_total_count_hm.get(media_type + "_" + route));
    	}
    	else {
    		try {
    			//if(route_alarm_count_hm.keySet().contains(media_type + "_" + route)&& route_total_count_hm.keySet().contains(media_type + "_" + route)) {
    				if(route_alarm_count_hm.get(media_type + "_" + route) < route_total_count_hm.get(media_type + "_" + route))
    					route_alarm_count_hm.put(media_type.replace(" Trunk", "") + "_" + route, route_alarm_count_hm.get(media_type + "_" + route) + 1); // if already exists then update count
    			//}
    			//else
    			    //route_alarm_count_hm.put(media_type.replace(" Trunk", "") + "_" + route,1);
				}
				catch(Exception e) {
					route_alarm_count_hm.put(media_type.replace(" Trunk", "") + "_" + route,1);
				}
			}
    	}
	}
	
	@Override
	public void calculateProblemAlarmOtherAttribute(Group group, Action action) throws Exception {
		// TODO Auto-generated method stub

		super.calculateProblemAlarmOtherAttribute(group, action);
		
		friendly_name="";
		region_name="Null";
		region_field="";
		ne_model="";
		ne_list = "";
		alarm_name="";
		alarm_name_target = "";
		additional_text_new ="";
		urgency_impact = "40";
		alarm_id="";
		route_total_count_hm.clear();
		route_alarm_count_hm.clear();
		route_alc_hm.clear();
		frn_detail.clear();
		
		int alarm_list_index=0;
		
		//############### Processing the alarms in collection ###############//
		for(Alarm alarm : group.getAlarmList()) {
			//Fetching alarm attributes of respective alarm in LIST
			String frn=CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Frd_Name", group.getProblemContext().getName());
			String rn=CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Region_Name", group.getProblemContext().getName());
			String nem=CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Ne_Mod", group.getProblemContext().getName());
			String aln=CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Al_Name", group.getProblemContext().getName());
			String alrmt=CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Aln_Target", group.getProblemContext().getName());
			String pl=CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Parsing_Logic", group.getProblemContext().getName());
			String alc=CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Allocation", group.getProblemContext().getName());
			String media_type=CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Media_Type", group.getProblemContext().getName()); 
			String route=CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Route", group.getProblemContext().getName()); 
			String total_media =CalculateUtil.getMapperValueWithFilterParamTag(alarm, "Customized.Total_Media", group.getProblemContext().getName());
			String add_text_list= alarm.getAdditionalText();
			

			int total_media_int;
			try {
				total_media_int = Integer.parseInt(total_media);
			}
			catch(Exception e) {
				total_media_int=0;
			}
			
			//Removing Unwanted tags/data from additional text
			String add_text_list_short = add_text_list.substring(80);
			if (add_text_list_short.contains("@pl=")){
				add_text_list_short = add_text_list_short.substring(0,add_text_list_short.indexOf("@pl=")+1);
			}
			if(add_text_list_short.contains("sm_liaison_end") && add_text_list_short.contains("ALM_ID")) {
				add_text_list_short = add_text_list_short.substring(add_text_list_short.indexOf("sm_liaison_end")+80,add_text_list_short.indexOf("ALM_ID")+1);	
			}
			
			alarm_id = alarm.getIdentifier().toString();
			
			if(alarm_list_index == 0) {
				alarm_name = aln;
				alarm_name_target = alrmt;
			}
			
			calculate_frn(group, alarm_list_index,frn,add_text_list_short,pl,alc,media_type,route); //Processing friendly name
				
			calculate_region(group, alarm_list_index,rn); //Processing Region Name
				
			calculate_ne_model(group, alarm_list_index,nem); //Processing NE Model
			
			calculate_ne_list(group, alarm_list_index,add_text_list); //Processing Ne List
			
			calculate_route_info(group, route, total_media_int, alc, media_type);  //Processing Route Info

			alarm_list_index++;
		}	
		
		//############### Processing Calculated field based on collection ###############//
		
		//Processing region
		region_name = region_name.replace("Central", "C");
		region_name = region_name.replace("South", "S");
		region_name = region_name.replace("North", "N");
		
		//Processing Friendly name
		friendly_name = "NCE_UTXM_01:\n\n";
		
		//Processing Route in Friendly name
		ArrayList<String> route_summary = new ArrayList<String>();
		int i = 0;
		for (String key_value : route_total_count_hm.keySet()) {
			if(!key_value.contains("NA")) {
				
				route_summary.add(key_value + " (" + route_alarm_count_hm.get(key_value) + " / " +route_total_count_hm.get(key_value)  + ")");
				if(i==0)
					friendly_name = friendly_name + "Route wise summary:";
				i++;
			}
		}
		Collections.sort(route_summary);
		
		String prev_group_name = "";
		//String[] SplitedValue = {};
		for (i=0;i<route_summary.size();i++) {
			
			String[] SplitedValue = route_summary.get(i).split("_");
			
			if(!SplitedValue[0].equals(prev_group_name))
				 friendly_name = friendly_name + "\n" + (SplitedValue[0] + "\n\n");
			
			if(SplitedValue.length >= 2)
				friendly_name = friendly_name + (SplitedValue[1]) + "\n";
			
			prev_group_name = SplitedValue[0];
		}
		
		Collections.sort(frn_detail);
		
		friendly_name = friendly_name + "\nDetails:";
		
		for(i=0;i<frn_detail.size();i++) {
			friendly_name = friendly_name + "\n" + frn_detail.get(i) + " |";
		}
		
		//Defining Ne List
		String ne_list_field = ne_list;
		if(ne_list.contains(",")) {
			ne_list = "<bulk>" + ne_list;
		}
		else {
			ne_list = "<single>" + ne_list;
		}
		
		//Defining Alarm Name
		if	(alarm_name_target.length()>0) { 
			alarm_name = alarm_name_target + " [" + region_name + "] | " + ne_model;	
		}
		else {	
			alarm_name = alarm_name + " [" + region_name + "] | " + ne_model; 
		}
		
		//Getting Additional text through trigger event
		Alarm trigger = (Alarm) group.getTriggerEvent(); 
		additional_text_new = trigger.getAdditionalText();
		
		//Update urgency_impact in additional text of trigger alarm
		if(additional_text_new.contains("Priority_SM=") && additional_text_new.contains("@NW_CLASS")) {
			additional_text_new = additional_text_new.substring(0,additional_text_new.indexOf("Priority_SM=")+12) + urgency_impact + additional_text_new.substring(additional_text_new.indexOf("@NW_CLASS"));
		}
		
		//Update region in additional text of trigger alarm
		if(additional_text_new.contains("Region=") && additional_text_new.contains("@Priority_SM")) {
			additional_text_new = additional_text_new.substring(0,additional_text_new.indexOf("Region=")+7) + region_field + additional_text_new.substring(additional_text_new.indexOf("@Priority_SM"));
		}
		
		//Update NE List of additional text
		if(additional_text_new.contains("NE_Name=") && additional_text_new.contains(":b")) {
			additional_text_new = additional_text_new.substring(0,additional_text_new.indexOf("NE_Name=")+8) + ne_list + additional_text_new.substring(additional_text_new.indexOf(":b"));
		}
		
		//############### Executing Commands to set the calculated fields ###############//
		if(!StringUtils.isEmpty(friendly_name)) {
			action.addCommand("Friendly_Name", friendly_name);  //set friendly name
		}
				
		if(!StringUtils.isEmpty(alarm_name)) {
			action.addCommand("Alarm_Name", alarm_name); //set alarm name
		}
		
//		if(!StringUtils.isEmpty(region_field)) {
//			action.addCommand("Region", region_field); // set region field
//		}
//		
//		if(!StringUtils.isEmpty(ne_list_field)) {
//			action.addCommand("Ne_Name", ne_list_field);  //set NE Name
//		}		
//		
//		if(!StringUtils.isEmpty(urgency_impact)) {
//			action.addCommand("Urgency_Impact", urgency_impact); //set urgency impact field
//		}
		
		if(!StringUtils.isEmpty(additional_text_new)) {
			action.addCommand("additionalText", additional_text_new);	//set additional text
		}		
	}
}