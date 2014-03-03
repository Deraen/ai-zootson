(ns ai-zootson.facts-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.facts :refer :all]))

(facts "read"
  (fact read-fact
    (read-fact "Aardvark is a good swimmer")
    => {:name "aardvark" :good-swimmer true}))

