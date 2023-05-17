package com.appdroid.reply99.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "Conversion")
public class ConversionHolder implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int conversionId;

    @ColumnInfo(name = "massage")
    private String massage;

    @ColumnInfo(name = "whatsAppMassage")
    private String whatsAppMassage;


    @ColumnInfo(name = "callerName")
    private String callerName;

    @ColumnInfo(name = "phoneNumber")
    private String phoneNumber;

    @ColumnInfo(name = "imageForWhatsApp")
    private String imageForWhatsApp;

    @ColumnInfo(name = "callingFlag")
    private String callingFlag;

    @ColumnInfo(name = "deliveryFlag")
    private boolean deliveryFlag;

    @ColumnInfo(name = "whatsAppDeliveryFlag")
    private boolean whatsAppDeliveryFlag;


    @ColumnInfo(name = "imageOrVideoFlag")
    private String imageOrVideoFlag;

    @ColumnInfo(name = "date")
    private long date;

    public ConversionHolder() {
    }

    public ConversionHolder(int conversionId, String massage, String whatsAppMassage, String callerName, String phoneNumber, String imageForWhatsApp, String callingFlag, boolean deliveryFlag, boolean whatsAppDeliveryFlag, String imageOrVideoFlag, long date) {
        this.conversionId = conversionId;
        this.massage = massage;
        this.whatsAppMassage = whatsAppMassage;
        this.callerName = callerName;
        this.phoneNumber = phoneNumber;
        this.imageForWhatsApp = imageForWhatsApp;
        this.callingFlag = callingFlag;
        this.deliveryFlag = deliveryFlag;
        this.whatsAppDeliveryFlag = whatsAppDeliveryFlag;
        this.imageOrVideoFlag = imageOrVideoFlag;
        this.date = date;
    }

    public int getConversionId() {
        return conversionId;
    }

    public void setConversionId(int conversionId) {
        this.conversionId = conversionId;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getWhatsAppMassage() {
        return whatsAppMassage;
    }

    public void setWhatsAppMassage(String whatsAppMassage) {
        this.whatsAppMassage = whatsAppMassage;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageForWhatsApp() {
        return imageForWhatsApp;
    }

    public void setImageForWhatsApp(String imageForWhatsApp) {
        this.imageForWhatsApp = imageForWhatsApp;
    }

    public String getCallingFlag() {
        return callingFlag;
    }

    public void setCallingFlag(String callingFlag) {
        this.callingFlag = callingFlag;
    }

    public boolean isDeliveryFlag() {
        return deliveryFlag;
    }

    public void setDeliveryFlag(boolean deliveryFlag) {
        this.deliveryFlag = deliveryFlag;
    }

    public boolean isWhatsAppDeliveryFlag() {
        return whatsAppDeliveryFlag;
    }

    public void setWhatsAppDeliveryFlag(boolean whatsAppDeliveryFlag) {
        this.whatsAppDeliveryFlag = whatsAppDeliveryFlag;
    }

    public String getImageOrVideoFlag() {
        return imageOrVideoFlag;
    }

    public void setImageOrVideoFlag(String imageOrVideoFlag) {
        this.imageOrVideoFlag = imageOrVideoFlag;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
