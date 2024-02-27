package com.example.crudapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@SuppressLint("StaticFieldLeak")
public class UpdateUserTask extends AsyncTask<User, Void, String> {

    private final Context context;

    public UpdateUserTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(User... users) {
        User user = users[0];

        try {
            // Setup HttpURLConnection to send a POST request
            URL url = new URL("http://192.168.0.58/crudapp/update_user.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Write POST data to request body
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(user.getId()), "UTF-8") + "&" +
                    URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(user.getName(), "UTF-8") + "&" +
                    URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(user.getEmail(), "UTF-8");

            writer.write(data);
            writer.flush();
            writer.close();
            os.close();

            // Check response code to determine the result of the operation
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return "User Updated Successfully";
            } else {
                String errorMsg = "Failed to Update User. Response code: " + responseCode;
                Log.e("UpdateUserTask", errorMsg);
                return errorMsg;
            }
        } catch (IOException e) {
            String errorMsg = "Error: " + e.getMessage();
            Log.e("UpdateUserTask", errorMsg, e);
            return errorMsg;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}
