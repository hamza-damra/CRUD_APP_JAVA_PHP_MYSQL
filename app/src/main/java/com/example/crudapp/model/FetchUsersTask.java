package com.example.crudapp.model;

import android.os.AsyncTask;
import com.example.crudapp.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class FetchUsersTask extends AsyncTask<Void, Void, List<User>> {

    private WeakReference<MainActivity> mActivity;
    private String sortOption;
    private OkHttpClient client = new OkHttpClient();

    public FetchUsersTask(MainActivity activity, String sortOption) {
        mActivity = new WeakReference<>(activity);
        this.sortOption = sortOption;
    }

    @Override
    protected List<User> doInBackground(Void... voids) {
        List<User> users = new ArrayList<>();
        Request request = new Request.Builder()
                .url("https://hamzadamra.000webhostapp.com/fetch_user.php?sortOption=" + sortOption)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseData = response.body().string();
            JSONArray jsonArray = new JSONArray(responseData);
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
