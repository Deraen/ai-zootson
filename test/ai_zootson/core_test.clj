(ns ai-zootson.core-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.core :refer :all]))

(facts "read"
  (fact "read-data"
    (read-data "aardvark,1,0,0,0,0,0,0" continent-fields)
    => (contains {:name "aardvark" :africa true}))

  (fact read-fact
    (read-fact "Aardvark is a good swimmer")
    => {:name "aardvark" :good-swimmer true}))

