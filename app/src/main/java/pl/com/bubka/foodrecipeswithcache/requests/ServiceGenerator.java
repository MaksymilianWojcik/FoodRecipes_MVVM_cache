package pl.com.bubka.foodrecipeswithcache.requests;

import pl.com.bubka.foodrecipeswithcache.util.Constants;

import pl.com.bubka.foodrecipeswithcache.util.LiveDataCallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }
}
