# GitHub Repository Kereső

**Feladat:**

Egy GitHub Search API-ra épülő repository kereső Android alkalmazás létrehozása.

**Technikai követelmények**

- Kotlin nyelven és Android Studioban valósítsd meg.
- Bármilyen eszközt és keretrendszert használhatsz, amely szerinted releváns és
    elősegíti a hatékony megoldást.
- A UI kialakítására Jetpack Compose-t használj.
- Az alkalmazás UI-tervezése során vedd figyelembe a Material Design ajánlásait.
- A GitHub Search API-t autentikáció nélkül használd. A dokumentáció itt érhető el:
   [GitHub Search API Dokumentáció](https://docs.github.com/en/rest/search/search?apiVersion=2022-11-28#search-repositories)
- Az elkészült alkalmazást töltsd fel GitHub-ra és azt oszd meg ezzel a felhasználóval:
    HajduBalint.

**Üzleti követelmények**

- Az alkalmazás nyitóképernyőjén jeleníts meg:
    - Egy szöveges beviteli mezőt
    - Egy keresést indító gombot
    - A beviteli mezőben a keresési feltételek megadhatók kulcsszavakkal és
       opcionálisan minősítőkkel is.
- A megadott keresési feltétellel kérd le a találatokat a GitHub Search API-ról, és
    listázd ki őket.
- Biztosíts töltési állapotot és valósíts meg hibakezelést.
- Egy listaelem tartalmazza az alábbi adatokat:
    - Név
    - Leírás
    - Csillagok száma
    - Utolsó frissítés időpontja
- Egy listaelem kiválasztása után egy részletes képernyőn jelenítsd meg a repository további adatait:
    - Tulajdonos neve, avatárja és egy link a GitHub profiljára
    - Név
    - Leírás
    - GitHub repository link
    - Csillagok száma
    - Forkok száma
    - Létrehozás időpontja
    - Utolsó frissítés időpontja

Ha bármilyen kérdésed van a megoldás során, bátran keress minket!