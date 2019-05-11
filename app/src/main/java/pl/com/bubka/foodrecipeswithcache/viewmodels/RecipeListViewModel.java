package pl.com.bubka.foodrecipeswithcache.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";


    public enum ViewState {CATEGORIES, RECIPES}; //should be repaced with static final int later

    private MutableLiveData<ViewState> viewState;


    public RecipeListViewModel(@NonNull Application application) {
        super(application);

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


}















