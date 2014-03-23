(ns ai-zootson.util-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.util :refer :all]))

(fact unpluralize
  (unpluralize "anteaters") => "anteater"
  (unpluralize "anteater") => "anteater")

(fact pluralize
  (pluralize "anteater") => "anteaters"
  (pluralize "anteaters") => "anteaters")

(fact with-singular
  (with-singular #{"anteaters" "do"}) => #{"anteaters" "anteater" "do"}
  (with-singular #{"do"}) => #{"do"}
  (with-singular #{}) => #{})

(fact with-plural
  (with-plural #{"anteater" "do"}) => #{"anteaters" "anteater" "do" "dos"})
