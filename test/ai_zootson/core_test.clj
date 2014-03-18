(ns ai-zootson.core-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.core :refer :all]))

(fact answer-question
  (answer-question [{:subject "anteater" :live "Africa"}] "Where do anteaters live?") => "Africa")
