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
Decyduje skad pobrac dane, co `robic z danymi itp.. To abstrakcyjna klasa, wiec mozemy ja tylko rozszerzac.


####MediatorLiveData
Jest to subklasa LiveDaty. Dzieki niej mozemy obserwowac inne obiekty LiveData i reagowac na ich onChanged eventy.
Klasa ta prawidlowo propaguje jej active/inactive stany w dol do zrodla obiektu LiveData

Np. mamy 2 instancje LiveData, livedata1 i livedata2, i chcemy zmergowac ich emisje w jeden obiekt, kotrym wlasnie bedzie mediatorLiveData.
W tym wypadku livedata1 i livedata2 zostana zrodlem (source) dla MediatorLiveData i za kazdym razem kiedy onChanged callback bedzie wywolany dla
ktorejkolwiek z nich, to ustawiamy nowa wartosc w mediatorlivedata.

z dokumentacji:
"If the given LiveData is already added as a source but with a different Observer, IllegalArgumentException will be thrown."

### Room

#### Converters
Room nie moze np. przechowywac tablicy (Recipe.class przechowuje tak skladniki). Dlatego musimy te tablice skonwertowac na taki typ,
jaki room moze przechowywac.

# Skrotowe notatki:


- ViewModel za nas obsluguje lifecycle, tzn. nie musimy sami obslugiwac zapisania stanu activiyt przy przejsciu w onPause itp.
- MutableLiveData -> ale getLiveData


#Retrofit

Retrofit to wrapper na OkHTTP i to jest wlasnei odpoweidzialne za robienie requestow.
Dzieki temu mozemy stworzyc swojego clienta okhttp i ustawic go na retrofita, ustawic timeouty wedlug potrzeb itp.

#Glide - cache

Kiedy np. zrobimy requesta, mamy liste i nagle wylaczymy neta, to lista przepisow bedzie zaladowana, ale bez zdjec. To dlatego, ze glide
domyslnie tych obrazkow nie cachuje, tylko cachuje pare obrazkow pozatym co uzytkownik widzi w recyclerview.
I tu uzyjemy tzw. glide-preloadera
##Glide preloader
[RecyclerView Glide](http://bumptech.github.io/glide/int/recyclerview.html)


## Ciekawosta
W android studi mozemy na emulatorze ustawic predkosc internetu jaka chcemy, ustawiamy to w ustawieniach emulatora -> show advanced settings -> 
i tam np. na bardzo wolne to na GPRS

