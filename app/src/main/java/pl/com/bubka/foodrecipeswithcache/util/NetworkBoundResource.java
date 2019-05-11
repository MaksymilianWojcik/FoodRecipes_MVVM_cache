package pl.com.bubka.foodrecipeswithcache.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import pl.com.bubka.foodrecipeswithcache.AppExecutors;
import pl.com.bubka.foodrecipeswithcache.requests.responses.ApiResponse;

import static android.support.constraint.Constraints.TAG;

//CacheObject: Type for the reosurce data (db cache)
// RequestObject: Type for the API response (network request)
public abstract class NetworkBoundResource<CacheObject, RequestObject> {
    //klasa abstrakcyjna - nie moze zostac zainicjowana, moze byc jedynie rozszezona


    //Potrzebny, bo niektore z tych metod beda wykonywane w backgroundzie, a takze zeby postowac cos na main threada
    private AppExecutors appExecutors;

    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init(){
        //Kiedy oeirwszy raz inicjujemy, chcemy sprawdzic w db cache

        //updtejtujemy LiveData na status "LADOWANIE"
        results.setValue((Resource<CacheObject>) Resource.loading(null)); //nic jeszcze nie otrzymujemy, dlatego null

        //obserwujemy LiveData z local db
        final LiveData<CacheObject> dbSource = loadFromDb(); // (1)

        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject cacheObject) {

                results.removeSource(dbSource); //przestajemy sluchac (3)

                if(shouldFetch(cacheObject)){ // (2)
                    //get data from network
                    fetchFromNetwork(dbSource);
                } else {
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject cacheObject) {
                            setValue(Resource.success(cacheObject));
                        }
                    });
                }
            }
        });
    }


    /**
     * 1) Obserwujemy lokal db
     * 2) if <condition/> query network
     * 3) przestajemy obserwowac local db
     * 4) insertujemy nowe dane do db
     * 5) zaczynamy obserwowac local db znowu zeby zobaczyc nowe dane z networka
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource){
        Log.d(TAG, "fetchFromNetwork: called");
        //updatujemy LiveData na status LADOWANIE
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject cacheObject) {
                setValue(Resource.loading(cacheObject)); //sprawdz cache i loading status
            }
        });

        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();
        results.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(@Nullable final ApiResponse<RequestObject> requestObjectApiResponse) {
                results.removeSource(dbSource);
                results.removeSource(apiResponse); //usuwamy, bo zanim bedziemy to obserwowac, musimy obsluzyc 3 mozliwosci z api response

                if(requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse){
                    Log.d(TAG, "onChanged: ApiSuccessResponse");

                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            //zapisac reponse do db
                            saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse)requestObjectApiResponse));

                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                        @Override
                                        public void onChanged(@Nullable CacheObject cacheObject) {
                                            setValue(Resource.success(cacheObject));
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else if (requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse){
                    Log.d(TAG, "onChanged: ApiEmptyResponse");
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                @Override
                                public void onChanged(@Nullable CacheObject cacheObject) {
                                    setValue(Resource.success(cacheObject)); //empty - request succesfull, ale nic nie zwrocilo, dlatego chcemy cache
                                }
                            });
                        }
                    });
                } else if (requestObjectApiResponse instanceof ApiResponse.ApiErrorResposne){
                    Log.d(TAG, "onChanged: ApiErrorResponse");
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject cacheObject) {
                            setValue(
                                    Resource.error(
                                            ((ApiResponse.ApiErrorResposne) requestObjectApiResponse).getErrorMessag(),
                                            cacheObject)
                            );
                        }
                    });
                }
            }
        });

    }

    private CacheObject processResponse(ApiResponse.ApiSuccessResponse response){
        return (CacheObject) response.getBody();
    }

    private void setValue(Resource<CacheObject> newValue){
        if(results.getValue() != newValue){
            results.setValue(newValue);
        }
    }

    //to bedizemy obserwowac. To jest ten nasz single source of truth.
    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>(); //zwroci obiket Resource, ktory trzyma cache. Bo zawsze chcemy patrzec na Cache, nawet jak robimy network requesta

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);

    // Called to get the cached data from the database.
    @NonNull
    @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();

    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<CacheObject>> getAsLiveData(){
        return results;
    };
}
