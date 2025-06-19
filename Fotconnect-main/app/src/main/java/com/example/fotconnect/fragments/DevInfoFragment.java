package com.example.fotconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fotconnect.R;

public class DevInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dev_info, container, false);

        // Hide searchBarSports
        if (getActivity() != null) {
            View searchBar = getActivity().findViewById(R.id.searchBarSports);
            if (searchBar != null) {
                searchBar.setVisibility(View.GONE);
            }

            // Hide imageButton4 (user profile button)
            ImageButton userButton = getActivity().findViewById(R.id.imageButton4);
            if (userButton != null) {
                userButton.setVisibility(View.GONE);
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Restore visibility of both views when leaving DevInfoFragment
        if (getActivity() != null) {
            View searchBar = getActivity().findViewById(R.id.searchBarSports);
            if (searchBar != null) {
                searchBar.setVisibility(View.VISIBLE);
            }

            ImageButton userButton = getActivity().findViewById(R.id.imageButton4);
            if (userButton != null) {
                userButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
