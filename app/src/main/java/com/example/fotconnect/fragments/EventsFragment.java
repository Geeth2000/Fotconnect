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

public class EventsFragment extends Fragment implements ArticleAdapter.OnReadMoreClickListener {

    private RecyclerView eventsRecyclerView;
    private ArticleAdapter articleAdapter;
    private List<Article> eventArticleList;
    private DatabaseReference eventNewsRef;

    private static final String TAG = "EventsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        eventsRecyclerView = view.findViewById(R.id.recyclerViewEvents);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventArticleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(eventArticleList, this);
        eventsRecyclerView.setAdapter(articleAdapter);

        eventNewsRef = FirebaseDatabase.getInstance().getReference("news").child("event");

        fetchEventNews();

        return view;
    }

    private void fetchEventNews() {
        eventNewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventArticleList.clear();
                for (DataSnapshot newsItemSnapshot : dataSnapshot.getChildren()) {
                    Article article = newsItemSnapshot.getValue(Article.class);
                    if (article != null) {
                        eventArticleList.add(article);
                    }
                }

                Collections.sort(eventArticleList, new Comparator<Article>() {
                    @Override
                    public int compare(Article o1, Article o2) {
                        return Long.compare(o2.getTimestamp(), o1.getTimestamp());
                    }
                });

                articleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load event news: " + databaseError.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load event news: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
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