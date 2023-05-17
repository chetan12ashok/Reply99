package com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA;

public class CustomModel{
	private String recipient_type;
	private Template template;
	private String messaging_product;
	private String to;
	private String type;

	public void setRecipientType(String recipientType){
		this.recipient_type = recipientType;
	}

	public String getRecipientType(){
		return recipient_type;
	}

	public void setTemplate(Template template){
		this.template = template;
	}

	public Template getTemplate(){
		return template;
	}

	public void setMessagingProduct(String messagingProduct){
		this.messaging_product = messagingProduct;
	}

	public String getMessagingProduct(){
		return messaging_product;
	}

	public void setTo(String to){
		this.to = to;
	}

	public String getTo(){
		return to;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}
}
