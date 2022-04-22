package com.pete.apps.loan;

public class UserLogIn {

    String phoneNumber;
    String pinNumber;

    public UserLogIn(String phoneNumber, String pinNumber){
        this.phoneNumber = phoneNumber;
        this.pinNumber = pinNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }
}
