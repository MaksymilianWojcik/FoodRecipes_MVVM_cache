package pl.com.bubka.foodrecipeswithcache.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import pl.com.bubka.foodrecipeswithcache.models.Recipe;
import pl.com.bubka.foodrecipeswithcache.repositories.RecipeRepository;
import pl.com.bubka.foodrecipeswithcache.util.Resource;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";
    public static final String QUERY_EXHAUSTED = "No more results";

    public enum ViewState {CATEGORIES, RECIPES}; //should be repaced with static final int later

    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

    private RecipeRepository recipeRepository;

    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;



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

    public int getPageNumber(){
        return pageNumber;
    }

    public void searchRecipesApi(String query, int pageNumber){
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = pageNumber;
            this.query = query;
            isQueryExhausted = false;
            executeSearch();
        }
    }

    private void executeSearch(){
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPES);
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query, pageNumber);
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    recipes.setValue(listResource);
                    if(listResource.status == Resource.Status.SUCCESS){
                        isPerformingQuery = false;
                        if(listResource.data != null){
                            if(listResource.data.size() == 0){
                                Log.i(TAG, "onChanged: query is exhauted...");
                                recipes.setValue(
                                        new Resource<List<Recipe>>(
                                                Resource.Status.ERROR,
                                                listResource.data,
                                                QUERY_EXHAUSTED
                                        )
                                );
                            }
                        }
                        recipes.removeSource(repositorySource);
                    } else if(listResource.status == Resource.Status.ERROR){
                        isPerformingQuery = false;
                        recipes.removeSource(repositorySource);
                    }
                } else {
                    recipes.removeSource(repositorySource); //zawsze musimy pamietac zey usunac srouce bo ebdziemy miec duplikaty
                }
            }
        });
    }


}















