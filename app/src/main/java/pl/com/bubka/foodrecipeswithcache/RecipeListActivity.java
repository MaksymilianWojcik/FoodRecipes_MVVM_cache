package pl.com.bubka.foodrecipeswithcache;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import java.util.List;

import pl.com.bubka.foodrecipeswithcache.adapters.OnRecipeListener;
import pl.com.bubka.foodrecipeswithcache.adapters.RecipeRecyclerAdapter;
import pl.com.bubka.foodrecipeswithcache.models.Recipe;
import pl.com.bubka.foodrecipeswithcache.util.Resource;
import pl.com.bubka.foodrecipeswithcache.util.Testing;
import pl.com.bubka.foodrecipeswithcache.util.VerticalSpacingItemDecorator;
import pl.com.bubka.foodrecipeswithcache.viewmodels.RecipeListViewModel;


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
                        Testing.printRecipes(listResource.data, "DataTest");
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

    private void searchRecipesApi(String query){
        mRecipeListViewModel.searchRecipesApi(query, 1);
    }

    private void displaySearchCategories() {
        mAdapter.displaySearchCategories();
    }


    private void initRecyclerView(){
        mAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

}

















