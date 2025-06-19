package com.example.fotconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide; // Glide library for loading images from URLs
import com.example.fotconnect.R;

// ArticleDetailFragment displays the full content of a selected article.
public class ArticleDetailFragment extends Fragment {

    // Keys for arguments Bundle to pass article data to this fragment.
    private static final String ARG_TITLE = "title";
    private static final String ARG_DATE = "date";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_IMAGE_URL = "imageUrl"; // Changed to ARG_IMAGE_URL for image URL

    // Factory method to create a new instance of ArticleDetailFragment with bundled arguments.
    // This is the recommended way to pass arguments to a fragment.
    public static ArticleDetailFragment newInstance(String title, String date, String content, String imageUrl) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DATE, date);
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_IMAGE_URL, imageUrl); // Put the image URL in the arguments
        fragment.setArguments(args); // Set the arguments for the fragment
        return fragment;
    }

    private TextView detailTitle, detailDate, detailContent; // TextViews for article details
    private ImageView detailImage; // ImageView for the article image

    @Nullable
    @Override
    // Called to have the fragment instantiate its user interface view.
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment (fragment_article_detail.xml).
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);

        // Initialize TextViews and ImageView by finding their IDs in the inflated view.
        detailTitle = view.findViewById(R.id.detailTitle);
        detailDate = view.findViewById(R.id.detailDate);
        detailContent = view.findViewById(R.id.detailContent);
        detailImage = view.findViewById(R.id.detailImage);

        // Retrieve arguments passed to this fragment.
        Bundle args = getArguments();
        if (args != null) {
            // Set the text for title, date, and content from the arguments.
            detailTitle.setText(args.getString(ARG_TITLE));
            detailDate.setText(args.getString(ARG_DATE));
            detailContent.setText(args.getString(ARG_CONTENT));

            // Get the image URL from arguments.
            String imageUrl = args.getString(ARG_IMAGE_URL);
            // Load the image using Glide if a valid URL is provided.
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.error) // Placeholder while loading
                        .error(R.drawable.error) // Image to show if loading fails
                        .into(detailImage);
            } else {
                // If no image URL is provided, set a default image.
                detailImage.setImageResource(R.drawable.error);
            }
        }

        return view;
    }
}
