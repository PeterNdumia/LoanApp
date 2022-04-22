package com.pete.apps.loan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;

import java.util.Calendar;



import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class LoanActivity extends AppCompatActivity {


    Button applyBtn;
    String phoneNumber;

    String loanStatus = "unpaid";
    String loanDate;
    NewLoan newLoan;
    Calendar calendar;
    SimpleDateFormat dateFormat;
    FirebaseUser user;
    String id, repayPeriod ;
    String loanAmount ="500";
    Spinner loanSpinner, repaySpinner;

    SQLiteOpenHelper sqLiteOpenHelper= new DatabaseHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    private FirebaseAuth mAuth;
    String[] loans = { "500", "1000",
            "1500", "2000",
            "2500", "3000" };
    String[] days = { "7 days", "14 days"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        id = user.getUid();

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        loanDate =  dateFormat.format(calendar.getTime());

        loanSpinner = findViewById(R.id.loanAmountSpinner);
        repaySpinner = findViewById(R.id.repayPeriodSpinner);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, loans);
        loanSpinner.setAdapter(adapter);
        loanSpinner.setOnItemSelectedListener(new amountSpinnerClass());

        ArrayAdapter adaptertwo = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, days);
        repaySpinner.setAdapter(adaptertwo);
        repaySpinner.setOnItemSelectedListener(new daySpinnerClass());

        SharedPreferences prefs =  getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phoneNumber = prefs.getString("phoneNumber", NULL);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM REPAIDLOANS WHERE USERPHONENUMBER = ?", new String[]{phoneNumber});

        if(cursor.getCount() == 0){
          loanSpinner.setOnItemSelectedListener(new loanFrstClass());
        }else{

           loanSpinner.setOnItemSelectedListener(new amountSpinnerClass());

        }

        applyBtn = findViewById(R.id.buttonApply);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeData();
            }
        });


    }
    class loanFrstClass implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            if(!adapterView.getItemAtPosition(i).toString().equals("500")){
                Toast.makeText(LoanActivity.this,"This amount is not allowed for first time users. Available limit is KES 500", Toast.LENGTH_LONG ).show();
                loanAmount = "500";
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    class amountSpinnerClass implements   AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            loanAmount = adapterView.getItemAtPosition(position).toString();


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    class daySpinnerClass implements   AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            repayPeriod = adapterView.getItemAtPosition(position).toString();

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    private void writeData() {


        if (loanSpinner.getSelectedItem() != null && repaySpinner.getSelectedItem()!=null && loanDate!=null) {

            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            ContentValues detailsValue = new ContentValues();
            detailsValue.put("LOANAMOUNT", loanAmount);
            detailsValue.put("USERPHONENUMBER", phoneNumber);
            detailsValue.put("LOANAPPROVALDATE", loanDate);
            detailsValue.put("LOANPAYMENTPERIOD", repayPeriod);


            sqLiteDatabase.insert("NEWLOAN", null, detailsValue);
            sqLiteDatabase.close();//enter data


        } else {
            //newLoan = new NewLoan(id,phoneNumber, loanAmount, loanStatus, loanDate);
            Toast.makeText(LoanActivity.this, "Error", Toast.LENGTH_LONG).show();


        }
        Intent intent = new Intent(LoanActivity.this, LoanApprovalActivity.class);
        startActivity(intent);
        finish();

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