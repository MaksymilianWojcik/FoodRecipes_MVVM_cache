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


## Guide to app architecture (android documentation) + google sample
[Android documentation](https://developer.android.com/jetpack/docs/guide)

Na tym opiera sie ten projekt.
Szczegolnie zwrocmy uwage w dokumentacji na przykład i klase Resource

### Resources
#### @NonNull lyb @Nullable
Adnotacje sa tylko po to, zeby kompilarowi powiedziec co chcemy. To po prostu mowi kompilatorowi, zeby pokazal nam warninga. Nie jest to potrzebne

### ApiResponse
Czesc best-practices w google samples. Klasa ta ma czytac raw responses z retrofita i decydowac, jaki status im przypisac: request SUCCESFULL, EMPTY lub ERROR.

### Single source of truth principle
Zazwyczaj apka dostaje dane z 2 zrodel:
- REST API
- Local db

w single source of truth principle chodzi o to, zeby dane pochodzily z jednego zrodla. Tzn. z databae cache. Ale zaraz, gdzie tu sens, przeciez bedziemy robic network requesty??
No tak, ale spojrzmy na cala droge pobeirania danych:

REST API -> Local DB cache -> APKA

Tak wiec uzytkownik widzi tylko to co jest w cache, bo dane pobrane z resta przechodza przez cache. Nowe dane sa insertowane do cache.
Wiec mamy single srouce of truth, ale w rzeczywiscoti w glebi mamy dane z 2 zrodel.

Dzieki temu w Androidzie mamy szybkie dzialanie apki. Np. jak nie mamy neta, to wtedy mamy tez dane z cacha. Oprocz tego mamy clean code, latwy do testowania.
Dobrze tez sie to wpasowuje do MVVM.


### NetworkBoundResource
Decyduje skad pobrac dane, co `robic z danymi itp.. To abstrakcyjna klasa, wiec bedziemy ja rozszerzac.

# Skrotowe notatki:


- ViewModel za nas obsluguje lifecycle, tzn. nie musimy sami obslugiwac zapisania stanu activiyt przy przejsciu w onPause itp.
- MutableLiveData -> ale getLiveData

