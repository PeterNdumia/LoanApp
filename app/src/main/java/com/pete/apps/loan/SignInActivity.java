package com.pete.apps.loan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class SignInActivity extends AppCompatActivity {


    //Retrieve user details from user authetication table and  allow sign in. then redirect to homeactivity.
    SQLiteOpenHelper sqLiteOpenHelper= new DatabaseHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    EditText phoneNumberTxt, pinTxt;
    String phoneNumber, pinNumber;
    Button signInButton;
    String phoneNumberRt;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTheme(R.style.AppPageTheme);
        getSupportActionBar().setElevation(0);

        SharedPreferences prefs =  getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phoneNumberRt = prefs.getString("phoneNumber", NULL);

        phoneNumberTxt = findViewById(R.id.phoneNumberEditText);
        phoneNumberTxt.setText(phoneNumberRt);
        pinTxt = findViewById(R.id.pinEditText);
        signInButton =findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();

            }
        });
    }
    public void signInUser(){
        pinNumber =pinTxt.getText().toString();
        phoneNumber= phoneNumberTxt.getText().toString();
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM USERAUTHENTICATION WHERE PHONENUMBER = ? and USERPINNUMBER  = ?", new String[]{phoneNumber, pinNumber});



        if(cursor.getCount() != 0){

            checkIfUserDetailsAvailable();


        }else{

           Toast.makeText(SignInActivity.this,"Your credentials are incorrect.Please try again", Toast.LENGTH_LONG).show();

        }

    }
    public void checkIfUserDetailsAvailable(){

        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM USERDETAILS WHERE PHONENUMBER = ?", new String[]{phoneNumberRt});
        if (cursor.getCount() == 0){

            Intent intent = new Intent(SignInActivity.this, UserDetailsActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM USERAUTHENTICATION WHERE FIREBASEUID = ?", new String[]{user.getUid()});
            if(cursor.getCount() == 0) {

                Intent intent = new Intent(SignInActivity.this, RegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);


            }

        }else {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}