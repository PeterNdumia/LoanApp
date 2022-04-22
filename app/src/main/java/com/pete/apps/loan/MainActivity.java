package com.pete.apps.loan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    EditText editTextCountryCode, editTextPhone;
    Button buttonContinue;
    private FirebaseAuth mAuth;
    //Enter details to user authentication table.
    SQLiteOpenHelper sqLiteOpenHelper= new DatabaseHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    String phoneNumberRt;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.AppPageTheme);
        getSupportActionBar().setElevation(0);
        mAuth = FirebaseAuth.getInstance();

        //Firebase Notification service
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (!task.isSuccessful()) {
                            task.getException();
                            return;
                        }
                        //String token = task.getResult().getToken();

                    }
                });


        editTextCountryCode = findViewById(R.id.editTextCountryCode);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonContinue = findViewById(R.id.buttonContinue);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCountryCode.getText().toString().trim();
                String number = editTextPhone.getText().toString().trim();

                if (number.isEmpty() || number.length() < 9) {
                    editTextPhone.setError("Valid number is required");
                    editTextPhone.requestFocus();
                    return;
                }

                String phoneNumber = code + number;

                Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
       /* mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


       if (FirebaseAuth.getInstance().getCurrentUser() != null) {
           sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
           cursor = sqLiteDatabase.rawQuery("SELECT * FROM USERAUTHENTICATION WHERE FIREBASEUID = ?", new String[]{user.getUid()});
        if(cursor.getCount() == 0){

            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else{


                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                    startActivity(intent);
                }

        }else {
           Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


           startActivity(intent);
       }*/
    }

}