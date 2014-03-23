(ns ai-zootson.csv-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.core :refer :all]
            [ai-zootson.csv :refer :all]))

(fact boolean-field
  (boolean-field "1") => true)

(fact numeric-field
  (numeric-field "1") => 1)

(fact field
  ((field :subject str) {} "aardvark") => {:subject "aardvark"})

(fact fact-field
  ((fact-field "live" boolean-field "africa") {} "1") => {:facts [{:property "live"
                                                                   :value "africa"}]}
  ((fact-field "live" boolean-field "africa") {} "0") => {:facts [{:property "live"
                                                                   :value "africa"
                                                                   :not true}]}
  ((fact-field "legs" numeric-field) {} "5") => {:facts [{:property "legs"
                                                          :value 5}]})

(def sample-fields [(field :subject str)
                    (fact-field "live" boolean-field "africa")
                    (fact-field "live" boolean-field "europe")
                    (fact-field "legs" numeric-field)])

(fact read-data
  (read-data "aardvark,1,0" sample-fields) => (contains {:subject "aardvark" :facts [{:property "live"
                                                                                      :value "africa"}
                                                                                     {:property "live"
                                                                                      :value "europe"
                                                                                      :not true}]}))
