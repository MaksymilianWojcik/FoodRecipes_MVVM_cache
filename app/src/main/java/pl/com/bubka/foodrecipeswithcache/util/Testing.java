package pl.com.bubka.foodrecipeswithcache.util;

import android.util.Log;

import pl.com.bubka.foodrecipeswithcache.models.Recipe;

import java.util.List;

public class Testing {

    public static void printRecipes(List<Recipe>list, String tag){
        for(Recipe recipe: list){
            Log.i(tag, "onChanged: " + recipe.getTitle());
        }
    }
}
