(ns ai-zootson.domain
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]))

(pldb/db-rel animal fact)
;; (pldb/db-rel continent fact)
;; (pldb/db-rel rank fact)

(def animal-classes {1 "mammal"
                     2 "bird"
                     3 "reptile"
                     4 "fish"
                     5 "amphibian"
                     6 "insect"
                     7 "invertebrate"})
(pldb/db-rel classify animal rank)

(pldb/db-rel lives-in animal continent value)
(pldb/db-rel has-prop animal property value)
(pldb/db-rel has-prop animal property)
(pldb/db-rel eats animal food)
(pldb/db-rel is-able animal thing)
(pldb/db-rel is-smth animal thing)
(pldb/db-rel is-smth-of animal thing adj)

;; Superlatives
(pldb/db-rel is-more a1 a2 prop)
(defn is-less [x y prop]
  (is-more y x prop))

(def adjectives {"slow" {:prop "speed" :oppposite "fast"}
                 "fast" {:prop "speed" :opposite "slow"}
                 "small" {:prop "size" :opposite "big"}
                 "big" {:prop "size" :opposite "small"}})

(def opposites (into {} (map (fn [[k v]]
                               [k (:opposite v)])
                             adjectives)))

(defn is-better [a1 a2 prop]
  (let [reverse (contains? opposites prop)
        prop (get opposites prop prop)]
    (if reverse
      (is-less a1 a2 prop)
      (is-more a1 a2 prop))))

;; Alias
(pldb/db-rel is-alias a1 a2)
(defn some-animal [q name]
  "Search for named animal. If name is a alias, match the real one."
  (conde
    [(animal name) (== q name)]
    [(is-alias q name)]))

(defn real-animal [db name]
  (pldb/with-db db
    (or (first (run* [q]
                     (some-animal q animal)))
        name)))

(defn same-animal [x y]
  "Tells if two names correspond to the same animal.
   Either or both of names might be aliases."
  (fresh [z]
         (conde
           [(is-alias x y)]
           [(is-alias y x)]
           [(is-alias z x) (is-alias z y)])))

;; Checkers?
(def prop-aliases {"poisonous" "venomous"})

(defn check-fact [animal fact & rest]
  (let [fact (get prop-aliases fact fact)]
    (conde
      [(apply has-prop animal fact rest)]
      [(classify animal fact)]
      [(eats animal fact)]
      [(is-able animal fact)]
      [(apply is-smth animal fact rest)]
      )))

(defn can-animal [animal thing]
  (conde
    [(is-able animal thing)]
    [(fresh [x] (is-smth animal x thing))]))
