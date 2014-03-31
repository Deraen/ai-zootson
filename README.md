# Ai-zootson documentation

MAT-75006 Artificial Intelligence 2014

Selkeyden, sekä kiireen kannalta, kirjoitin tämä suurimmaksi osaksi suomeksi.

## Running

```
java -jar ai-zootson.jar # Run precompiled program
./lein run # Compile & run
./lein midje # Run unit tests
```

## How does your implementation work?

Toteutus on tehty funktionaalisessä Clojure kielellä joka on Lisp toteutus Java Virtual Machinelle.
Toteutus hyödyntää core.logic-kirjastoa joka tarkoaa Clojurella Prolog tyylisen logiikkaohjelmoinnin.
Lauseiden parsimiseen käytän instaparse-kirjastoa jolle voi syöttää BNF-määrityksen tekstinä
ja se luo siirtä funktion joka jäsentelee syötteen kielen mukaan.

Core.logic tarjoaa faktatietokannan: https://github.com/clojure/core.logic/wiki/Features#simple-in-memory-database
Faktat kuvataan relaatioina esim. `(has-prop "gorilla" "hair" true)` ja sieltä voi tehdä hakuja jossa yksi tai useampi
relaation muuttujista voi olla vapaa: ` (run* [q] (has-prop q "hair" true))`` palauttaisi eläimet joilla on karvoitusta.
Hauissa vois myös olla vapaita muuttujia joista ei olla kiinnostuneita:
```
(run* [q]
  (fresh [x]
    (has-prop "gorilla" x y)))
```
Antaisi listan gorillan kaikista tiedoista, välittämättä niiden arvosta...

ps. `test` hakemistosta löytyy yksikkötestit. Niitä on lähes yhtä paljon kuin koodia ja kuvaavat hyvin miten mikäkin funktio toimii.
Erityisesti facts\_test.clj ja questions\_test.clj ovat kiintoisia sillä niissä on testi jokaiselle esimerkin lauseelle
joista näkyy mitenkä BNF parseri toimii.

http://clojure.org/
https://github.com/clojure/core.logic
https://github.com/Engelberg/instaparse

## What AI methods have been used in your implementation?

- Looginen ohjelmointi?
- ei oikein mitään?

## Were you able to use things learned during the course in your implementation?

En.

Kirjassa oli NLP liittyen todella pieni raapaisu enkä kurssin kalvoistakaan löytänyt juuri mitään.

## Were the instructions for the assignment clear enough?

Ehkä muutamilta kohdilta voisi olla selkeämpi.

- Esim. ohjeita mistä faktoista mikäkin vastaus pitäisi pystyä päättelemään
- hieman epäselvää esim:
  - "Cheetah is the fastest land animal." -> "Are girls slower than a cheetah?"
  - "Which is smaller: dolphin or crayfish?", delfiiniä ei mikään fakta maininnut
- jotain ohjetta miten sanojen eri taivutukset/synonyymit kannattaisi hoitaa
  - "swimmer" "swim" "poisonous" "venomous"

## Is it OK to use your implementation in our AI research?

Kyllä

## What did this assignment teach you, if anything?

- Loogista ohjelmointi
- Merkkijonojen parsimista BNF:llä
- Clojuresta taas paljon lisää
- NLP on todella vaikeata

## How much time did you use for this assignment?
- 150h, enemmän kun olisi kerennyt tekemään niin ei olisi jäänyt näin myöhään ja keskeneräiseksi.

## Possible other opinions or remarks concerning the assignment and suggestions for the future?

- Saako koodin laittaa julkiseksi?
- Kuinkahan moni tämän sai palautettua, tuntui todella vaikealta
- Haluasin tietää miten tämä olisi kannattanut tehdä. Oma ratkaisu meinasi hajota
  käsiin viimeisen päivän aikana.
