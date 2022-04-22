package com.pete.apps.loan;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DB_NAME="LoanAppDb";
    public static final int DB_VERSION=1;


    public DatabaseHelper(Context context) {
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        String queryy=("CREATE TABLE USERAUTHENTICATION(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"+"FIRSTNAME TEXT,"+"LASTNAME TEXT,"+"EMAILADDRESS TEXT,"+"USERIDNO TEXT UNIQUE,"+"USERPINNUMBER TEXT,"+"PHONENUMBER TEXT UNIQUE,"+"FIREBASEUID TEXT);");
        sqLiteDatabase.execSQL(queryy);

        String query=("CREATE TABLE USERDETAILS(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"+"FIRSTNAME TEXT,"+"LASTNAME TEXT,"+"EMAILADDRESS TEXT,"+"USERIDNO TEXT,"+"USERPINNUMBER TEXT,"+"PHONENUMBER TEXT,"+"NEXTOFKINNAME TEXT,"+"KINRELATIONSHIP TEXT,"+"KINPHONENUMBER TEXT,"+"KINEMAILADDRESS TEXT);");
        sqLiteDatabase.execSQL(query);


        String query1=("CREATE TABLE NEWLOAN(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"+"LOANAMOUNT TEXT,"+"USERPHONENUMBER TEXT,"+"LOANAPPROVALDATE TEXT,"+"LOANPAYMENTPERIOD TEXT);");
        sqLiteDatabase.execSQL(query1);

        String query2=("CREATE TABLE REPAIDLOANS(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"+"APPROVEDLOANAMOUNT TEXT,"+"APPROVEDLOANDATE TEXT,"+"REPAYMENTDATE TEXT,"+"USERPHONENUMBER TEXT);");
        sqLiteDatabase.execSQL(query2);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Cursor readAllUserAuthenticated(){
        String query= "SELECT * FROM USERAUTHENTICATION";
        SQLiteDatabase sqLiteDatabase= this.getReadableDatabase();

        Cursor cursor= null;
        if (sqLiteDatabase!= null){
            cursor=  sqLiteDatabase.rawQuery(query,null);
        }
        return cursor;
    }
    public Cursor readAllUsers(){
        String query= "SELECT * FROM USERDETAILS";
        SQLiteDatabase sqLiteDatabase= this.getReadableDatabase();

        Cursor cursor= null;
        if (sqLiteDatabase!= null){
            cursor=  sqLiteDatabase.rawQuery(query,null);
        }
        return cursor;
    }
    public Cursor readAllNewLoanDetails(){
        String query= "SELECT * FROM NEWLOAN";
        SQLiteDatabase sqLiteDatabase= this.getReadableDatabase();

        Cursor cursor= null;
        if (sqLiteDatabase!= null){
            cursor=  sqLiteDatabase.rawQuery(query,null);
        }
        return cursor;
    }
    public Cursor readAllRepaidLoanDetails(){
        String query= "SELECT * FROM REPAIDLOANS";
        SQLiteDatabase sqLiteDatabase= this.getReadableDatabase();

        Cursor cursor= null;
        if (sqLiteDatabase!= null){
            cursor=  sqLiteDatabase.rawQuery(query,null);
        }
        return cursor;
    }
}
