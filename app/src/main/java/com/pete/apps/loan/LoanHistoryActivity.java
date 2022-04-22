package com.pete.apps.loan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class LoanHistoryActivity extends AppCompatActivity {

    SQLiteOpenHelper sqLiteOpenHelper= new DatabaseHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    String phoneNumber;
    String loanAmount, loanApprovalDate, loanRepayDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        SharedPreferences prefs =  getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phoneNumber = prefs.getString("phoneNumber", NULL);
        populateRecycler();
    }
    public void populateRecycler(){
        RecyclerView loanHistoryRecycler = findViewById(R.id.loanHistoryRecycler);
        try{
            sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

            ArrayList<String> loanAmountArray = new ArrayList<>();
            ArrayList<String> loanApprovalDateArray = new ArrayList<>();
            ArrayList<String> loanRepayDateArray = new ArrayList<>();


            cursor = sqLiteDatabase.rawQuery("SELECT * FROM REPAIDLOANS WHERE USERPHONENUMBER = ?", new String[]{phoneNumber});
            if(cursor.getCount() != 0){

                while (cursor.moveToNext()) {
                    loanAmount = cursor.getString(1);
                    loanApprovalDate = cursor.getString(2);
                    loanRepayDate = cursor.getString(3);

                    loanAmountArray.add(loanAmount);
                    loanApprovalDateArray.add(loanApprovalDate);
                    loanRepayDateArray.add(loanRepayDate);


                }

            }else{
                Toast.makeText(LoanHistoryActivity.this,"There are no prior cleared loans", Toast.LENGTH_LONG).show();

            }

            LoanHistoryRecyclerAdapter loanHistoryRecyclerAdapter = new LoanHistoryRecyclerAdapter(loanAmountArray, loanApprovalDateArray, loanRepayDateArray);
            loanHistoryRecycler.setAdapter(loanHistoryRecyclerAdapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            loanHistoryRecycler.setLayoutManager(linearLayoutManager);

        }catch (SQLException e){
            Toast.makeText(this,"Database Error", Toast.LENGTH_LONG).show();
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }





}