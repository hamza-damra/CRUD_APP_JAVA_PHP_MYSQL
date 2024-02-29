package com.example.crudapp.model;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.crudapp.MainActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteUserTask extends AsyncTask<User, Void, Boolean> {

    private final Context context;

    public DeleteUserTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(User... users) {
        User user = users[0];
        boolean success = false;
        HttpURLConnection connection = null;

        try {
            URL url = new URL("https://hamzadamra.000webhostapp.com/delete_user.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true); // Enable output for POST request

            // Prepare data to be sent in the request body
            String postData = "user_id=" + user.getId();

            // Write data to the output stream
            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());
            os.flush();

            // Get response code
            int responseCode = connection.getResponseCode();
            success = responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return success;
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
