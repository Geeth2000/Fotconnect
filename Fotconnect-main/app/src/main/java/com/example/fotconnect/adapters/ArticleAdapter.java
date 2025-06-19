package com.example.fotconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Glide library for image loading from URLs
import com.example.fotconnect.R;
import com.example.fotconnect.models.Article;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// ArticleAdapter is responsible for populating the RecyclerView with Article data.
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    // Interface for click listener on "Read more" button within an article card.
    public interface OnReadMoreClickListener {
        void onReadMoreClick(Article article);
    }

    private List<Article> articleList; // List of Article objects to display
    private OnReadMoreClickListener readMoreClickListener; // Listener for "Read more" clicks

    // Constructor for the ArticleAdapter.
    // @param articleList The list of articles to be displayed.
    // @param listener The listener for read more clicks.
    public ArticleAdapter(List<Article> articleList, OnReadMoreClickListener listener) {
        this.articleList = articleList;
        this.readMoreClickListener = listener;
    }

    @NonNull
    @Override
    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the article_card layout to create a new view holder.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    // Called by RecyclerView to display the data at the specified position.
    // This method updates the contents of the ViewHolder to reflect the item at the given position.
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position); // Get the current article

        // Set article title, summary (content), and formatted date.
        holder.articleTitle.setText(article.getTitle());
        holder.articleSummary.setText(article.getContent()); // Display content as summary in card

        // Format the timestamp (milliseconds) into a human-readable date string.
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String date = sdf.format(new Date(article.getTimestamp()));
        holder.articleDate.setText("Date Published : " + date);

        // Load the image from the URL into the ImageView using Glide.
        // A placeholder and error image are provided in case the URL is invalid or image loading fails.
        Glide.with(holder.articleImage.getContext())
                .load(article.getImageUrl())
                .placeholder(R.drawable.error) // Placeholder drawable resource
                .error(R.drawable.error) // Error drawable resource
                .into(holder.articleImage);

        // Set an OnClickListener for the "Read more" TextView.
        // This will trigger the onReadMoreClick method in the listener (e.g., SportsFragment).
        holder.readMore.setOnClickListener(v -> {
            if (readMoreClickListener != null) {
                readMoreClickListener.onReadMoreClick(article);
            }
        });
    }

    @Override
    // Returns the total number of items in the data set held by the adapter.
    public int getItemCount() {
        return articleList.size();
    }

    // ViewHolder class to hold references to the views in each item layout (article_card.xml).
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView articleTitle, articleSummary, articleDate, readMore;
        ImageView articleImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize TextViews and ImageView by finding them in the item layout.
            articleTitle = itemView.findViewById(R.id.articleTitle);
            articleSummary = itemView.findViewById(R.id.articleSummary);
            articleDate = itemView.findViewById(R.id.articleDate);
            articleImage = itemView.findViewById(R.id.articleImage);
            readMore = itemView.findViewById(R.id.readMore);
        }
    }
}
