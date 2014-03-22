(ns ai-zootson.csv-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.core :refer :all]
            [ai-zootson.csv :refer :all]))

#_(facts "csv"
  (fact read-data
    (read-data "aardvark,1,0,0,0,0,0,0" continent-fields)
    => (contains {:name "aardvark" :lives-in {:africa true}})))
