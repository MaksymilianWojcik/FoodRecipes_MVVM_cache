package pl.com.bubka.foodrecipeswithcache;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.util.List;

import pl.com.bubka.foodrecipeswithcache.adapters.OnRecipeListener;
import pl.com.bubka.foodrecipeswithcache.adapters.RecipeRecyclerAdapter;
import pl.com.bubka.foodrecipeswithcache.models.Recipe;
import pl.com.bubka.foodrecipeswithcache.util.Resource;
import pl.com.bubka.foodrecipeswithcache.util.VerticalSpacingItemDecorator;
import pl.com.bubka.foodrecipeswithcache.viewmodels.RecipeListViewModel;

import static pl.com.bubka.foodrecipeswithcache.viewmodels.RecipeListViewModel.QUERY_EXHAUSTED;


public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        initSearchView();
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        subscribeObservers();
    }


    private void subscribeObservers(){

        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    Log.i(TAG, "onChanged: status: " + listResource.status);
                    if(listResource.data != null){
                        switch(listResource.status){
                            case LOADING:
                                if(mRecipeListViewModel.getPageNumber() > 1){
                                    mAdapter.displayLoading();
                                } else {
                                    mAdapter.displayOnlyLoading(); //szukamy pierwsza strone
                                }
                                break;
                            case ERROR:
                                Log.e(TAG, "onChanged: Cannot refresh the cache");
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message);
                                Log.e(TAG, "onChanged: status: ERROR, #recipes: " + listResource.data.size()); //ile z cacha przepisow
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_LONG).show();

                                if(listResource.message.equals(QUERY_EXHAUSTED)){
                                    mAdapter.setQueryExhausted();
                                }
                                break;
                            case SUCCESS:
                                Log.i(TAG, "onChanged: cache was refreshed");
                                Log.i(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                break;
                        }
                    }
                }
            }
        });

        mRecipeListViewModel.getViewState().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(@Nullable RecipeListViewModel.ViewState viewState) {
                if(viewState != null){
                    switch(viewState){
                        case RECIPES:
                            //recipes beda automatycznie pokazywane z innego observera
                            break;
                        case CATEGORIES:
                            displaySearchCategories();
                            break;
                    }
                }
            }
        });
    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);
        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    private void searchRecipesApi(String query){
        mRecyclerView.smoothScrollToPosition(0);
        mRecipeListViewModel.searchRecipesApi(query, 1);
        mSearchView.clearFocus();
    }

    private void displaySearchCategories() {
        mAdapter.displaySearchCategories();
    }


    private void initRecyclerView(){
        ViewPreloadSizeProvider<String> viewPreloadSizeProvider = new ViewPreloadSizeProvider<>();
        mAdapter = new RecipeRecyclerAdapter(this, initGlide(), viewPreloadSizeProvider);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewPreloader<String> recyclerViewPreloader = new RecyclerViewPreloader<String>(
                Glide.with(this),
                mAdapter,
                viewPreloadSizeProvider,
                30); //30 chcemy cachowac
        mRecyclerView.addOnScrollListener(recyclerViewPreloader); 

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!mRecyclerView.canScrollVertically(1) && mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.RECIPES){
                    mRecipeListViewModel.searchNextPage();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                searchRecipesApi(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        searchRecipesApi(category);
    }

    @Override
    public void onBackPressed() {
        if(mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.CATEGORIES){
            super.onBackPressed(); //zamykamy apke
        } else {
            mRecipeListViewModel.cancelSearchRequest();
            mRecipeListViewModel.setViewCategories();
        }
    }
}

















