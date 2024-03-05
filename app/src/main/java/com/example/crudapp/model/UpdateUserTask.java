package com.example.crudapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressLint("StaticFieldLeak")
public class UpdateUserTask extends AsyncTask<User, Void, String> {

    private final Context context;
    private final OkHttpClient client = new OkHttpClient();

    public UpdateUserTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(User... users) {
        User user = users[0];

        RequestBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(user.getId()))
                .add("name", user.getName())
                .add("email", user.getEmail())
                .add("birthdate", user.getBirthdate())
                .add("salary", user.getSalary())
                .build();

        Request request = new Request.Builder()
                .url("https://hamzadamra.000webhostapp.com/update_user.php")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Log.d("UpdateUserTask", "User Updated Successfully");
                return "User Updated Successfully";
            } else {
                Log.d("UpdateUserTask", "Failed to Update User. Response code: " + response.code());
                return "Failed to Update User. Response code: " + response.code();
            }
        } catch (IOException e) {
            Log.e("UpdateUserTask", "Error: " + e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}
