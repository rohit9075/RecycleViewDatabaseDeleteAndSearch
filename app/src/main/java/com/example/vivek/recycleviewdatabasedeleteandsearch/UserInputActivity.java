package com.example.vivek.recycleviewdatabasedeleteandsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UserInputActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText mTextInputEditTextFirstName;
    private TextInputEditText mTextInputEditTextSecondName;
    private Button mButtonSubmit, mButtonUserList;
    private ListDataModel listDataModel;
    private DatabaseHelper databaseHelper;


    private final AppCompatActivity activity = UserInputActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);

        // method calls
        initiateView();
        initiateListeners();
        initiateObjects();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_submit:
                postDataToDatabase();
                emptyInputEditText();
                break;
            case R.id.button_userList:
                Intent mIntentUserDataDisplayActivity = new Intent(this, UserDataDisplayActivity.class);
                startActivity(mIntentUserDataDisplayActivity);

        }

    }


    private void initiateView() {
        mTextInputEditTextFirstName = (TextInputEditText) findViewById(R.id.textInputEditText_first_name);
        mTextInputEditTextSecondName = (TextInputEditText) findViewById(R.id.textInputEditText_second_name);
        mButtonSubmit = (Button) findViewById(R.id.button_submit);
        mButtonUserList = (Button) findViewById(R.id.button_userList);

    }

    private void initiateListeners() {
        mButtonSubmit.setOnClickListener(this);
        mButtonUserList.setOnClickListener(this);

    }

    private void initiateObjects() {
        listDataModel = new ListDataModel();
        databaseHelper = new DatabaseHelper(activity);

    }

    private void postDataToDatabase() {
        listDataModel.setFirstName(mTextInputEditTextFirstName.getText().toString().trim());
        listDataModel.setSecondName(mTextInputEditTextSecondName.getText().toString().trim());

        databaseHelper.addUser(listDataModel);

        Toast.makeText(this, "Data Saved", Toast.LENGTH_LONG).show();
    }

    private void emptyInputEditText() {
        mTextInputEditTextFirstName.setText(null);
        mTextInputEditTextSecondName.setText(null);
    }

}
