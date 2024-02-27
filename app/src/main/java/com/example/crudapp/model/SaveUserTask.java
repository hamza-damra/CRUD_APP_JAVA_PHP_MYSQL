package com.example.crudapp.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.example.crudapp.MainActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SaveUserTask extends AsyncTask<String, Void, String> {

    private final MainActivity mainActivity;

    public SaveUserTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL("http://192.168.0.58/crudapp/create.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                    URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

            writer.write(data);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
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
        MainActivity.progressBar.setVisibility(View.GONE);
        Toast.makeText(mainActivity, result, Toast.LENGTH_SHORT).show();
        mainActivity.fetchUsers();
    }
}
