(ns ai-zootson.facts-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.facts :refer :all]))

(def sample-facts [{:subject "aardvark" :facts [{:property "mammal" :value true}]}])

#_(fact parse-fact
  (parse-fact sample-facts "Aardvark is a good swimmer")
  => {:subject "aardvark" :facts #{"swim"}})

(fact parse-foo
  (parse-foo "Where do lions live?") => [])
