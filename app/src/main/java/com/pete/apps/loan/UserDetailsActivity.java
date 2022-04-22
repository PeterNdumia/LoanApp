package com.pete.apps.loan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class UserDetailsActivity extends AppCompatActivity {


    //fetch details in user authentication table
    SQLiteOpenHelper sqLiteOpenHelper = new DatabaseHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    EditText firstnameTxt, lastnameTxt, emailTxt, idNoTxt, nextofkinnameTxt, kinphonenumberTxt, kinEmailAddressTxt;
    String phoneNumber;
    String kinRelationship;
    String firstname, lastname, email, idNo, pinNo;
    Button submitBtn;
    Spinner kinRelationshipSpinner;
    String kinPhoneNumber, kinName, kinEmailAddress;
    String[] relations = new String[]{"Father","Mother", "Brother","Sister","Uncle","Aunt","Guardian"};

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.READ_CONTACTS };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        getSupportActionBar().setElevation(0);



        SharedPreferences prefs = getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phoneNumber = prefs.getString("phoneNumber", NULL);

        firstnameTxt = findViewById(R.id.detFirstNameEText);
        lastnameTxt = findViewById(R.id.detLastNameEText);
        emailTxt = findViewById(R.id.detEmailEText);
        idNoTxt = findViewById(R.id.detIDnoEText);
        nextofkinnameTxt = findViewById(R.id.detKinNameEText);
        kinEmailAddressTxt =findViewById(R.id.detKinEmailEText);

        kinRelationshipSpinner = findViewById(R.id.detKinRelationshipSpinner);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, relations);
        kinRelationshipSpinner.setAdapter(adapter);
        kinRelationshipSpinner.setOnItemSelectedListener(new kinSpinnerClass());

        kinphonenumberTxt = findViewById(R.id.detKinPhoneEText);
        kinphonenumberTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checkPermissions();
                getKinDetails();
            }
        });


        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM USERAUTHENTICATION WHERE PHONENUMBER = ?", new String[]{phoneNumber});


        if(cursor.getCount() != 0){
            while (cursor.moveToNext()) {
                firstname = cursor.getString(1);
                lastname = cursor.getString(2);
                email = cursor.getString(3);
                idNo = cursor.getString(4);
                pinNo = cursor.getString(5);


                firstnameTxt.setText(firstname);
                lastnameTxt.setText(lastname);
                emailTxt.setText(email);
                idNoTxt.setText(idNo);
            }

        }else{

            Toast.makeText(UserDetailsActivity.this,"Error", Toast.LENGTH_LONG).show();

        }

        submitBtn = findViewById(R.id.submitdetailsBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterUserDetails();
            }
        });







    }
    class kinSpinnerClass implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

             kinRelationship = adapterView.getItemAtPosition(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            TextView errorText = (TextView)kinRelationshipSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("This field cannot be empty");

        }
    }

    public void getKinDetails(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c = getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {

                kinPhoneNumber = "";
                kinEmailAddress = "";
                kinName= c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));


                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1"))
                    hasPhone = "true";
                else
                    hasPhone = "false";

                if (Boolean.parseBoolean(hasPhone)) {
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        kinPhoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phones.close();
                }

                // Find Email Addresses
                Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                while (emails.moveToNext()) {
                    kinEmailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                emails.close();


                nextofkinnameTxt.setText(kinName);
                kinphonenumberTxt.setText(kinPhoneNumber);
                kinEmailAddressTxt.setText(kinEmailAddress);

            }
            c.close();
        }
    }






    public void enterUserDetails(){


        Boolean valid = true;
        if(TextUtils.isEmpty(firstnameTxt.getText().toString())){
            firstnameTxt.setError("This field cannot be empty");
            valid =false;
        }
        if(TextUtils.isEmpty(lastnameTxt.getText().toString())){
            lastnameTxt.setError("This field cannot be empty");
            valid =false;
        }
        if(TextUtils.isEmpty(emailTxt.getText().toString())){
            emailTxt.setError("This field cannot be empty");
            valid =false;
        }
        if(TextUtils.isEmpty(idNoTxt.getText().toString())){
            idNoTxt.setError("This field cannot be empty");
            valid =false;
        }
        if(TextUtils.isEmpty(nextofkinnameTxt.getText().toString())){
            nextofkinnameTxt.setError("This field cannot be empty");
            valid =false;
        }

        if(TextUtils.isEmpty(kinphonenumberTxt.getText().toString())){
            kinphonenumberTxt.setError("This field cannot be empty");
            valid =false;
        }
        if(TextUtils.isEmpty(kinEmailAddressTxt.getText().toString())){
            kinEmailAddressTxt.setError("This field cannot be empty");
            valid =false;
        }


        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        if(valid) {
            try {
                ContentValues detailsValue = new ContentValues();
                detailsValue.put("FIRSTNAME", firstnameTxt.getText().toString());
                detailsValue.put("LASTNAME", lastnameTxt.getText().toString());
                detailsValue.put("EMAILADDRESS", emailTxt.getText().toString());
                detailsValue.put("USERIDNO", idNoTxt.getText().toString());
                detailsValue.put("USERPINNUMBER", pinNo);
                detailsValue.put("PHONENUMBER", phoneNumber);
                detailsValue.put("NEXTOFKINNAME", nextofkinnameTxt.getText().toString());
                detailsValue.put("KINRELATIONSHIP", kinRelationship);
                detailsValue.put("KINPHONENUMBER", kinphonenumberTxt.getText().toString());
                detailsValue.put("KINEMAILADDRESS", kinEmailAddressTxt.getText().toString());


                sqLiteDatabase.insert("USERDETAILS", null, detailsValue);
                sqLiteDatabase.close();

                Intent intent = new Intent(UserDetailsActivity.this, HomeActivity.class);
                startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(UserDetailsActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
            }

        }





    }
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
             //   initialize();


                break;
        }
    }

}