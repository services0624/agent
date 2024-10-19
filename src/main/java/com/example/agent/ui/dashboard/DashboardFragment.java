package com.example.agent.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.agent.R;
import com.example.agent.User;
import com.example.agent.databinding.FragmentDashboardBinding;
import com.example.agent.ui.home.HomeViewModel;

public class DashboardFragment extends Fragment {

    private HomeViewModel viewModel;
    String name,company,price;
    private TextView displayTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        displayTextView = view.findViewById(R.id.text_dashboard);

        // Load user data
        loadUserData();

        return view;
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userData = sharedPreferences.getString("userData", "No user data found");
        displayTextView.setText(userData);
    }
}
