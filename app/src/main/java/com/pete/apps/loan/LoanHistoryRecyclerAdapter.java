package com.pete.apps.loan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LoanHistoryRecyclerAdapter extends RecyclerView.Adapter<LoanHistoryRecyclerAdapter.ViewHolder> {



    ArrayList<String> loanAmountArray;
    ArrayList<String> loanApprovalDateArray;
    ArrayList<String> loanRepayDateArray;

    public LoanHistoryRecyclerAdapter(ArrayList<String> loanAmountArray, ArrayList<String> loanApprovalDateArray,   ArrayList<String> loanRepayDateArray){
        this.loanAmountArray =loanAmountArray;
        this.loanApprovalDateArray =loanApprovalDateArray;
        this.loanRepayDateArray =loanRepayDateArray;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (RelativeLayout) itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout cardView= (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_history_item,parent,false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RelativeLayout cardView= holder.cardView;

        TextView amountTxt= cardView.findViewById(R.id.loanAmountCard);
        amountTxt.setText(String.valueOf(loanAmountArray.get(position)));

        TextView approvalDateTxt= cardView.findViewById(R.id.loandatecard);
        approvalDateTxt.setText(String.valueOf(loanApprovalDateArray.get(position)));

        TextView repayDateTxt= cardView.findViewById(R.id.repaymentDateCard);
        repayDateTxt.setText(String.valueOf(loanRepayDateArray.get(position)));

    }

    @Override
    public int getItemCount() {
        return loanAmountArray.size();
    }


}
