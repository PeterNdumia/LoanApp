package com.pete.apps.loan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Date;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class LoanApprovalActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    FirebaseUser user;
    Calendar calendar;
    SimpleDateFormat dateFormat;
    String id, phoneNumber;
    String loanApprovalDate, loanAmount;
    String todayDate;
    SQLiteOpenHelper sqLiteOpenHelper= new DatabaseHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;


    private FirebaseAuth mAuth;

    //checK newloans table.Check days passed
    //add penalty

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_approval);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        id = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(user.getUid());

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        todayDate = dateFormat.format(calendar.getTime());

        SharedPreferences prefs =  getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phoneNumber = prefs.getString("phoneNumber", NULL);

       sendReminders();
       updatePenaltyAmount();



        



    }
    public void sendReminders(){

        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM NEWLOAN WHERE USERPHONENUMBER = ?", new String[]{phoneNumber});

        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                loanApprovalDate = cursor.getString(3);
            }




        }else{

            //Toast.makeText(LoanApprovalActivity.this,"Your credentials are incorrect.Please try again", Toast.LENGTH_LONG).show();

        }

        //long reminderOnePeriod = 1000*60*60*24*9;
        long reminderTwoPeriod = 1000*60*60*24*15;

        long reminderOnePeriod = 10;


        try {
            Date today = dateFormat.parse(todayDate);
            Date approvaldate = dateFormat.parse(loanApprovalDate);
            if(today.getTime() -  approvaldate.getTime() == reminderOnePeriod ){
                NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify=new Notification.Builder
                        (getApplicationContext()).setContentTitle("Remember to pay your loan").setContentText("Hello, we hope that you remember to pay your loan before the end of the 14 day period.Purpose to pay as soon as possible to increase your loan limit")
                        .setSmallIcon(R.drawable.ic_notification).build();

                notify.flags |= Notification.FLAG_AUTO_CANCEL;
                notif.notify(0, notify);
            }
            if(today.getTime()- approvaldate.getTime() == reminderTwoPeriod){
                NotificationManager notif2=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify2=new Notification.Builder
                        (getApplicationContext()).setContentTitle("The payment period is up").setContentText("Hello, you have not paid you loan during the required period. This will attract a daily 2% penalty. Purpose to pay as soon as possible to increase your loan limit")
                        .setSmallIcon(R.drawable.ic_notification).build();

                notify2.flags |= Notification.FLAG_AUTO_CANCEL;
                notif2.notify(0, notify2);
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void updatePenaltyAmount(){
        long penaltyPeriod = 1000*60*60*24*15;
        try {
            Date today = dateFormat.parse(todayDate);
            Date approvaldate = dateFormat.parse(loanApprovalDate);
            sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM NEWLOAN WHERE USERPHONENUMBER = ?", new String[]{phoneNumber});

            if(cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    loanAmount = cursor.getString(3);
                }
            }

            if(today.getTime() - approvaldate.getTime() == penaltyPeriod){
                double newLoanAmount = Double.parseDouble(loanAmount)*1.02;
                String newLoanAmountStr = String.valueOf(newLoanAmount);

                ContentValues contentValues = new ContentValues();
                contentValues.put("LOANAMOUNT", newLoanAmountStr);

                sqLiteDatabase.insert("NEWLOANS", null, contentValues);
                sqLiteDatabase.close();

            }
        }catch (Exception e){

        }





    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoanApprovalActivity.this, HomeActivity.class);
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
    //function to calculate 2% penalty and update data in firebase
}