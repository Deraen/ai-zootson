(ns ai-zootson.sentence-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.sentence :refer :all]))

(def sample-facts [{:subject "anteater" :facts [{:property "live" :value "africa"}
                                                {:property "live" :value "europe" :negate true}
                                                {:property "hair" :value true}
                                                {:property "legs" :value 5}]}
                   {:subject "africa"}
                   {:subject "europe"}])

(fact subjects-set
  (subjects-set sample-facts) => #{"anteater" "africa" "europe"})

(fact facts-set
  (facts-set sample-facts) => #{"live" "hair" "legs"})

(fact find-subjects
  (find-subjects sample-facts {:words #{"do" "anteaters" "live"} :q "where"}) => {:words #{"do" "live"} :q "where" :subjects #{"anteater"}})

(fact find-facts
  (find-facts sample-facts {:words #{"do" "live"} :q "where" :subjects #{"anteater"}}) => {:words #{"do"} :q "where" :subjects #{"anteater"} :facts #{"live"}})

(fact parse-line
  (parse-line "Where do anteaters live?") => #{"where" "do" "anteaters" "live"}
  (parse-line "Anteater is a synonym for aardvark.") => #{"anteater" "is" "a" "synonym" "for" "aardvark"})
