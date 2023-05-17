package com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA;

public class ParametersItem{
	private Image image;
	private String type;
	private DateTime date_time;
	private Currency currency;
	private String text;
	private String payload;

	public void setImage(Image image){
		this.image = image;
	}

	public Image getImage(){
		return image;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setDateTime(DateTime dateTime){
		this.date_time = dateTime;
	}

	public DateTime getDateTime(){
		return date_time;
	}

	public void setCurrency(Currency currency){
		this.currency = currency;
	}

	public Currency getCurrency(){
		return currency;
	}

	public void setText(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}

	public void setPayload(String payload){
		this.payload = payload;
	}

	public String getPayload(){
		return payload;
	}
}
