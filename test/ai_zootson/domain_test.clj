(ns ai-zootson.domain-test
  (:refer-clojure :exclude [==])
  (:require [midje.sweet :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [ai-zootson.domain :refer :all]))

#_(fact core.logic
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

(facts alias
  (let [test-data (pldb/db
                    [animal "Lion"]
                    [is-alias "Lion" "Leijona"])]
    (pldb/with-db test-data
      (run* [q] (== q "Lion")) => (just "Lion")
      (fact is-alias
        (run* [q] (some-animal q "Leijona")) => (just "Lion"))
      (fact add-alias
        (let [test-data (-> test-data
                            (add-alias "Leijona" "Jellona"))]
          (pldb/with-db test-data
            (run* [q] (some-animal q "Jellona")) => (just "Lion")
            (run* [q] (same-animal "Lion" "Jellona")) =not=> empty?
            (run* [q] (same-animal "Jellona" "Leijona")) =not=> empty?
            (run* [q] (same-animal "Lion" "FooBar")) => empty?
            )))
      )))
