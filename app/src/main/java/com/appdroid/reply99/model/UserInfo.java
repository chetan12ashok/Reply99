package com.appdroid.reply99.model;

import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable {
    String contactNumber,userId,userName,lastName,email,dob,docId,deviceId,profilePic;
    boolean activationFlag;
    List<String> keywords;

    public UserInfo() {
    }

    public UserInfo(String contactNumber, String userId, String userName, String lastName, String email, String dob, String docId, String deviceId, String profilePic, boolean activationFlag, List<String> keywords) {
        this.contactNumber = contactNumber;
        this.userId = userId;
        this.userName = userName;
        this.lastName = lastName;
        this.email = email;
        this.dob = dob;
        this.docId = docId;
        this.deviceId = deviceId;
        this.profilePic = profilePic;
        this.activationFlag = activationFlag;
        this.keywords = keywords;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isActivationFlag() {
        return activationFlag;
    }

    public void setActivationFlag(boolean activationFlag) {
        this.activationFlag = activationFlag;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}







