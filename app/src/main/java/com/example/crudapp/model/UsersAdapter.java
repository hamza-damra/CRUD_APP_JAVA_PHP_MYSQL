package com.example.crudapp.model;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapp.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private static List<User> users = null;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User user);
        void onEditClick(User user);
        void onDeleteClick(User user);
    }

    public UsersAdapter(List<User> users, OnItemClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.textViewName.setText(user.getName());
        holder.textViewEmail.setText(user.getEmail());
        holder.textViewBirthdate.setText(user.getBirthdate().toString()); // Set birthdate
        holder.textViewSalary.setText(user.getSalary());

        if (ViewHolder.isDarkTheme(holder.textViewName.getContext())) {
            holder.textViewName.setTextColor(holder.textViewName.getResources().getColor(R.color.white));
            holder.textViewEmail.setTextColor(holder.textViewEmail.getResources().getColor(R.color.white));
            holder.textViewName.setHintTextColor(holder.textViewName.getResources().getColor(R.color.white));
            holder.textViewEmail.setHintTextColor(holder.textViewEmail.getResources().getColor(R.color.white));
            holder.textViewBirthdate.setTextColor(holder.textViewBirthdate.getResources().getColor(R.color.white)); // Set birthdate
            holder.textViewSalary.setTextColor(holder.textViewSalary.getResources().getColor(R.color.white));

        } else {
            holder.textViewName.setTextColor(holder.textViewName.getResources().getColor(R.color.black));
            holder.textViewEmail.setTextColor(holder.textViewEmail.getResources().getColor(R.color.black));
            holder.textViewName.setHintTextColor(holder.textViewName.getResources().getColor(R.color.black));
            holder.textViewEmail.setHintTextColor(holder.textViewEmail.getResources().getColor(R.color.black));
            holder.textViewBirthdate.setTextColor(holder.textViewBirthdate.getResources().getColor(R.color.black)); // Set birthdate
            holder.textViewSalary.setTextColor(holder.textViewSalary.getResources().getColor(R.color.black));
        }
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewEmail, textViewBirthdate, textViewSalary;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewBirthdate = itemView.findViewById(R.id.textViewBirthdate); // Added
            textViewSalary = itemView.findViewById(R.id.textViewSalary);
            // Item click listener
            itemView.setOnClickListener(v -> listener.onItemClick(getUserAtPosition(getAdapterPosition())));

            // Long click listener for custom animation and showing PopupMenu
            itemView.setOnLongClickListener(view -> {
                // Perform haptic feedback
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                // Load and start fade out animation
                Animation fadeOutAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
                view.startAnimation(fadeOutAnimation);

                // Show PopupMenu after animation ends
                view.postDelayed(() -> {
                    PopupMenu popup = new PopupMenu(view.getContext(), view);
                    popup.getMenuInflater().inflate(R.menu.context_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(item -> {
                        if(item.getItemId() == R.id.edit) {
                            listener.onEditClick(getUserAtPosition(getAdapterPosition()));
                            return true;
                        }
                        else if(item.getItemId() == R.id.delete) {
                            listener.onDeleteClick(getUserAtPosition(getAdapterPosition()));
                            return true;
                        }
                        else {
                            return false;
                        }
                    });
                    popup.show();
                }, 200); // Delay to ensure animation is visible

                return true;
            });
        }




        private User getUserAtPosition(int position) {
            if (position != RecyclerView.NO_POSITION && position < users.size()) {
                return users.get(position);
            }
            return null;
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




}
