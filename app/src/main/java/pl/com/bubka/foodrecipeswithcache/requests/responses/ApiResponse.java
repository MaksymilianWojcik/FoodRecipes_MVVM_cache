package pl.com.bubka.foodrecipeswithcache.requests.responses;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse<T> {

    public ApiResponse<T> create(Throwable error) {
        return new ApiErrorResposne<>(error.getMessage().equals("") ? error.getMessage() : "Unknown error\n Check netowrk connection");
    }

    public ApiResponse<T> create(Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();

            if (body instanceof RecipeSearchResponse) {
                if (!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeSearchResponse) body)) {
                    String error = "Api key expired";
                    return new ApiErrorResposne<>(error);
                }
            }

            if (body instanceof RecipeResponse) {
                if (!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeResponse) body)) {
                    String error = "Api key expired";
                    return new ApiErrorResposne<>(error);
                }
            }

            if (body == null || response.code() == 204) { //204 to pusty response code
                return new ApiEmptyResponse<>();
            } else {
                return new ApiSuccessResponse<>(body);
            }
        } else {
            String errorMessage = "";
            try {
                errorMessage = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = response.message();
            }
            return new ApiErrorResposne<>(errorMessage);
        }
    }

    public class ApiSuccessResponse<T> extends ApiResponse<T> { //T to cokolwiek zwrocimy z retrofita

        private T body; //body responsa

        ApiSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }
    }


    public class ApiErrorResposne<T> extends ApiResponse<T> {
        private String errorMessag;

        ApiErrorResposne(String errorMessag) {
            this.errorMessag = errorMessag;
        }

        public String getErrorMessag() {
            return errorMessag;
        }
    }


    public class ApiEmptyResponse<T> extends ApiResponse<T> {
    }
}
