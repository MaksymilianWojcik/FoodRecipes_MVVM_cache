package pl.com.bubka.foodrecipeswithcache.requests.responses;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pl.com.bubka.foodrecipeswithcache.models.Recipe;

public class RecipeResponse {

    @SerializedName("recipe")
    @Expose()
    private Recipe recipe;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public Recipe getRecipe() {
        return recipe;
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "recipe=" + recipe +
                ", error='" + error + '\'' +
                '}';
    }
}
