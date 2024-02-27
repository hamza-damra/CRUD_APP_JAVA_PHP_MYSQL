package com.example.crudapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapp.model.DeleteAllUsersTask;
import com.example.crudapp.model.DeleteUserTask;
import com.example.crudapp.model.SaveUserTask;
import com.example.crudapp.model.UpdateUserTask;
import com.example.crudapp.model.User;
import com.example.crudapp.model.UsersAdapter;
import com.example.crudapp.model.FetchUsersTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;
    private UsersAdapter adapter;
    Button buttonSave, buttonDelete;
    private final List<User> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        progressBar = findViewById(R.id.progressBar);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);

        if(UsersAdapter.ViewHolder.isDarkTheme(this))
        {
             constraintLayout.setBackgroundColor(getResources().getColor(R.color.dark_grey));
             editTextEmail.setTextColor(getResources().getColor(R.color.white));
                editTextName.setTextColor(getResources().getColor(R.color.white));
                buttonSave.setBackgroundColor(getResources().getColor(R.color.buttonSaveBackgroundColorForDarkTheme));
                buttonDelete.setBackgroundColor(getResources().getColor(R.color.buttonDeleteBackgroundColorForDarkTheme));
        }
        else
        {
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.white));
            editTextEmail.setTextColor(getResources().getColor(R.color.black));
            editTextName.setTextColor(getResources().getColor(R.color.black));
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(usersList, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Toast.makeText(MainActivity.this, user.getName() + " clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(User user) {
                showEditUserDialog(user);
            }

            @Override
            public void onDeleteClick(User user) {
                deleteUser(user);
            }
        });
        recyclerView.setAdapter(adapter);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();

                if (!name.isEmpty() && !email.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    new SaveUserTask(MainActivity.this).execute(name, email);
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }

                editTextEmail.setText("");
                editTextName.setText("");
            }

        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteAllUsersTask(MainActivity.this).execute();
            }
        });
        fetchUsers();
    }

    private void showEditUserDialog(User user) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);

        EditText editTextUserName = dialogView.findViewById(R.id.editTextUserName);
        EditText editTextUserEmail = dialogView.findViewById(R.id.editTextUserEmail);
        editTextUserName.setText(user.getName());
        editTextUserEmail.setText(user.getEmail());

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView)
                .setTitle("Edit User")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = editTextUserName.getText().toString().trim();
                        String newEmail = editTextUserEmail.getText().toString().trim();

                        user.setName(newName);
                        user.setEmail(newEmail);
                        adapter.notifyDataSetChanged();

                        new UpdateUserTask(MainActivity.this).execute(user);
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteUser(User user) {
        usersList.remove(user);
        adapter.notifyDataSetChanged();
        new DeleteUserTask(MainActivity.this).execute(user);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshUsers(List<User> userList) {
        usersList.clear();
        usersList.addAll(userList);
        adapter.notifyDataSetChanged();
    }

    public void fetchUsers() {
        new FetchUsersTask(this).execute();
    }
}
