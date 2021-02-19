package com.example.scannerbarcode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.BuildConfig;
import com.facebook.stetho.Stetho;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final  String TAG = "MainActivity";
    //call sql
    SQLiteDataBaseHelper controller  = new SQLiteDataBaseHelper(this);
    //ProgressDialog prgDialog;
    //SQLiteDataBaseHelper mDatabaseHelper;
    private Button btnsave,btnclear,btnlook;
    private EditText carnumber1,cusnumber1,pronumber1,number1;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //When Sync action button is clicked
        if (id == R.id.refresh) {
            //Sync SQLite DB data to remote MySQL DB
            syncSQLiteMySQLDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void syncSQLiteMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> userList =  controller.getAllUsers();
        if(userList.size()!=0){
            if(controller.dbSyncCount() != 0){
                //prgDialog.show();
                params.put("usersJSON", controller.composeJSONfromSQLite());
                client.post("http://192.168.0.157/sqlitemysqlsync/phpserver.php",params ,new AsyncHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        Log.d(TAG, new String(responseBody) );
                        //prgDialog.hide();
                        try {
                            Log.d(TAG, "You onSuccess on " );
                            //JSONObject arr = new JSONObject(new String(responseBody));
                            //System.out.println(arr.length());
                            JSONArray arr = new JSONArray(new String(responseBody));
                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);
                                System.out.println(obj.get("id"));
                                System.out.println(obj.get("status"));
                                controller.updateSyncStatus(obj.get("id").toString(),obj.get("status").toString());
                            }
                            Toast.makeText(getApplicationContext(), "DB Sync completed!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Log.d(TAG, "You onSuccess on " +e);
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // TODO Auto-generated method stub
                        //prgDialog.hide();
                        Log.d(TAG, "You Failure on " +error);
                        if(statusCode == 404){
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }else if(statusCode == 500){
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                        }
                    }

                });
            }else{
                Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No data in SQLite DB, please do enter User name to perform Sync action", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //text variable
        carnumber1 = (EditText) findViewById(R.id.carnumber);
        cusnumber1 = (EditText) findViewById(R.id.cusnumber);
        pronumber1 = (EditText) findViewById(R.id.pronumber);
        number1 = (EditText) findViewById(R.id.number);
        //btn variable
        btnsave = (Button) findViewById(R.id.btnsave);
        btnclear = (Button) findViewById(R.id.btnclear);
        btnlook = (Button) findViewById(R.id.btnlook);


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry1 = carnumber1.getText().toString();
                String newEntry2 = cusnumber1.getText().toString();
                String newEntry3 = pronumber1.getText().toString();
                String newEntry4 = number1.getText().toString();
                if (newEntry1.length() != 0 && newEntry2.length() != 0 && newEntry3.length() != 0 && newEntry4.length() != 0) {
                    AddData(newEntry1,newEntry2,newEntry3,newEntry4);
                    Toast.makeText(getBaseContext(), "already Save!", Toast.LENGTH_SHORT).show();
                    carnumber1.setText("");
                    cusnumber1.setText("");
                    pronumber1.setText("");
                    number1.setText("");

                } else {
                    toastMessage("You must put something in the text field!");
                }

            }
        });
        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carnumber1.setText("");
                cusnumber1.setText("");
                pronumber1.setText("");
                number1.setText("");
            }
        });
        btnlook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
                startActivity(intent);
            }
        });

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    public void AddData(String nEntry1,String nEntry2,String nEntry3,String nEntry4) {
        boolean insertData = controller.addData(nEntry1, nEntry2, nEntry3,nEntry4);

        if (insertData) {
            toastMessage("Data Successfully Inserted!");
        } else {
            toastMessage("Something went wrong");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}