package pl.com.bubka.foodrecipeswithcache.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import pl.com.bubka.foodrecipeswithcache.R;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.com.bubka.foodrecipeswithcache.models.Recipe;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    CircleImageView categoryImage;
    TextView categoryTitle;
    OnRecipeListener listener;
    RequestManager requestManager;

    public CategoryViewHolder(@NonNull View itemView, OnRecipeListener listener, RequestManager requestManager) {
        super(itemView);

        this.listener = listener;
        this.requestManager = requestManager;

        categoryImage = itemView.findViewById(R.id.category_image);
        categoryTitle = itemView.findViewById(R.id.category_title);

        itemView.setOnClickListener(this);
    }


    public void onBind(Recipe recipe) {
        Uri path = Uri.parse("android.resource://pl.com.bubka.foodrecipeswithcache/drawable/" + recipe.getImage_url());
        requestManager
                .load(path)
                .into(categoryImage);

        categoryTitle.setText(recipe.getTitle());
    }

    @Override
    public void onClick(View v) {
        listener.onCategoryClick(categoryTitle.getText().toString());
    }
}
