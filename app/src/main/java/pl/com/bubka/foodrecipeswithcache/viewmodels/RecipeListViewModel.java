package pl.com.bubka.foodrecipeswithcache.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import pl.com.bubka.foodrecipeswithcache.models.Recipe;
import pl.com.bubka.foodrecipeswithcache.repositories.RecipeRepository;
import pl.com.bubka.foodrecipeswithcache.util.Resource;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";


    public enum ViewState {CATEGORIES, RECIPES}; //should be repaced with static final int later

    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

    private RecipeRepository recipeRepository;


    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipeRepository = RecipeRepository.getInstance(application);
        init();

    }

    private void init() {
        if(viewState == null){ //nie zostal jeszcze zainicjowany (np. kied peirwszy raz jest stworzony view model
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES); //chcemy za pierwszym razem widziec kategorie
        }
    }

    public LiveData<ViewState> getViewState(){
        return viewState;
    }

    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return recipes;
    }

    public void searchRecipesApi(String query, int pageNumber){
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query, pageNumber);
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {

                //mozemy tutaj robic co checmy, modyfikowac itp. bo to w koncu medaitor live data
                recipes.setValue(listResource);
            }
        });
    }


}















