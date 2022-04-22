package com.pete.apps.loan;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class MakePaymentActivity extends AppCompatActivity {

    FirebaseUser user;
    String id;
    TextView loanAmntTxtView;
    String loanAmount;
    Button paymentBtn;
    EditText paidAmount;

    SQLiteOpenHelper sqLiteOpenHelper= new DatabaseHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String phoneNumber, loanDate, repayDate;
    Calendar calendar;
    SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        id = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(user.getUid());



        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        repayDate =dateFormat.format(calendar.getTime());

        loanAmntTxtView = findViewById(R.id.loanAmountLbl);

        SharedPreferences prefs =  getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phoneNumber = prefs.getString("phoneNumber", NULL);



        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM NEWLOAN WHERE USERPHONENUMBER = ?", new String[]{phoneNumber});
        if (cursor.getCount() == 0){

            Intent intent = new Intent(MakePaymentActivity.this, LoanActivity.class);
            startActivity(intent);
        }else{
            while (cursor.moveToNext()){
                loanAmount = cursor.getString(1);
                loanDate = cursor.getString(3);
                loanAmntTxtView.setText("Your loan amount is KES " +loanAmount);
            }
        }

        paymentBtn = findViewById(R.id.makePaymentBtn);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();

            }
        });
    }
    public void updateData(){
        paidAmount = findViewById(R.id.paymentAmountTxt);




        if(TextUtils.isEmpty(paidAmount.getText().toString()) || Integer.parseInt(paidAmount.getText().toString()) > Integer.parseInt(loanAmount) ){
            paidAmount.setError("Please enter a valid amount");
            paidAmount.requestFocus();

        }else {

            String paidAmountStr = paidAmount.getText().toString();
            int loanBalance = Integer.parseInt(loanAmount) - Integer.parseInt(paidAmountStr);
            String loanBalanceStr = String.valueOf(loanBalance);

            //update loanamount in newloan table
            ContentValues cv = new ContentValues();
            cv.put("LOANAMOUNT", loanBalanceStr);
            sqLiteDatabase.update("NEWLOAN", cv, "USERPHONENUMBER =?", new String[]{phoneNumber});


            if (loanBalanceStr.equals("0")) {
                //remove from table newloans
                ContentValues values = new ContentValues();
                values.put("APPROVEDLOANAMOUNT", loanAmount);
                values.put("APPROVEDLOANDATE", loanDate);
                values.put("REPAYMENTDATE", repayDate);
                values.put("USERPHONENUMBER", phoneNumber);

                sqLiteDatabase.insert("REPAIDLOANS", null, values);

                sqLiteDatabase.delete("NEWLOAN","USERPHONENUMBER =?", new String[]{phoneNumber});

                sqLiteDatabase.close();



                Intent intent = new Intent(MakePaymentActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(MakePaymentActivity.this, "You have completely repaid your loan", Toast.LENGTH_LONG).show();



            } else {
                Toast.makeText(MakePaymentActivity.this, "You still have a loan balance of " + loanBalanceStr, Toast.LENGTH_LONG).show();
            }
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MakePaymentActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return(super.onOptionsItemSelected(item));
    }

}