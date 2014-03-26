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
(pldb/db-rel has-property animal property value)
;; (pldb/db-rel eats animal food)
;; (pldb/db-rel is-able-to thing)
(pldb/db-rel national-symbol a c)


;; Superlatives
(pldb/db-rel is-faster a1 a2)
(defn is-slower [x y]
  "Complement of is-faster"
  (is-faster y x))

;; Alias
(pldb/db-rel is-alias a1 a2)
(defn some-animal [q name]
  "Search for named animal. If name is a alias, match the real one."
  (conde
    [(animal name) (== q name)]
    [(is-alias q name)]))

(defn add-alias [db target alias-name]
  "Add is-alias fact but makes sure target is a real animal insteaf of another alias.
   So instead of alias2 -> alias1 -> animal we get alias2 -> animal & alias1 -> animal"
  (if-let [[target] (run* [q]
                          (some-animal q target))]
    (-> db
        (pldb/db-fact is-alias target alias-name))
    db))

(defn same-animal [x y]
  "Tells if two names correspond to the same animal.
   Either or both of names might be aliases."
  (fresh [z]
         (conde
           [(is-alias x y)]
           [(is-alias y x)]
           [(is-alias z x) (is-alias z y)])))

;; Checkers?
(defn check-fact [animal fact]
  (conde
    [(has-property animal fact true)]
    [(classify animal fact)]
    ;; [(eats animal fact)]
    ;; [(is-able-to animal fact)]
    ))
