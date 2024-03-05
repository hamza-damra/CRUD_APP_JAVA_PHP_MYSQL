package com.example.crudapp.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.Toast;
import com.example.crudapp.MainActivity;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SaveUserTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final MainActivity mainActivity;
    private final OkHttpClient client = new OkHttpClient();

    public SaveUserTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        RequestBody formBody = new FormBody.Builder()
                .add("name", params[0])
                .add("email", params[1])
                .add("birthdate", params[2])
                .add("salary", params[3])
                .build();

        Request request = new Request.Builder()
                .url("https://hamzadamra.000webhostapp.com/save_user.php")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return "User Saved Successfully";
            } else {
                return "Failed to Save User";
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(mainActivity, result, Toast.LENGTH_SHORT).show();
        mainActivity.fetchUsers();
    }
}
