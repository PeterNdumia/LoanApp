package com.pete.apps.loan;

public class LoanHistoryItem {

    String loanAmount;
    String loanApprovalDate;
    String loanRepayDate;

    public LoanHistoryItem(String loanAmount, String loanApprovalDate, String loanRepayDate){
        this.loanAmount =loanAmount;
        this.loanApprovalDate =loanApprovalDate;
        this.loanRepayDate =loanRepayDate;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public String getLoanApprovalDate() {
        return loanApprovalDate;
    }

    public String getLoanRepayDate() {
        return loanRepayDate;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setLoanApprovalDate(String loanApprovalDate) {
        this.loanApprovalDate = loanApprovalDate;
    }

    public void setLoanRepayDate(String loanRepayDate) {
        this.loanRepayDate = loanRepayDate;
    }
}
