package com.pete.apps.loan;

public class NewLoan {

    public String id;
    public String phoneNumber;
    public String amount;
    public String loanStatus;
    public String loanDate;

    public  NewLoan(){

    }

    public NewLoan(String id,String phoneNumber, String amount, String loanStatus, String loanDate){


        this.id = id;
        this.phoneNumber =phoneNumber;
        this.amount = amount;
        this.loanStatus = loanStatus;
        this.loanDate =loanDate;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAmount() {
        return amount;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
