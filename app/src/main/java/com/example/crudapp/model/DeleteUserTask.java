package com.example.crudapp.model;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.example.crudapp.MainActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class DeleteUserTask extends AsyncTask<User, Void, Boolean> {

    private final Context context;
    private final OkHttpClient client = new OkHttpClient();

    public DeleteUserTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(User... users) {
        User user = users[0];
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user.getId()))
                .build();

        Request request = new Request.Builder()
                .url("https://hamzadamra.000webhostapp.com/delete_user.php")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Check if the request was successful
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
            if (context instanceof MainActivity) {
                ((MainActivity) context).fetchUsers(); // Refresh the user list after deletion
            }
        } else {
            Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
        }
    }
}
