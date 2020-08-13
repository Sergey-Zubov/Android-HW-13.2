package com.szubov.android_hw_132;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextLogin;
    private EditText mEditTextPassword;
    private CheckBox mCheckBoxStorage;
    private SharedPreferences mCheckboxStatusSharedPref;
    public static final String SELECTED_STORAGE = "Selected storage";
    public static final String TAG = "my app";
    public static final UserRegistrationData USER_REG_DATA =
            new UserRegistrationData("UserRegistrationData.txt");;

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
                        if (USER_REG_DATA.saveValuesToExternalStorage(stringLogin, stringPassword,
                                MainActivity.this)) {
                            Toast.makeText(MainActivity.this, R.string.user_registered,
                                    Toast.LENGTH_LONG).show();
                            mEditTextLogin.setText("");
                            mEditTextPassword.setText("");
                        }
                    } else {
                        if (USER_REG_DATA.saveValuesToInternalStorage(stringLogin, stringPassword,
                                MainActivity.this)) {
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
                Log.d(TAG, "MainActivity -> checkbox -> onCheckedChanged");
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
        Log.d(TAG, "MainActivity -> checkbox -> onCheckedChanged -> " +
                "valuesFromExternalToInternalStorage");
        String string = USER_REG_DATA.loadValuesFromExternalStorage(MainActivity.this);
        if (string.length() > 0) {
            String[] values = string.split("/");
            if (values.length == 2) {
                USER_REG_DATA.saveValuesToInternalStorage(values[0], values[1],
                        MainActivity.this);
            } else {
                Log.e(TAG, "MainActivity -> checkbox -> onCheckedChanged -> " +
                        "valuesFromExternalToInternalStorage");
            }
        }
    }

    private void valuesFromInternalToExternalStorage() {
        Log.d(TAG, "MainActivity -> checkbox -> onCheckedChanged -> " +
                "valuesFromInternalToExternalStorage");
        String string = USER_REG_DATA.loadValuesFromInternalStorage(MainActivity.this);
        if (string.length() > 0) {
            String[] values = string.split("/");
            if (values.length == 2) {
                USER_REG_DATA.saveValuesToExternalStorage(values[0], values[1],
                        MainActivity.this);
            } else {
                Log.e(TAG, "MainActivity -> checkbox -> onCheckedChanged -> " +
                        "valuesFromInternalToExternalStorage");
            }
        }
    }

    private void getDataFromSharedPref() {
        Log.d(TAG, "MainActivity -> getDataFromSharedPref");
        if (mCheckboxStatusSharedPref != null &&
                mCheckboxStatusSharedPref.getString(SELECTED_STORAGE, "").length() > 0) {
            String string = mCheckboxStatusSharedPref.getString(SELECTED_STORAGE, "");
            mCheckBoxStorage.setChecked(Boolean.parseBoolean(string));
        }
    }

    private boolean compareLoginAndPassword(String login, String password) {
        Log.d(TAG, "MainActivity -> BtnOk -> OnClick -> compareLoginAndPassword");
        if (login != null && password != null) {
            String[] values;
            String string;
            if (mCheckBoxStorage.isChecked()) {
                string = USER_REG_DATA.loadValuesFromExternalStorage(MainActivity.this);
            } else {
                string = USER_REG_DATA.loadValuesFromInternalStorage(MainActivity.this);
            }
            if (string.length() > 0) {
                values = string.split("/");
                if (values.length == 2) {
                    String storedLogin = values[0];
                    String storedPassword = values[1];
                    if (storedLogin.length() > 0 && storedPassword.length() > 0) {
                        return storedLogin.equals(login) && storedPassword.equals(password);
                    }
                } else {
                    Log.e(TAG, "MainActivity -> BtnOk -> OnClick -> " +
                            "compareLoginAndPassword -> " +
                            "values.length != 2");
                }
            } else {
                Log.e(TAG, "MainActivity -> BtnOk -> OnClick -> " +
                        "compareLoginAndPassword -> " +
                        "string.length() <= 0");
            }
        }
        return false;
    }
}