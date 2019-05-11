package pl.com.bubka.foodrecipeswithcache.util;

import android.arch.lifecycle.LiveData;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import pl.com.bubka.foodrecipeswithcache.requests.responses.ApiResponse;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    /**
     * Ta metoda wykonuje jakas lizbe sprawdzen i zwraca Response type dla requesta rerofita
     * (@bodyType to ResponseType. Moze byc RecipeResponse lub RecipeSearchResponse)
     *
     * CHECK #1) returnType zwraca LiveData
     * CHECK #2) Type LiveData<T> jest type ApiResponse.class
     * CHECK #3) Upewnij sie ze ApiResponse jest parametryzowany. AKA ApiResponse<T> istnieje
     */
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        // Check#1 upewnic sie ze CallAdapter zwraca LiveDate

        if(CallAdapter.Factory.getRawType(returnType) != LiveData.class){
            return null;
        }

        // Check#2 typ ktory LiveData wrapuje. CZYLI TAK NAPRAWDE DOSTAJEMY SIE DO T
        Type observableType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) returnType);

        // Check czy to Type apiResponse
        Type rawObservableType = CallAdapter.Factory.getRawType(observableType);
        if(rawObservableType != ApiResponse.class){
            throw new IllegalArgumentException("Type must be a defined resource");
        }

        // Check #3 sprawdzamy czy ApiResponse jest parametrized, czyli czy ApiResponse<T> istnieje? (musi byc T)
        // T to albo RecipeResponse albo T bedzie RecipeSearchResponse
        if(!(observableType instanceof ParameterizedType)){
            throw new IllegalArgumentException("Resource must be parametrized");
        }

        Type bodyType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) observableType);
        return new LiveDataCallAdapter<Type>(bodyType);
    }
}
