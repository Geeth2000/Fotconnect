package com.example.fotconnect.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Added Toast for user feedback

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fotconnect.R;
import com.example.fotconnect.adapters.ArticleAdapter;
import com.example.fotconnect.models.Article;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AcademicFragment extends Fragment implements ArticleAdapter.OnReadMoreClickListener {

    private RecyclerView academicRecyclerView;
    private ArticleAdapter articleAdapter;
    private List<Article> academicArticleList;
    private DatabaseReference academicNewsRef;

    private static final String TAG = "AcademicFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic, container, false);

        academicRecyclerView = view.findViewById(R.id.recyclerViewAcademic);
        academicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        academicArticleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(academicArticleList, this);
        academicRecyclerView.setAdapter(articleAdapter);

        academicNewsRef = FirebaseDatabase.getInstance().getReference("news").child("academic");

        fetchAcademicNews();

        return view;
    }

    private void fetchAcademicNews() {
        academicNewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                academicArticleList.clear();
                for (DataSnapshot newsItemSnapshot : dataSnapshot.getChildren()) {
                    Article article = newsItemSnapshot.getValue(Article.class);
                    if (article != null) {
                        academicArticleList.add(article);
                    }
                }

                Collections.sort(academicArticleList, new Comparator<Article>() {
                    @Override
                    public int compare(Article o1, Article o2) {
                        return Long.compare(o2.getTimestamp(), o1.getTimestamp());
                    }
                });

                articleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load academic news: " + databaseError.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load academic news: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onReadMoreClick(Article article) {
        if (getActivity() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy", Locale.getDefault());
            String dateString = sdf.format(new Date(article.getTimestamp()));

            ArticleDetailFragment detailFragment = ArticleDetailFragment.newInstance(
                    article.getTitle(),
                    "Date Published : " + dateString,
                    article.getContent(),
                    article.getImageUrl()
            );

            // Using R.id.fragmentContainer, ensure this ID matches your main activity's layout
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}