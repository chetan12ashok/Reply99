package com.appdroid.reply99.whatsappAutoReply.VideoTemplate;

import java.util.List;

public class ComponentsItem{
	private String sub_type;
	private String index;
	private String type;
	private List<ParametersItem> parameters;

	public void setSubType(String subType){
		this.sub_type = subType;
	}

	public String getSubType(){
		return sub_type;
	}

	public void setIndex(String index){
		this.index = index;
	}

	public String getIndex(){
		return index;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setParameters(List<ParametersItem> parameters){
		this.parameters = parameters;
	}

	public List<ParametersItem> getParameters(){
		return parameters;
	}
}