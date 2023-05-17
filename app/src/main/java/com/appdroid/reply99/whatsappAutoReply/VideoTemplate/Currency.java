package com.appdroid.reply99.whatsappAutoReply.VideoTemplate;

public class Currency{
	private String fallback_value;
	private String code;
	private String amount_1000;

	public void setFallbackValue(String fallbackValue){
		this.fallback_value = fallbackValue;
	}

	public String getFallbackValue(){
		return fallback_value;
	}

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setAmount1000(String amount1000){
		this.amount_1000 = amount1000;
	}

	public String getAmount1000(){
		return amount_1000;
	}
}
