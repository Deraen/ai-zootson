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
(pldb/db-rel lives-in animal continent)

(pldb/db-rel is-faster a1 a2)

(defn is-slower [a1 a2]
  (is-faster a2 a1))

(defn is-alias [x y]
  [x y])

(def test-facts (pldb/db
                  [animal "Lion"]
                  [animal "Pidgeon"]

                  [continent "Africa"]
                  [continent "Europe"]

                  [is-alias "Lion" "Leijona"]

                  [is-faster "Pidgeon" "Lion"]

                  [lives-in "Lion" "Africa"]
                  [lives-in "Pidgeon" "Europe"]))

(fact core.logic
  (pldb/with-db test-facts
    (run* [q]
          (lives-in q "Africa")) => (just "Lion")
    (run* [q]
          (is-faster q "Lion")) => (just "Pidgeon")
    (not (empty? (run* [q]
                       (animal "Leijona")
                       (lives-in q "Africa")))) => true))
