package pl.com.bubka.foodrecipeswithcache.util;

public class Constants {

    public static final String BASE_URL = "https://www.food2fork.com";

    // YOU NEED YOUR OWN API KEY!!!!!!!!!!!!! https://www.food2fork.com/about/api
    public static final String API_KEY = "436a6d9efb827514fa936bf4114ca9ff";

    public static final int CONNECTION_TIMEOUT = 10; //seconds
    public static final int READ_TIMEOUT = 2; //seconds
    public static final int WRITE_TIMEOUT = 2; //seconds

    public static final int RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30; //30 dni w sekundach


    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };
}
