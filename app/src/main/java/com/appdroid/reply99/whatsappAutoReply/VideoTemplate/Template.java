package com.appdroid.reply99.whatsappAutoReply.VideoTemplate;

import java.util.List;

public class Template{
	private List<ComponentsItem> components;
	private String name;
	private Language language;

	public void setComponents(List<ComponentsItem> components){
		this.components = components;
	}

	public List<ComponentsItem> getComponents(){
		return components;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setLanguage(Language language){
		this.language = language;
	}

	public Language getLanguage(){
		return language;
	}
}