(ns ai-zootson.core
  (:refer-clojure :exclude [==])
  (:require [slingshot.slingshot :refer [throw+]]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [ai-zootson.domain :refer :all]
            [ai-zootson.csv :refer :all]
            [ai-zootson.questions :refer :all]
            [ai-zootson.util :refer :all]
            [ai-zootson.facts :refer [read-facts]]
            ))

(defn read-zoo-data [db [name hair feathers eggs milk airborne aquatic predator toothed backbone
                         breathes venomous fins legs tail domestic catsize type]]
  (-> db
      (pldb/db-fact animal name)
      (pldb/db-fact has-prop name "hair" (= hair "1"))
      (pldb/db-fact has-prop name "feathers" (= feathers "1"))
      (pldb/db-fact has-prop name "eggs" (= eggs "1"))
      (pldb/db-fact has-prop name "milk" (= milk "1"))
      (pldb/db-fact has-prop name "airborne" (= airborne "1"))
      (pldb/db-fact has-prop name "aquatic" (= aquatic "1"))
      (pldb/db-fact has-prop name "predator" (= predator "1"))
      (pldb/db-fact has-prop name "toothed" (= toothed "1"))
      (pldb/db-fact has-prop name "backbone" (= backbone "1"))
      (pldb/db-fact has-prop name "breathes" (= breathes "1"))
      (pldb/db-fact has-prop name "venomous" (= venomous "1"))
      (pldb/db-fact has-prop name "fins" (= fins "1"))
      (pldb/db-fact has-prop name "legs" (Integer/parseInt legs))
      (pldb/db-fact has-prop name "tail" (= tail "1"))
      (pldb/db-fact has-prop name "domestic" (= domestic "1"))
      (pldb/db-fact has-prop name "catsize" (= catsize "1"))
      (pldb/db-fact classify name (get animal-classes (Integer/parseInt type)))
      ))

(defn read-continent [db [name africa europe asia north-america south-america australia antarctica]]
  (-> db
      ;; zoo.data and continents.txt should have same animals but it wont hurt to define animals again
      (pldb/db-fact animal name)
      (pldb/db-fact lives-in name "africa" (= africa "1"))
      (pldb/db-fact lives-in name "europe" (= europe "1"))
      (pldb/db-fact lives-in name "asia" (= asia "1"))
      (pldb/db-fact lives-in name "north america" (= north-america "1"))
      (pldb/db-fact lives-in name "south america" (= south-america "1"))
      (pldb/db-fact lives-in name "australia" (= australia "1"))
      (pldb/db-fact lives-in name "antarctica" (= antarctica "1"))
      ))

(defn read-file [db file-name f & rest]
  (with-open [rdr (clojure.java.io/reader file-name)]
    (apply f db rdr rest)))

(defn read-files []
  "Create a new db and add facts from files into it."
  (-> (pldb/db)
      (read-file "inputs/zoo.data" read-data read-zoo-data)
      (read-file "inputs/continents.txt" read-data read-continent)
      (read-file "inputs/facts.txt" read-facts)
      (read-file "inputs/own_facts.txt" read-facts)
      ))
