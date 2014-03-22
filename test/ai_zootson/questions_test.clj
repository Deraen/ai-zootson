(ns ai-zootson.questions-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.questions :refer :all]))

(fact find-qword
  (find-qword {:words #{"where" "do" "anteaters" "live"}}) => {:words #{"do" "anteaters" "live"} :q "where"})

(def sample-facts [{:subject "anteater" :facts [{:property "live" :value "africa"}
                                                {:property "live" :value "europe" :negate true}
                                                {:property "hair" :value true}
                                                {:property "legs" :value 5}]}
                   {:subject "africa"}
                   {:subject "europe"}])

(facts "questions"
  (tabular
    (fact parse-question
      (parse-question sample-facts ?line) => ?expected)
    ?line                                            ?expected
    "How many legs does anteater have?"              {:q "how" :subjects #{"anteater"} :facts #{"legs"} :words #{"many" "does" "have"}}

    ;; Samples
    "Where do anteaters live?"                       {:q "where" :subjects #{"anteater"} :facts #{"live"} :words #{"do"}}
    ;; "How many reptiles do you know?"                 {}
    ;; "What hairy reptiles do you know?"               {}
    ;; "Mention a bat that lives in South America."     {}
    ;; "Which animal eats worms?"                       {}
    ;; "What kind of a tail does a lynx have?"          {}
    ;; "Which is smaller: dolphin or crayfish?"         {}
    ;; "Is a lobster smaller than a crayfish?"          {}
    ;; "Are girls slower than a cheetah?"               {}
    ;; "Mention an animal that is a national symbol. "  {}
    ;; "Can aardvarks swim?"                            {}
    ;; "Which animal has ears?"                         {}
    ;; "Do bears and dogfish have fins?"                {}
    ;; "How many legs does a crayfish have?"            {}
    ;; "Is it true that deer are not poisonous?"        {}
    ;; "Which animals are able to meow?"                {}
    ;; "Is it true that elephants do not lay eggs?"     {}
    ;; "Is it false that girls cannot meow?"            {}
    ))

(fact get-fact-values
  (get-fact-values sample-facts {:q "where" :subjects #{"anteater"} :facts #{"live"} :words #{"do"}}) => ["africa" {:not "europe"}])

(fact answer-question
  (answer-question sample-facts "Where do anteaters live?") => "Africa"
  (answer-question sample-facts "How many legs does an anteater have?") => "5")
