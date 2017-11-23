package com.example.ngondo.convertcsv;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int ACTIVITY_CHOOSE_FILE = 1; //set the intent choose to always open the file chooser
    public static final int PERMISSION_REQUEST_CODE = 1;

    //DB Variables
    public DBController dbController;
    public SQLiteDatabase db;

    public static String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Check the permissions after creating the app
        isWriteStoragePermissionGranted();

        //Initialize the DB values in onCreate()
        dbController = new DBController(getApplicationContext());
        db = dbController.getWritableDatabase();

        /*
        * Action button for selecting and uploading the csv
        * */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isReadStoragePermissionGranted();
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                fileIntent.setType("text/csv");
                try{
                    startActivityForResult(Intent.createChooser(fileIntent, "open csv"), ACTIVITY_CHOOSE_FILE );
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MainActivity.this, "No app found to open the File", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*
    * Runtime permissions for devices with Marshmallow and above
    * */
    public boolean isReadStoragePermissionGranted(){
        int requestReadPermissions = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if(Build.VERSION.SDK_INT >= 23){
            if(requestReadPermissions == PackageManager.PERMISSION_GRANTED){
                Log.v("PERMISSION_GRANTED", "Permission is granted");
                return true;
            }else{
                Log.v("PERMISSION_REVOKED", "Permission is Revoked");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        }else{
            Log.v("PERMISSION_GRANTED", "Permission is granted");
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted(){
        int requestWritePermissions = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(Build.VERSION.SDK_INT >= 23){
            if((requestWritePermissions == PackageManager.PERMISSION_GRANTED)){
                Log.v("PERMISSION_GRANTED", "Permission is granted");
                return true;
            }else{
                Log.v("PERMISSION_REVOKED", "Permission is Revoked");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        }else{
            Log.v("PERMISSION_GRANTED", "Permission is granted");
            return true;
        }
    }

    /*
    * Result handshake for runtime permissions
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.e("PERMISSION_GRANTED", "Permission is granted");
                } else {
                    Log.e("PERMISSION_REVOKED", "Permission is Revoked");
                }
        }
    }

    /* Action after the selection of the csv file, we need to inmpoirt the data to our database
            * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ACTIVITY_CHOOSE_FILE: {
                filePath = data.getData().getPath();

                db.beginTransaction();
                if (resultCode == RESULT_OK) {
                    //AsyncTask for importing the values to the sqlite db
                     new ImportCSV().execute("");

//                    Toast.makeText(getApplicationContext(),"Intent chooser works well", Toast.LENGTH_SHORT).show();
                    Log.d("FILE_UPLOAD",filePath);

                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * Importing the csv file to the DB using DBcontroller helper class
    * */
    class ImportCSV extends AsyncTask<String, String, String> {

//        ProgressDialog progressDialog;

        //Throws this exception if the file is not found, or the wrong format is chosen
//        ImportCSV() throws FileNotFoundException {
//            Toast.makeText(getApplicationContext(), "CSV File not found. Failed to import. Try Again", Toast.LENGTH_SHORT).show();
//
//        }

//        @Override
//        protected void onPreExecute() {
//            this.progressDialog.setMessage("Importing CSV File. Please wait...");
//            this.progressDialog.show();
//        }

        // Import the csv file using content values
        @Override
        protected String doInBackground(String... strings) {
            File csvFile = new File(filePath);
            BufferedReader fileReader = null;
            try {
                fileReader = new BufferedReader(new FileReader(csvFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String line = ""; //initialize empty string to read the values
            ContentValues cv = new ContentValues();

            try {
                while((line = fileReader.readLine())!= null){
                    String[] str = line.split(",", 3); //Split the values to the three columns

                    String admno = str[0].toString();
                    String name = str[1].toString();
                    String pnumber = str[2].toString();

                    /*
                    * Input the values into content Values
                    * */
                    cv.put(DBModel.Columns.ADMNO, admno);
                    cv.put(DBModel.Columns.STUDENTNAME, name);
                    cv.put(DBModel.Columns.PARENTNO, pnumber);

                    db.insert(DBModel.DBNAME, null, cv);
                }
                db.setTransactionSuccessful();
                db.endTransaction();

            } catch (IOException e) {
                if(db.inTransaction()){
                    db.endTransaction();
                    Log.e("DB_ERROR", e.getMessage().toString());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
