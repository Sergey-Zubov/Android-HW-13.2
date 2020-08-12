package com.szubov.android_hw_132;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextLogin;
    private EditText mEditTextPassword;
    private CheckBox mCheckBoxStorage;
    private SharedPreferences mCheckboxStatusSharedPref;
    public static final String SELECTED_STORAGE = "Selected storage";
    public static final String TAG = "my app";
    public static final String LOGIN_PASSWORD_FILE_NAME = "UserRegistrationData.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getDataFromSharedPref();

    }

    private void initViews() {
        mEditTextLogin = findViewById(R.id.editTextLogin);
        mEditTextPassword = findViewById(R.id.ediTextPassword);

        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MainActivity -> BtnOk -> OnClick");
                String stringLogin = mEditTextLogin.getText().toString();
                String stringPassword = mEditTextPassword.getText().toString();
                if (stringLogin.length() > 0 && stringPassword.length() > 0) {
                    if (compareLoginAndPassword(stringLogin, stringPassword)) {
                        Toast.makeText(MainActivity.this, R.string.log_pass_available,
                                Toast.LENGTH_LONG).show();
                        mEditTextLogin.setText("");
                        mEditTextPassword.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, R.string.log_pass_not_available,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.enter_login_and_password,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.btnRegistration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MainActivity -> BtnRegistration -> OnClick");
                String stringLogin = mEditTextLogin.getText().toString();
                String stringPassword = mEditTextPassword.getText().toString();
                if(stringLogin.length() > 0 && stringPassword.length() > 0) {
                    if (mCheckBoxStorage.isChecked()) {
                        if (saveValuesToExternalStorage(stringLogin, stringPassword)) {
                            Toast.makeText(MainActivity.this, R.string.user_registered,
                                    Toast.LENGTH_LONG).show();
                            mEditTextLogin.setText("");
                            mEditTextPassword.setText("");
                        }
                    } else {
                        if (saveValuesToInternalStorage(stringLogin, stringPassword)) {
                            Toast.makeText(MainActivity.this, R.string.user_registered,
                                    Toast.LENGTH_LONG).show();
                            mEditTextLogin.setText("");
                            mEditTextPassword.setText("");
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.login_or_password_is_empty,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mCheckboxStatusSharedPref = getSharedPreferences("StorageTypeSelected", MODE_PRIVATE);
        mCheckBoxStorage = findViewById(R.id.checkBoxStorage);

        mCheckBoxStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mCheckboxStatusSharedPref.edit();
                String checkBoxStatus = String.valueOf(isChecked);
                editor.putString(SELECTED_STORAGE, checkBoxStatus);
                editor.apply();
                if (isChecked) {
                    valuesFromInternalToExternalStorage();
                } else {
                    valuesFromExternalToInternalStorage();
                }
            }
        });
    }

    private void valuesFromExternalToInternalStorage() {
        String[] values = loadValuesFromExternalStorage().split(",", 2);
        if (values.length == 2) {
            saveValuesToInternalStorage(values[0], values[1]);
        } else {
            Log.e(TAG, "MainActivity -> BtnOk -> OnClick -> compareLoginAndPassword -> " +
                    "values.length != 2");
        }
    }

    private void valuesFromInternalToExternalStorage() {
        String[] values = loadValuesFromInternalStorage().split(",", 2);
        if (values.length == 2) {
            saveValuesToExternalStorage(values[0], values[1]);
        } else {
            Log.e(TAG, "MainActivity -> BtnOk -> OnClick -> compareLoginAndPassword -> " +
                    "values.length != 2");
        }
    }

    private void getDataFromSharedPref() {
        if (mCheckboxStatusSharedPref != null && mCheckboxStatusSharedPref.getString(SELECTED_STORAGE, "").length() > 0) {
            String string = mCheckboxStatusSharedPref.getString(SELECTED_STORAGE, "");
            mCheckBoxStorage.setChecked(Boolean.parseBoolean(string));
        }
    }

    private boolean compareLoginAndPassword(String login, String password) {
        Log.d(TAG, "MainActivity -> BtnOk -> OnClick -> compareLoginAndPassword");
        if (login != null && password != null) {
            String[] values;
            if (mCheckBoxStorage.isChecked()) {
                values = loadValuesFromExternalStorage().split(",", 2);
            } else {
                values = loadValuesFromInternalStorage().split(",", 2);
            }
            if (values.length == 2) {
                String storedLogin = values[0];
                String storedPassword = values[1];
                if (storedLogin.length() > 0 && storedPassword.length() > 0) {
                    return storedLogin.equals(login) && storedPassword.equals(password);
                }
            } else {
                Log.e(TAG, "MainActivity -> BtnOk -> OnClick -> compareLoginAndPassword -> " +
                        "values.length != 2");
            }
        }
        return false;
    }

    private boolean saveValuesToInternalStorage(String login, String password) {
        Log.d(TAG, "MainActivity -> BtnOk -> OnClick -> saveLoginAndPassword -> saveValue");
        try {
            FileOutputStream fileOutputStream = openFileOutput(LOGIN_PASSWORD_FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bw = new BufferedWriter(outputStreamWriter);
            bw.append(login).append("\n").append(password);
            bw.close();
            return true;
        } catch (IOException ex){
            Log.e(TAG, "MainActivity -> btnRegistration -> onClick -> " +
                    "saveLoginAndPassword -> saveValue", ex);
            ex.getStackTrace();
        }
        return false;
    }

    private boolean saveValuesToExternalStorage(String login, String password) {
        Log.d(TAG, "MainActivity -> BtnOk -> OnClick -> saveLoginAndPassword -> saveValue");
        if (isExternalStorageMounted()) {
            if (login != null && password != null) {
                try {
                    File file = new File(this.getExternalFilesDir(null), LOGIN_PASSWORD_FILE_NAME);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
                    bw.append(login).append("\n").append(password);
                    bw.close();
                    return true;
                } catch (IOException ex){
                    Log.e(TAG, "MainActivity -> btnRegistration -> onClick -> " +
                            "saveLoginAndPassword -> saveValue", ex);
                    ex.getStackTrace();
                }
            }
        }
        return false;
    }

    private String loadValuesFromInternalStorage() {
        Log.d(TAG, "MainActivity -> BtnOk -> OnClick -> compareLoginAndPassword -> loadValuesFromInternalStorage");
        String values = "";
        try {
            FileInputStream fileInputStream = openFileInput(LOGIN_PASSWORD_FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            while ((values = br.readLine())!= null) {
                values = br.readLine() + ",";
            }
            br.close();
        } catch (IOException ex) {
            Log.e(TAG, "MainActivity -> btnOk -> onClick -> " +
                    "compareLoginAndPassword -> loadValue", ex);
            ex.getStackTrace();
        }
        return values;
    }

    private String loadValuesFromExternalStorage() {
        Log.d(TAG, "MainActivity -> BtnOk -> OnClick -> compareLoginAndPassword -> loadValuesFromExternalStorage");
        File file = new File(this.getExternalFilesDir(null), LOGIN_PASSWORD_FILE_NAME);
        String values = "";
        if (isExternalStorageMounted()) {
            if (file.exists()) {
                try {
                    if (file.length() > 0) {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        while ((values = br.readLine()) != null) {
                            values = br.readLine() + ",";
                        }
                        br.close();
                    } else {
                        Log.d(TAG, "File is empty");
                    }
                } catch (IOException ex) {
                    Log.e(TAG, "MainActivity -> btnOk -> onClick -> " +
                            "compareLoginAndPassword -> loadValue", ex);
                    ex.getStackTrace();
                }
            }
        }
        return values;
    }

    private boolean isExternalStorageMounted() {
        Log.d(TAG, "MainActivity -> ");
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }
}