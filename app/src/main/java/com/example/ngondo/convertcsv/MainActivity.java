package com.example.ngondo.convertcsv;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int ACTIVITY_CHOOSE_FILE = 1; //set the intent choose to always open the file chooser dialog

    //DB Varianblles
    DBController dbController;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    * Action after the selection of the csv file, we need to inmpoirt the data to our database
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case ACTIVITY_CHOOSE_FILE: {
                if (resultCode == RESULT_OK) {
                    //AsyncTask for importing the values to the sqlite db
                   // Toast.makeText(getApplicationContext(),"Intent chooser works well", Toast.LENGTH_SHORT).show();

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

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Importing CSV File. Please wait...");
            this.progressDialog.show();
        }

        // Import the csv file using content values
        @Override
        protected String doInBackground(String... strings) {


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
