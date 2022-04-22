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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class RegistrationActivity extends AppCompatActivity {

    //Enter details to user authentication table.
    SQLiteOpenHelper sqLiteOpenHelper = new DatabaseHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    Button registerBtn;
    EditText firstname, lastname, idNo, email, pin, confirmpin;
    String phoneNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTheme(R.style.AppPageTheme);
        getSupportActionBar().setElevation(0);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        firstname = findViewById(R.id.regFirstNameEText);
        lastname = findViewById(R.id.regLastNameEText);
        idNo = findViewById(R.id.regIDnoEText);
        email = findViewById(R.id.regEmailEText);
        pin = findViewById(R.id.regPinEText);
        confirmpin = findViewById(R.id.regComfirmPinEText);






        SharedPreferences prefs = getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phoneNumber = prefs.getString("phoneNumber", NULL);


        registerBtn = findViewById(R.id.regUserBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        //check if user registration details already exist.redirect to sign in activity
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM USERAUTHENTICATION WHERE PHONENUMBER = ?", new String[]{phoneNumber});
        if (cursor.getCount() == 0) {
            registerUser();

        } else {
            while (cursor.moveToNext()) {

                Intent intent = new Intent(RegistrationActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                startActivity(intent);


            }

        }

    }

    public void registerUser() {


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            Boolean valid = true;
            if(TextUtils.isEmpty(firstname.getText().toString())){
                firstname.setError("This field cannot be empty");
                valid =false;
            }
            if(TextUtils.isEmpty(lastname.getText().toString())){
                lastname.setError("This field cannot be empty");
                valid =false;
            }
            if(TextUtils.isEmpty(email.getText().toString())){
                email.setError("This field cannot be empty");
                valid =false;
            }
            if(TextUtils.isEmpty(idNo.getText().toString()) || idNo.length() < 7){
                idNo.setError("Please enter the correct details");
                valid =false;
            }
            if(TextUtils.isEmpty(pin.getText().toString()) || pin.length() <4){
                pin.setError("Set a pin of up to 4 numbers");
                valid =false;
            }
            if(!pin.getText().toString().equals(confirmpin.getText().toString()) ){
                confirmpin.setError("Your PINs do not match");
                valid = false;
            }


            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            String phoneNumberCheck = phoneNumber;
            String idNoCheck =idNo.getText().toString();

            cursor = sqLiteDatabase.rawQuery("SELECT * FROM USERAUTHENTICATION WHERE PHONENUMBER = ? and USERIDNO  = ?", new String[]{phoneNumberCheck, idNoCheck});

            if(cursor.getCount() == 0 && valid ) {

                try {
                    ContentValues detailsValue = new ContentValues();
                    detailsValue.put("FIRSTNAME", firstname.getText().toString());
                    detailsValue.put("LASTNAME", lastname.getText().toString());
                    detailsValue.put("EMAILADDRESS", email.getText().toString());
                    detailsValue.put("USERIDNO", idNo.getText().toString());
                    detailsValue.put("USERPINNUMBER", pin.getText().toString());
                    detailsValue.put("PHONENUMBER", phoneNumber);
                    detailsValue.put("FIREBASEUID", user.getUid());


                    sqLiteDatabase.insert("USERAUTHENTICATION", null, detailsValue);
                    sqLiteDatabase.close();
                }catch (Exception e){
                    Toast.makeText(RegistrationActivity.this,"Registration failed", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(RegistrationActivity.this, UserDetailsActivity.class);
                startActivity(intent);


            }else{

                //Toast.makeText(RegistrationActivity.this,"The mobile number and ID number have already been registered", Toast.LENGTH_LONG).show();
            }

        } else {
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


            startActivity(intent);
        }
    }
}