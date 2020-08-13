package com.szubov.android_hw_132;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
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

import static android.content.Context.MODE_PRIVATE;

public class UserRegistrationData {

    public static final String TAG = "my app";
    private String loginPasswordFileName;

    public UserRegistrationData(String loginPasswordFileName) {
        this.loginPasswordFileName = loginPasswordFileName;
    }

    public boolean saveValuesToInternalStorage(String login, String password, Context context) {
        Log.d(TAG, "MainActivity -> saveValuesToInternalStorage");
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(loginPasswordFileName,
                    MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bw = new BufferedWriter(outputStreamWriter);
            bw.append(login).append("/").append(password);
            bw.close();
            return true;
        } catch (IOException ex){
            Log.e(TAG, "MainActivity -> saveValuesToInternalStorage", ex);
            ex.getStackTrace();
        }
        return false;
    }

    public boolean saveValuesToExternalStorage(String login, String password, Context context) {
        Log.d(TAG, "MainActivity -> saveValuesToExternalStorage");
        if (isExternalStorageMounted()) {
            if (login != null && password != null) {
                try {
                    File file = new File(context.getExternalFilesDir(null),
                            loginPasswordFileName);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
                    bw.append(login).append("/").append(password);
                    bw.close();
                    return true;
                } catch (IOException ex){
                    Log.e(TAG, "MainActivity -> saveValuesToExternalStorage", ex);
                    ex.getStackTrace();
                }
            }
        }
        return false;
    }

    public String loadValuesFromInternalStorage(Context context) {
        Log.d(TAG, "MainActivity -> loadValuesFromInternalStorage");
        String values = "";
        try {
            FileInputStream fileInputStream = context.openFileInput(loginPasswordFileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            values = br.readLine();
            br.close();
        } catch (IOException ex) {
            Log.e(TAG, "MainActivity -> loadValuesFromInternalStorage", ex);
            ex.getStackTrace();
        }
        return values;
    }

    public String loadValuesFromExternalStorage(Context context) {
        Log.d(TAG, "MainActivity -> loadValuesFromExternalStorage");
        File file = new File(context.getExternalFilesDir(null), loginPasswordFileName);
        String values = "";
        if (isExternalStorageMounted()) {
            if (file.exists()) {
                try {
                    if (file.length() > 0) {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        values = br.readLine();
                        br.close();
                    } else {
                        Log.d(TAG, "File is empty");
                    }
                } catch (IOException ex) {
                    Log.e(TAG, "MainActivity -> loadValuesFromExternalStorage", ex);
                    ex.getStackTrace();
                }
            }
        }
        return values;
    }

    private boolean isExternalStorageMounted() {
        Log.d(TAG, "MainActivity -> isExternalStorageMounted");
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }
}
