package com.example.agent.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.agent.R;
import com.example.agent.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private HomeViewModel viewModel;

    private EditText username, usercompany, userprice;
    private Button submit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        username = view.findViewById(R.id.name);
        usercompany = view.findViewById(R.id.company);
        userprice = view.findViewById(R.id.price);
        submit = view.findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            String name = username.getText().toString().trim();
            String company = usercompany.getText().toString().trim();
            String priceText = userprice.getText().toString().trim();

            if (name.isEmpty() || company.isEmpty() || priceText.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int price;
            try {
                price = Integer.parseInt(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            String userEmail = mAuth.getCurrentUser().getEmail();
            if (userEmail == null) {
                Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = userEmail.replace(".", ",");
            User user = new User(name, company, price);
            String entryId = databaseReference.child(userId).push().getKey();

            if (entryId != null) {
                databaseReference.child(userId).child(entryId).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "User data saved", Toast.LENGTH_SHORT).show();
                        viewModel.addUserData(user); // Add user to the ViewModel
                        onUserDataUpdated(user); // Save to SharedPreferences
                    } else {
                        Toast.makeText(getActivity(), "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Failed to generate entry ID", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void saveUserData(String name, String company, String price) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userData = "Name: " + name + "\nCompany: " + company + "\nPrice: " + price;
        editor.putString("userData", userData);
        editor.apply();
    }


    private void onUserDataUpdated(User user) {
        String name = user.getName();
        String company = user.getCompany();
        String price = String.valueOf(user.getPrice()); // Convert int to String
        saveUserData(name, company, price);
        // Additional logic after saving user data
    }
}
