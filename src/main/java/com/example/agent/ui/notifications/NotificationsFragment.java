package com.example.agent.ui.notifications;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.agent.R;
import com.example.agent.databinding.FragmentNotificationsBinding;
import com.example.agent.login;

import com.google.firebase.auth.FirebaseAuth;

//import appBarBinding.Toolbar;

public class NotificationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        Button logoutButton = view.findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    public void logout() {
        // Clear user data
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Or you can selectively remove user data
        editor.apply();

        // Optionally, sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Navigate to the login screen
        Intent intent = new Intent(getActivity(), login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
        startActivity(intent);
        requireActivity().finish(); // Close the current activity
    }

}
