package pl.com.bubka.foodrecipeswithcache.requests;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import pl.com.bubka.foodrecipeswithcache.util.Constants;

import pl.com.bubka.foodrecipeswithcache.util.LiveDataCallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pl.com.bubka.foodrecipeswithcache.util.Constants.CONNECTION_TIMEOUT;
import static pl.com.bubka.foodrecipeswithcache.util.Constants.READ_TIMEOUT;
import static pl.com.bubka.foodrecipeswithcache.util.Constants.WRITE_TIMEOUT;

public class ServiceGenerator {

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS) //czy udalo nam sie nawiazac polaczenie z serwerem
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //czas miedzy kazdym bajtem odczytanym z serwera
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //czas miedzy kazdym bajtem wyslanym do serwera
            .retryOnConnectionFailure(false) //nie chcemy probowac na nowo jak sie nie uda nawiazac polaczenia, zazwyczaj pare razy proboje
            //my jednak chcemy ze jak connetiontimeout to juz koniec i tyle
            .build();

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }
}
