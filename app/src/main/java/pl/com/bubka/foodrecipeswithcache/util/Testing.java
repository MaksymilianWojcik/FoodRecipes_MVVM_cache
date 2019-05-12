package pl.com.bubka.foodrecipeswithcache.util;

import android.util.Log;

import java.util.List;

import pl.com.bubka.foodrecipeswithcache.models.Recipe;

public class Testing {

    public static void printRecipes(List<Recipe> list, String tag) {
        for (Recipe recipe : list) {
            Log.i(tag, "onChanged: " + recipe.getTitle());
        }
    }
}
