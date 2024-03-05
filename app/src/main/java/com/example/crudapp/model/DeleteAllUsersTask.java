package com.example.crudapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.example.crudapp.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;

public class DeleteAllUsersTask extends AsyncTask<Void, Void, Boolean> {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final OkHttpClient client = new OkHttpClient();

    public DeleteAllUsersTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        RequestBody requestBody = RequestBody.create("", null); // Empty request body for POST
        Request request = new Request.Builder()
                .url("https://hamzadamra.000webhostapp.com/delete_all_users.php")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseContent = response.body().string();
            // Check if the response indicates success or if there are no users to delete
            return response.isSuccessful() && !responseContent.contains("No users to delete");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            Toast.makeText(context, "All users deleted successfully", Toast.LENGTH_SHORT).show();
            if (context instanceof MainActivity) {
                ((MainActivity) context).refreshUsers(new ArrayList<>());
            }
        } else {
            Toast.makeText(context, "No users to delete or failed to delete users", Toast.LENGTH_SHORT).show();
        }
    }
}
