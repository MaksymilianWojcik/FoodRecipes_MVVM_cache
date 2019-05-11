package pl.com.bubka.foodrecipeswithcache.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import pl.com.bubka.foodrecipeswithcache.AppExecutors;
import pl.com.bubka.foodrecipeswithcache.models.Recipe;
import pl.com.bubka.foodrecipeswithcache.persistence.RecipeDao;
import pl.com.bubka.foodrecipeswithcache.persistence.RecipeDatabase;
import pl.com.bubka.foodrecipeswithcache.requests.ServiceGenerator;
import pl.com.bubka.foodrecipeswithcache.requests.responses.ApiResponse;
import pl.com.bubka.foodrecipeswithcache.requests.responses.RecipeSearchResponse;
import pl.com.bubka.foodrecipeswithcache.util.Constants;
import pl.com.bubka.foodrecipeswithcache.util.NetworkBoundResource;
import pl.com.bubka.foodrecipeswithcache.util.Resource;

import static android.support.constraint.Constraints.TAG;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeDao recipeDao;

    public static RecipeRepository getInstance(Context context){
        if(instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    private RecipeRepository(Context context) {
        recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }

    public LiveData<Resource<List<Recipe>>> searchRecipesApi(final String query, final int pageNumber){
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull RecipeSearchResponse item) {
                //tu zapiszemy dane z retorfita do cachea
                if(item.getRecipes() != null){ //recipe list == null jak np. api key jest expired
                    Recipe[] recipes = new Recipe[item.getRecipes().size()];
                    int index = 0;
                    for(long rowid : recipeDao.insertRecipes((Recipe[]) (item.getRecipes().toArray(recipes)))){
                        if(rowid == -1){ //mamy konflikt
                            Log.i(TAG, "saveCallResult: CONFLICT... Recipe already in cache");
                            // jak przepis juz istenieje... Nie hcemy ustawiac ingredient czy timestampa bo zostana wymazane
                            recipeDao.updateRecipe(
                                    recipes[index].getRecipe_id(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getImage_url(),
                                    recipes[index].getSocial_rank()
                            );
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                //decyduje czy odswiezyc cachea, po to dodalismy timestampa do modelu. Dajemy true bo przy searchRecipes zawsze chcemy
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                //zwracamy dane z cachea
                return recipeDao.searchRecipes(query, pageNumber);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                //tutaj bedzie skomplikowane troszke.
                //creteCalls zwraca obiekt LiveData, metoda ta tworzy retrofit call obiekt, a raczej LiveData retrofit call obiekt.
                //Dltego bedziemy musiel izorbic RetrofitConveter do skonwertowania Call na LiveData
                return ServiceGenerator.getRecipeApi().searchRecipe(Constants.API_KEY, query, String.valueOf(pageNumber));
            }
        }.getAsLiveData();
    }
}
