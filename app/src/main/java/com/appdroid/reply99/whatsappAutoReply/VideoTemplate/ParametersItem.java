package com.appdroid.reply99.whatsappAutoReply.VideoTemplate;

public class ParametersItem{
	private Video video;
	private String type;
	private DateTime date_time;
	private Currency currency;
	private String text;
	private String payload;

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public DateTime getDate_time() {
		return date_time;
	}

	public void setDate_time(DateTime date_time) {
		this.date_time = date_time;
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

	public void setVideo(com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA.Video video) {
	}
}
