# FoodRecipes_MVVM_cache
Food Recipes MVVM app with local cache


# Retrofit cache vs SQLite Cache

## Dlaczego nie retrofit cache?

1. Kiedy robimy requesta, to retrofit cachuje requesta oraz jego odpowiedz z serwera (key(url)-value(response)). 
Jednak co np. gdy zrobimy requesta z np. "id=1", a pozniej wylaczymy neta i zrobimy requesta z "id=1 & id=2"? No retrofit bedzie mial z tym problem lekki. Retrofit polaczy tylko konkretne query z responsem. 
Tak samo jak np. zmienimy jakas literke na wielka - retrofit sobie z tym nie poraddzi.

2. Zrobimy requesta z lista, scrollujemy sobie na dol i glide cachuje obrazki. Retrofit requesta i responsa, glide obrazki. Teraz gdy wylacze neta i bede scrollowal na dol po requescie, to na liscie zaczna sie pojawiac puste obrazki.
Slabe doswiadczenie dla uzytkownika, ale tutaj widzimy, ze retrofit-cache (wiadomo, ze tutaj jesze wchodzi w gre glide) nie spelnia wszytkich wymagan.


## Bazy danych

1. Relational (SQLite)
2. Non-Relational (Firebase db)


## Guid to app architecture (android documentation) + google sample
[Android documentation](https://developer.android.com/jetpack/docs/guide)

Na tym opiera sie ten projekt.
Szczegolnie zwrocmy uwage w dokumentacji na przykład i klase Resource

### @NonNull lyb @Nullable
Adnotacje sa tylko po to, zeby kompilarowi powiedziec co chcemy. To po prostu mowi kompilatorowi, zeby pokazal nam warninga. Nie jest to potrzebne




# Skrotowe notatki:


- ViewModel za nas obsluguje lifecycle, tzn. nie musimy sami obslugiwac zapisania stanu activiyt przy przejsciu w onPause itp.
- MutableLiveData -> ale getLiveData

