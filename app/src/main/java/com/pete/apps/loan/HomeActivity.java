package com.pete.apps.loan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;


public class HomeActivity extends AppCompatActivity {

    //check if user exist on newloans table
    SQLiteOpenHelper sqLiteOpenHelper= new DatabaseHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    String phoneNumber;
    FirebaseUser user;
    Button history;
    Button repayLoan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setElevation(0);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

       SharedPreferences prefs =  getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phoneNumber = prefs.getString("phoneNumber", NULL);

        repayLoan = findViewById(R.id.buttonRepayLOan);
        repayLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkIfUserHasLoan();
            }
        });




        findViewById(R.id.buttonApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                readAndCheckLoan();

            }
        });

        history = findViewById(R.id.buttonHistory);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LoanHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
    public void  checkIfUserHasLoan(){

        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM NEWLOAN WHERE USERPHONENUMBER = ?", new String[]{phoneNumber});

        if(cursor.getCount() != 0){


            Intent intent = new Intent(HomeActivity.this, MakePaymentActivity.class);
            startActivity(intent);
            finish();

        }else{

            Toast.makeText(HomeActivity.this,"Apply for a loan first", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HomeActivity.this, LoanActivity.class);
            startActivity(intent);
            finish();
        }

    }
    private void readAndCheckLoan() {

        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM NEWLOAN WHERE USERPHONENUMBER = ?", new String[]{phoneNumber});

        if(cursor.getCount() != 0){


            Intent intent = new Intent(HomeActivity.this, DeclinedLoanActivity.class);
            startActivity(intent);
            //finish();

        }else{

            Intent intent = new Intent(HomeActivity.this, LoanActivity.class);
            startActivity(intent);
            //finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logoutmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_logout:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void logOut(){
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        finish();

    }
}