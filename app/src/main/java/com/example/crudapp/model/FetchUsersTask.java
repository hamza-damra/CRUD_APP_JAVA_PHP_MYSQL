package com.example.crudapp.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import com.example.crudapp.MainActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String sortOption;

    public FetchUsersTask(MainActivity activity, String sortOption) {
        mActivity = new WeakReference<>(activity);
        this.sortOption = sortOption;
    }

    @Override
    protected List<User> doInBackground(Void... voids) {
        List<User> users = new ArrayList<>();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://hamzadamra.000webhostapp.com/fetch_user.php?sortOption=" + sortOption);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                String birthdate = jsonObject.getString("birthdate");
                String salary = jsonObject.getString("salary");

                users.add(new User(id, name, email, birthdate, salary));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
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
        if (activity != null && !userList.isEmpty()) {
            activity.refreshUsers(userList);
        }
        if (activity != null) {
            activity.swipeRefreshLayout.setRefreshing(false);
        }
    }
}
