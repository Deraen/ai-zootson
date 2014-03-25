(ns ai-zootson.questions-test
  (:refer-clojure :exclude [==])
  (:require [midje.sweet :refer :all]
            [ai-zootson.questions :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]))

(fact is-location-q?
  (is-location-q? #{"where" "foo"}) => #{"where"}
  (is-location-q? #{"foo" "bar"}) => nil)

(fact find-qword
  (find-qwords {:words #{"where" "do" "anteaters" "live"}}) => {:qtype :location :words #{"do" "anteaters" "live"} :q #{"where"}}
  (find-qwords {:words #{"how" "many" "reptiles" "do" "you" "know"}}) => {:qtype :numeric :q #{"how" "many"} :words #{"reptiles" "do" "you" "know"}}
  (find-qwords {:words #{"is" "it" "true" "that" "elephants" "do" "not" "lay" "eggs"}}) => (contains {:qtype :boolean :q #{"is" "it" "true"}}))

(def sample-facts [{:subject "anteater" :facts [{:property "live" :value "africa"}
                                                {:property "live" :value "europe" :not true}
                                                {:property "hair" :value true}
                                                {:property "legs" :value 5}]}
                   {:subject "africa"}
                   {:subject "europe"}])

(fact parse-question
  (parse-question sample-facts "How many legs does anteater have?") => {:q #{"how" "many"} :qtype :numeric :subjects #{"anteater"} :facts #{"legs"} :words #{"does" "have"}})

(fact get-fact-values
  (get-fact-values sample-facts {:q #{"where"} :subjects #{"anteater"} :facts #{"live"} :words #{"do"}}) => ["africa" {:not "europe"}])

(fact answer-question
  (answer-question sample-facts "Where do anteaters live?") => "Africa"
  (answer-question sample-facts "How many legs does an anteater have?") => "5")


(pldb/db-rel animal fact)
(pldb/db-rel continent fact)
(pldb/db-rel rank fact)

(pldb/db-rel classify animal rank)
(pldb/db-rel lives-in animal continent)
(pldb/db-rel has-prop animal property)
(pldb/db-rel eats animal food)
(pldb/db-rel is-able-to thing)

(pldb/db-rel is-alias a1 a2)
(pldb/db-rel is-faster a1 a2)

(defn is-slower [a1 a2]
  (is-faster a2 a1))

(defn some-animal [q y]
  (conde
    [(animal y) (== q y)]
    [(is-alias q y)]
    ;; [(fresh [singular]
    ;;         (== singular (subs 0 (- (count y))))
    ;;         (some-animal q singular))]
    ))

(defn check-fact [animal fact]
  (conde
    [(has-prop animal fact)]
    [(classify animal fact)]
    [(eats animal fact)]
    [(is-able-to animal fact)]))

(def test-facts (pldb/db
                  [animal "Lion"]
                  [animal "Pigeon"]

                  [animal "Snake"]
                  [rank "Reptile"]
                  [classify "Snake" "Reptile"]
                  [has-prop "Snake" "Hair"]

                  [animal "Fish"]
                  [is-able-to "Fish" "Swim"]
                  [eats "Fish" "Plankton"]

                  [not [is-able-to "Lion" "Swim"]]

                  [continent "Africa"]
                  [continent "Europe"]

                  [is-faster "Pigeon" "Lion"]

                  [is-alias "Lion" "Leijona"]

                  [lives-in "Lion" "Africa"]
                  [lives-in "Pigeon" "Europe"]))

(fact core.logic
  (pldb/with-db test-facts
    (run* [q] (lives-in q "Africa")) => (just "Lion")
    (run* [q] (is-faster q "Lion")) => (just "Pigeon")
    (run* [q] (is-alias q "Leijona")) => (just "Lion")
    (run* [q] (is-alias q "Lion")) => empty?
    (run* [q] (is-slower q "Pigeon")) => (just "Lion")
    (run* [q] (== q "Lion")) => (just "Lion")
    (run* [q] (some-animal q "Lion")) => (just "Lion")
    (run* [q] (some-animal q "Leijona")) => (just "Lion")
    ;; (run* [q] (some-animal q "Lions")) => (just "Lion")
    (run* [q]
          (some-animal q "Leijona")
          (lives-in q "Africa")) => (just "Lion")
    (run* [q]
          (check-fact q "Reptile")
          (check-fact q "Hair")) => (just "Snake")
    (run* [q]
          (check-fact q "Plankton")
          (check-fact q "Swim")) => (just "Fish")
    ))
