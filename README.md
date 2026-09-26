# Football App – TheSportsDB Kotlin Android

Modern Android foci alkalmazás Jetpack Compose-szal a **TheSportsDB** API használatával.

## Funkciók

- **Meccsek** fül: Következő és legutóbbi mérkőzések (Premier League, La Liga, Serie A, Bundesliga, Ligue 1, Champions League)
- **Keresés** fül: Csapatok és játékosok keresése
- **Tabella** fül: Liga tabellák
- Csapat részletes oldal: jelvény, leírás, következő/legutóbbi meccsek

## Technológia

- Kotlin
- Jetpack Compose + Material 3
- Retrofit + OkHttp + Gson
- Coil (képek)
- Navigation Compose
- Coroutines + Flow + ViewModel
- Sötét sportos dizájn

## API

Ingyenes kulcs: `123`  
Base URL: `https://www.thesportsdb.com/api/v1/json/123/`

**Fontos korlátozások az ingyenes kulccsal:**
- A csapatkeresés csak bizonyos csapatokra működik (pl. Arsenal)
- A tabella és néhány endpoint korlátozott lehet
- Rate limit: 30 kérés/perc

Prémium kulcs esetén cseréld ki az `ApiClient.kt`-ben az `API_KEY` értékét.

## Telepítés

1. Nyisd meg a projektet **Android Studio**-ban (Hedgehog vagy újabb ajánlott)
2. Sync Gradle
3. Futtasd emulátoron vagy fizikai eszközön (minSdk 26)

## Projekt struktúra

```
app/src/main/java/com/example/footballapp/
├── data/
│   ├── api/          # Retrofit interface + client
│   ├── model/        # Data classes
│   └── repository/   # Repository
├── ui/
│   ├── components/   # Újrahasználható UI elemek
│   ├── navigation/   # NavGraph
│   ├── screens/      # Képernyők
│   └── theme/        # Színek, tipográfia
├── viewmodel/        # ViewModel-ek
└── MainActivity.kt
```

## Továbbfejlesztési ötletek

- Élő eredmények (v2 API + prémium)
- Kedvenc csapatok mentése (DataStore / Room)
- Push értesítések meccsekről
- Játékos részletes oldal
- Szezon választó a tabellához
- Offline cache

Készítve a TheSportsDB dokumentáció alapján.
