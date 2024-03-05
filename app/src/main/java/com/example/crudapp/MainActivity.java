package com.example.crudapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.text.SimpleDateFormat;
import android.icu.util.LocaleData;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.crudapp.model.DeleteAllUsersTask;
import com.example.crudapp.model.DeleteUserTask;
import com.example.crudapp.model.SaveUserTask;
import com.example.crudapp.model.UpdateUserTask;
import com.example.crudapp.model.User;
import com.example.crudapp.model.UsersAdapter;
import com.example.crudapp.model.FetchUsersTask;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText editTextName, editTextEmail, editTextDate, editTextSalary;
    ConstraintLayout constraintLayout;
    public SwipeRefreshLayout swipeRefreshLayout;
    private UsersAdapter adapter;
    Handler handler = new Handler();

    Runnable runnable;

    final static int ACTIVE_POLLING_INTERVAL = 10000;
    private Spinner spinner;
    private final List<User> usersList = new ArrayList<>();
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.mainLayout);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextDate = findViewById(R.id.editTextDate);
        editTextSalary = findViewById(R.id.editTextSalary);
        spinner = findViewById(R.id.spinner);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonDelete = findViewById(R.id.buttonDelete);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        if(isDarkTheme(this)) {
            editTextName.setTextColor(getResources().getColor(R.color.white));
            editTextEmail.setTextColor(getResources().getColor(R.color.white));
            editTextDate.setTextColor(getResources().getColor(R.color.white));
            editTextSalary.setTextColor(getResources().getColor(R.color.white));
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                //check if the app is active or not and call fetchUsers() accordingly to refresh the users list
                if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    fetchUsers();
                }
                handler.postDelayed(this, ACTIVE_POLLING_INTERVAL);
            }
        };


        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.dropdown_items, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            @SuppressLint("StaticFieldLeak")
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                swipeRefreshLayout.setRefreshing(true);

                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... positions) {
                        fetchUsersBasedOnSelection(positions[0]);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (usersList.isEmpty()) {
                            Toast.makeText(MainActivity.this, "No users to display", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DatePickerDialog datePickerDialog = getDatePickerDialog();
        editTextDate.setOnTouchListener((v, event) -> {
            datePickerDialog.show();
            return false;
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchUsers();
            spinner.setSelection(0);
        });

        setupRecyclerView(recyclerView);

        buttonSave.setOnClickListener(view -> saveUser());

        buttonDelete.setOnClickListener(view -> {
            if(!usersList.isEmpty()) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete all users")
                        .setMessage("Are you sure you want to delete all users?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> new DeleteAllUsersTask(MainActivity.this).execute())
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                Toast.makeText(MainActivity.this, "No users to delete", Toast.LENGTH_SHORT).show();
            }
        });

        fetchUsers();
    }

    private void fetchUsersBasedOnSelection(int position) {
        String sortOption = "";
        switch (position) {
            case 0: // Ascending
                sortOption = "asc";
                break;
            case 1: // Descending
                sortOption = "desc";
                break;
            case 2: // Newest
                sortOption = "newest";
                break;
            case 3: // Oldest
                sortOption = "oldest";
                break;
        }
        // Assuming you have a method that fetches the users with a sort option
        new FetchUsersTask(MainActivity.this, sortOption).execute();
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
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
    }

    @NonNull
    private DatePickerDialog getDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            editTextDate.setText(selectedDate);
        }, year, month, day);
    }

    private void saveUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String birthdate = editTextDate.getText().toString().trim();
        String salary = editTextSalary.getText().toString().trim();

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        if (!name.isEmpty() && !email.isEmpty() && matcher.matches() && !birthdate.isEmpty() && !salary.isEmpty()) {
            SaveUserTask saveUserTask = new SaveUserTask(MainActivity.this);
            saveUserTask.execute(name, email, birthdate, salary);
            editTextName.setText("");
            editTextEmail.setText("");
            editTextDate.setText("");
            editTextSalary.setText("");
        } else if (email.isEmpty() || !matcher.matches()) {
            Toast.makeText(MainActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void showEditUserDialog(User user) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);

        final EditText editTextUserName = dialogView.findViewById(R.id.editTextUserName);
        final EditText editTextUserEmail = dialogView.findViewById(R.id.editTextUserEmail);
        final EditText editTextUserBirthdate = dialogView.findViewById(R.id.editTextUserBirthdate);
        final EditText editTextUserSalary = dialogView.findViewById(R.id.editTextUserSalary);

        editTextUserName.setText(user.getName());
        editTextUserEmail.setText(user.getEmail());
        editTextUserBirthdate.setText(user.getBirthdate().toString());
        editTextUserSalary.setText(user.getSalary());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Edit User")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = editTextUserName.getText().toString().trim();
                    String newEmail = editTextUserEmail.getText().toString().trim();
                    String newBirthdate = editTextUserBirthdate.getText().toString().trim();
                    String newSalary = editTextUserSalary.getText().toString().trim();

                    user.setName(newName);
                    user.setEmail(newEmail);
                    user.setBirthdate(newBirthdate);
                    user.setSalary(newSalary);

                    new UpdateUserTask(MainActivity.this).execute(user);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

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
        swipeRefreshLayout.setRefreshing(false);
    }

    public void fetchUsers() {
        swipeRefreshLayout.setRefreshing(true);
        new FetchUsersTask(this,"newest").execute();
    }
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    public static boolean isDarkTheme(Context context) {
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (mode == Configuration.UI_MODE_NIGHT_YES) {
            Log.d("ThemeUtils", "Dark Theme Detected");
            return true;
        } else {
            Log.d("ThemeUtils", "Light Theme Detected");
            return false;
        }
    }

}
