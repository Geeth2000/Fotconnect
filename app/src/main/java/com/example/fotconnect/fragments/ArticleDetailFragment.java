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

import com.bumptech.glide.Glide;
import com.example.fotconnect.R;

public class ArticleDetailFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DATE = "date";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_IMAGE_URL = "imageUrl";

    public static ArticleDetailFragment newInstance(String title, String date, String content, String imageUrl) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DATE, date);
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView detailTitle, detailDate, detailContent;
    private ImageView detailImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);

        detailTitle = view.findViewById(R.id.detailTitle);
        detailDate = view.findViewById(R.id.detailDate);
        detailContent = view.findViewById(R.id.detailContent);
        detailImage = view.findViewById(R.id.detailImage);

        Bundle args = getArguments();
        if (args != null) {
            detailTitle.setText(args.getString(ARG_TITLE));
            detailDate.setText(args.getString(ARG_DATE));
            detailContent.setText(args.getString(ARG_CONTENT));

            String imageUrl = args.getString(ARG_IMAGE_URL);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.error)
                        .error(R.drawable.error)
                        .into(detailImage);
            } else {
                detailImage.setImageResource(R.drawable.error);
            }
        }

        return view;
    }
}