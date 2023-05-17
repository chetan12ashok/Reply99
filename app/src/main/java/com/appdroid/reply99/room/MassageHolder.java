package com.appdroid.reply99.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "massage")
public class MassageHolder implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int massageId;

    @ColumnInfo(name = "massage")
    private String massage;

    @ColumnInfo(name = "whatsappImageLink")
    private String whatsappImageLink;

    @ColumnInfo(name = "smsImageLink")
    private String smsImageLink;

    @ColumnInfo(name = "containMedia")
    private String containMedia;

    @ColumnInfo(name = "whatsAppFlag")
    private boolean whatsAppFlag;

    public MassageHolder() {
    }


    public MassageHolder(int massageId, String massage, String whatsappImageLink, String smsImageLink, String containMedia, boolean whatsAppFlag) {
        this.massageId = massageId;
        this.massage = massage;
        this.whatsappImageLink = whatsappImageLink;
        this.smsImageLink = smsImageLink;
        this.containMedia = containMedia;
        this.whatsAppFlag = whatsAppFlag;
    }

    public int getMassageId() {
        return massageId;
    }

    public void setMassageId(int massageId) {
        this.massageId = massageId;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getWhatsappImageLink() {
        return whatsappImageLink;
    }

    public void setWhatsappImageLink(String whatsappImageLink) {
        this.whatsappImageLink = whatsappImageLink;
    }

    public String getSmsImageLink() {
        return smsImageLink;
    }

    public void setSmsImageLink(String smsImageLink) {
        this.smsImageLink = smsImageLink;
    }

    public String getContainMedia() {
        return containMedia;
    }

    public void setContainMedia(String containMedia) {
        this.containMedia = containMedia;
    }

    public boolean isWhatsAppFlag() {
        return whatsAppFlag;
    }

    public void setWhatsAppFlag(boolean whatsAppFlag) {
        this.whatsAppFlag = whatsAppFlag;
    }
}
