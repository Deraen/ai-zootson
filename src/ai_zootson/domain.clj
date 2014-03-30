(ns ai-zootson.domain
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]))

(pldb/db-rel animal fact)
(pldb/db-rel continent fact)
(pldb/db-rel rank fact)

(def animal-classes {1 "mammal"
                     2 "bird"
                     3 "reptile"
                     4 "fish"
                     5 "amphibian"
                     6 "insect"
                     7 "invertebrate"})
(pldb/db-rel classify animal rank)

(pldb/db-rel lives-in animal continent value)
(pldb/db-rel has-prop animal property)
(pldb/db-rel some-kind-prop animal property value)
(pldb/db-rel eats animal food)
(pldb/db-rel is-able animal thing)
(pldb/db-rel is-smth animal thing)
(pldb/db-rel is-smth-of animal thing adj)

;; Superlatives
(pldb/db-rel is-faster a1 a2)
(pldb/db-rel is-more a1 a2 prop)
(defn is-slower [x y]
  "Complement of is-faster"
  (is-faster y x))
(defn is-less [x y prop]
  (is-more y x prop))

;; Alias
(pldb/db-rel is-alias a1 a2)
(defn some-animal [q name]
  "Search for named animal. If name is a alias, match the real one."
  (conde
    [(animal name) (== q name)]
    [(is-alias q name)]))

(defn same-animal [x y]
  "Tells if two names correspond to the same animal.
   Either or both of names might be aliases."
  (fresh [z]
         (conde
           [(is-alias x y)]
           [(is-alias y x)]
           [(is-alias z x) (is-alias z y)])))

;; Checkers?
(defn check-fact [animal fact & rest]
  (conde
    [(has-property animal fact true)]
    [(classify animal fact)]
    [(eats animal fact)]
    [(is-able animal fact)]
    [(apply is-smth animal fact rest)]
    ))
