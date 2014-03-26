(ns ai-zootson.facts-test
  (:refer-clojure :exclude [==])
  (:require [midje.sweet :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [ai-zootson.facts :refer :all]))

(def sample-facts [{:subject "aardvark" :facts [{:property "mammal" :value true}]}])

#_(fact parse-fact
  (parse-fact sample-facts "Aardvark is a good swimmer")
  => {:subject "aardvark" :facts #{"swim"}})

(fact parse-foo
  (pldb/with-db (pldb/db
                  [is-det "a"]
                  [is-det "an"]
                  [is-noun "anteater"]
                  [is-noun "aardvark"]
                  [is-verb "is"])
    (run* [q] (is-noun q)) => '("aardvark" "anteater")
    (run* [q] (noun-pharse q ["a" "anteater"] [])) => '([:np [:det "a"] [:noun "anteater"]])
    (run* [q] (verb-pharse q ["is" "a" "aardvark"] [])) => '([:vp [:verb "is"] [:np [:det "a"] [:noun "aardvark"]]])
    (parse-foo "an anteater is an aardvark") => '([:s [:np [:det "an"] [:noun "anteater"]] [:vp [:verb "is"] [:np [:det "an"] [:noun "aardvark"]]]])
    (parse-foo "Anteater is a synonym for aardvark") => [:s [:np [:noun "anteater"]]]
    ))
