package com.example.fotconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log; // Import Log for debugging

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fotconnect.R;
import com.example.fotconnect.models.Article;
import com.google.firebase.storage.FirebaseStorage; // Import FirebaseStorage
import com.google.firebase.storage.StorageReference; // Import StorageReference

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    public interface OnReadMoreClickListener {
        void onReadMoreClick(Article article);
    }

    private List<Article> articleList;
    private OnReadMoreClickListener readMoreClickListener;

    private static final String TAG = "ArticleAdapter"; // Tag for logging

    public ArticleAdapter(List<Article> articleList, OnReadMoreClickListener listener) {
        this.articleList = articleList;
        this.readMoreClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);

        holder.articleTitle.setText(article.getTitle());
        holder.articleSummary.setText(article.getContent());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy", Locale.getDefault());
        String date = sdf.format(new Date(article.getTimestamp()));
        holder.articleDate.setText("Date Published : " + date);

        String imageUrl = article.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Check if the URL is a Firebase Storage gs:// path
            if (imageUrl.startsWith("gs://")) {
                try {
                    // Get a StorageReference from the gs:// URL
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

                    // Show a temporary placeholder while fetching the download URL
                    Glide.with(holder.articleImage.getContext())
                            .load(R.drawable.error) // Ensure you have a 'placeholder.png' or similar in res/drawable
                            .into(holder.articleImage);

                    // Get the public download URL asynchronously
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Once the download URL is obtained, load it with Glide
                        Glide.with(holder.articleImage.getContext())
                                .load(uri)
                                .placeholder(R.drawable.error) // Can keep placeholder for a smoother transition
                                .error(R.drawable.error) // Show error image if Glide fails to load the HTTPS URL
                                .into(holder.articleImage);
                    }).addOnFailureListener(exception -> {
                        // Log the error and show a generic error image if URL fetching fails
                        Log.e(TAG, "Failed to get download URL for " + imageUrl + ": " + exception.getMessage());
                        Glide.with(holder.articleImage.getContext())
                                .load(R.drawable.error) // Show your error drawable
                                .into(holder.articleImage);
                    });
                } catch (IllegalArgumentException e) {
                    // Catch if the gs:// URL format itself is invalid
                    Log.e(TAG, "Invalid gs:// URL format: " + imageUrl + ". Error: " + e.getMessage());
                    Glide.with(holder.articleImage.getContext())
                            .load(R.drawable.error)
                            .into(holder.articleImage);
                }
            } else {
                // If it's already an HTTPS URL or another type of web URL, load directly
                Glide.with(holder.articleImage.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.error)
                        .error(R.drawable.error)
                        .into(holder.articleImage);
            }
        } else {
            // If imageUrl is null or empty in the Article object, show a default 'no image' drawable
            Glide.with(holder.articleImage.getContext())
                    .load(R.drawable.error) // Consider adding a 'no_image_available.png' to your drawables
                    .into(holder.articleImage);
        }

        holder.readMore.setOnClickListener(v -> {
            if (readMoreClickListener != null) {
                readMoreClickListener.onReadMoreClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView articleTitle, articleSummary, articleDate, readMore;
        ImageView articleImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleTitle = itemView.findViewById(R.id.articleTitle);
            articleSummary = itemView.findViewById(R.id.articleSummary);
            articleDate = itemView.findViewById(R.id.articleDate);
            articleImage = itemView.findViewById(R.id.articleImage);
            readMore = itemView.findViewById(R.id.readMore);
        }
    }
}