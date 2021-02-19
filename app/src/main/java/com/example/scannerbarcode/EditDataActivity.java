package com.example.scannerbarcode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.annotation.Nullable;

public class EditDataActivity extends AppCompatActivity {

    private static final String TAG = "EditDataActivity";

    private Button btnSave,btnDelete;
    //private EditText editable_item;

    SQLiteDataBaseHelper mDatabaseHelper;

    private String selectedcarnumber,selectedcusnumber,selectedpronumber,selectednumber;
    private int selectedID;
    private EditText carnumber,cusnumber,pronumber,number;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_layout);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        //text variable
        carnumber = (EditText) findViewById(R.id.carnumber);
        cusnumber = (EditText) findViewById(R.id.cusnumber);
        pronumber = (EditText) findViewById(R.id.pronumber);
        number = (EditText) findViewById(R.id.number);
        mDatabaseHelper = new SQLiteDataBaseHelper(this);

        //get the intent extra from the ListDataActivity
        Intent receivedIntent = getIntent();

        //now get the itemID we passed as an extra
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value

        //now get the name we passed as an extra
        selectedcarnumber= receivedIntent.getStringExtra("carnumber");
        selectedcusnumber = receivedIntent.getStringExtra("cusnumber");
        selectedpronumber= receivedIntent.getStringExtra("pronumber");
        selectednumber = receivedIntent.getStringExtra("number");

        //set the text to show the current selected name
        carnumber.setText(selectedcarnumber);
        cusnumber.setText(selectedcusnumber);
        pronumber.setText(selectedpronumber);
        number.setText(selectednumber);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item1 = carnumber.getText().toString();
                String item2 = cusnumber.getText().toString();
                String item3 = pronumber.getText().toString();
                String item4 = number.getText().toString();
                if(!item1.equals("")&&!item2.equals("")&&!item3.equals("")&&!item4.equals("")){
                    Log.d(TAG, "onItemClick: You Clicked on " + selectedpronumber);
                    mDatabaseHelper.updateName(item1,item2,item3,item4,selectedID);
                    Log.d(TAG, "You Clicked SUS");
                    Intent intent = new Intent();
                    intent.setClass(EditDataActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    toastMessage("You must enter a name");
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteName(selectedID);
                //set the text to show the current selected name
                carnumber.setText(selectedcarnumber);
                cusnumber.setText(selectedcusnumber);
                pronumber.setText(selectedpronumber);
                number.setText(selectednumber);
                toastMessage("removed from database");
                Intent intent = new Intent();
                intent.setClass(EditDataActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
