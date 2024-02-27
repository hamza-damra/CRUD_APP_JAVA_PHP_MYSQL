package com.example.crudapp.model;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.crudapp.MainActivity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchUsersTask extends AsyncTask<Void, Void, List<User>> {

    private WeakReference<MainActivity> mActivity;

    public FetchUsersTask(MainActivity activity) {
        mActivity = new WeakReference<>(activity);
    }



    @Override
    protected List<User> doInBackground(Void... voids) {
        List<User> users = new ArrayList<>();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("http://192.168.0.58/crudapp/fetch_user.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            String result = response.toString();


            String[] rows = result.split("<br>");
            for (String row : rows) {
                String[] columns = row.split(",");
                if (columns.length == 3) {
                    int id = Integer.parseInt(columns[0]);
                    String name = columns[1];
                    String email = columns[2];
                    users.add(new User(id, name, email));

                }

            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle error gracefully
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        return users;
    }


    @Override
    protected void onPostExecute(List<User> userList) {
        super.onPostExecute(userList);
        MainActivity activity = mActivity.get();
        if (activity != null) {
            MainActivity.progressBar.setVisibility(View.GONE);
            activity.refreshUsers(userList);
        }
    }
}
