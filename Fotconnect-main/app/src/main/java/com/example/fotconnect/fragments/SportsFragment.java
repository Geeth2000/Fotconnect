package com.example.fotconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Used for showing short messages to the user

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fotconnect.R;
import com.example.fotconnect.adapters.ArticleAdapter;
import com.example.fotconnect.models.Article;
import com.google.firebase.database.DataSnapshot; // Firebase data snapshot
import com.google.firebase.database.DatabaseError; // Firebase database error
import com.google.firebase.database.DatabaseReference; // Firebase database reference
import com.google.firebase.database.FirebaseDatabase; // Firebase database instance
import com.google.firebase.database.ValueEventListener; // Listener for data changes
import com.google.firebase.storage.FirebaseStorage; // Firebase Storage instance
import com.google.firebase.storage.StorageReference; // Firebase Storage reference

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections; // For sorting the list
import java.util.Comparator; // For custom sorting
import java.util.Date; // For date formatting
import java.util.List;
import java.util.Locale; // For locale in date formatting

// SportsFragment displays a list of sports news articles in a RecyclerView.
// It implements ArticleAdapter.OnReadMoreClickListener to handle clicks on article cards.
public class SportsFragment extends Fragment implements ArticleAdapter.OnReadMoreClickListener {

    private RecyclerView recyclerView; // RecyclerView to display the list of articles
    private ArticleAdapter adapter; // Adapter for the RecyclerView
    private List<Article> articleList; // List to hold Article objects

    // Firebase Database Reference to the "sport" news section.
    private DatabaseReference newsRef;
    // Firebase Storage instance
    private FirebaseStorage storage;


    @Override
    // Called to have the fragment instantiate its user interface view.
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment (fragment_sports.xml).
        View view = inflater.inflate(R.layout.fragment_sports, container, false);

        // Initialize RecyclerView and set its layout manager.
        recyclerView = view.findViewById(R.id.recyclerViewSports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the article list and adapter.
        articleList = new ArrayList<>();
        adapter = new ArticleAdapter(articleList, this);
        recyclerView.setAdapter(adapter);

        // Get an instance of the Firebase Realtime Database and create a reference
        // to the "sport" child node under the "news" parent node.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        newsRef = database.getReference("news").child("sport");

        // Get an instance of Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Call the method to fetch sports news from Firebase.
        fetchSportsNews();

        return view;
    }

    // Fetches sports news data from Firebase Realtime Database.
    private void fetchSportsNews() {
        // Add a ValueEventListener to newsRef. This listener will be triggered
        // whenever the data at newsRef changes, or once initially.
        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            // This method is called with a snapshot of the data at this location.
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articleList.clear(); // Clear existing articles to avoid duplicates on data change.

                // Assuming "sport" node directly contains a single news item as per your JSON,
                // we directly map it to an Article object.
                final Article sportNews = dataSnapshot.getValue(Article.class);

                if (sportNews != null) {
                    // Check if the imageUrl is a gs:// URL (Firebase Storage URI)
                    if (sportNews.getImageUrl() != null && sportNews.getImageUrl().startsWith("gs://")) {
                        // Get a reference to the image in Firebase Storage using the gs:// URL
                        StorageReference imageRef = storage.getReferenceFromUrl(sportNews.getImageUrl());

                        // Get the public download URL asynchronously
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update the Article object with the new public download URL
                            sportNews.setImageUrl(uri.toString());
                            // Add the article to the list and notify adapter
                            articleList.add(sportNews);
                            // Sort the article list by timestamp in descending order (latest news first).
                            Collections.sort(articleList, new Comparator<Article>() {
                                @Override
                                public int compare(Article o1, Article o2) {
                                    return Long.compare(o2.getTimestamp(), o1.getTimestamp()); // Latest first
                                }
                            });
                            adapter.notifyDataSetChanged();
                        }).addOnFailureListener(exception -> {
                            // Handle any errors during URL retrieval
                            Toast.makeText(getContext(), "Failed to get image URL: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            // If image fails to load, still add the article with its existing (possibly invalid) URL or a default
                            articleList.add(sportNews);
                            Collections.sort(articleList, new Comparator<Article>() {
                                @Override
                                public int compare(Article o1, Article o2) {
                                    return Long.compare(o2.getTimestamp(), o1.getTimestamp()); // Latest first
                                }
                            });
                            adapter.notifyDataSetChanged();
                        });
                    } else {
                        // If it's not a gs:// URL, assume it's already a direct HTTP/HTTPS URL or a placeholder
                        articleList.add(sportNews);
                        Collections.sort(articleList, new Comparator<Article>() {
                            @Override
                            public int compare(Article o1, Article o2) {
                                return Long.compare(o2.getTimestamp(), o1.getTimestamp()); // Latest first
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // If no sport news is found, notify adapter (it will be empty)
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            // This method is called if a listener is cancelled or if the client does not have permission.
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Display an error message to the user if data fetching fails.
                Toast.makeText(getContext(), "Failed to load sports news: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    // Callback method when "Read more" is clicked on an article card.
    // It navigates to the ArticleDetailFragment, passing the article details.
    public void onReadMoreClick(Article article) {
        // Create a new instance of ArticleDetailFragment, passing the article's data.
        ArticleDetailFragment detailFragment = ArticleDetailFragment.newInstance(
                article.getTitle(),
                // Format the timestamp for display in the detail fragment.
                new SimpleDateFormat("MMM dd,yyyy", Locale.getDefault()).format(new Date(article.getTimestamp())),
                article.getContent(),
                article.getImageUrl() // Pass the image URL to the detail fragment
        );

        // Use the FragmentManager to replace the current fragment with the detail fragment.
        // addToBackStack(null) allows the user to press the back button to return to SportsFragment.
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment) // R.id.fragmentContainer is where fragments are displayed
                .addToBackStack(null) // Add transaction to back stack
                .commit(); // Commit the transaction
    }
}
