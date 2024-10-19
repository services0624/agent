package com.example.agent.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agent.User;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<User>> userDataList = new MutableLiveData<>(new ArrayList<>());

    public void addUserData(User user) {
        List<User> currentList = userDataList.getValue();
        if (currentList != null) {
            currentList.add(user);
            userDataList.setValue(currentList);
        }
    }

    public LiveData<List<User>> getUserDataList() {
        return userDataList;
    }
}
