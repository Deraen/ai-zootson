(ns ai-zootson.core-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.core :refer :all]
            [ai-zootson.questions :refer :all]))

(fact merge-fields
  (merge-fields "foo" "abc") => "foo"
  (merge-fields [1] [2]) => [1 2])

(fact merge-facts
  (merge-facts [{:subject "aardvark" :facts [{:property "legs" :value 5}]}]
               [{:subject "aardvark" :facts [{:property "type" :value 3}]}
                {:subject "shark" :facts [{:property "aquatic" :value true}]}]) =>
  [{:subject "aardvark" :facts [{:property "legs"
                                 :value 5}
                                {:property "type"
                                 :value 3}]}
   {:subject "shark" :facts [{:property "aquatic" :value true}]}])

(fact init
  (init)
  (let [[{:keys [facts]} & _ :as aardvark-facts] (filter #(= (:subject %) "aardvark") @ai-facts)]
    (count aardvark-facts) => 1
    facts => (contains [{:property "hair" :value true}
                        {:property "live" :value "africa"}]))

  (parse-question @ai-facts "How many reptiles do you know?") => (contains {:qtype :numeric :q #{"how" "many"}
                                                                            :facts #{"reptile"} :subjects #{}})

  (let [parsed (parse-question @ai-facts "What hairy reptiles do you know?")]
    parsed => (contains {:qtype :general :q #{"what"}
                         :facts #{"hair" "reptile"}}))

  (facts "questions"
    (tabular
      (fact answer-question
        (answer-question @ai-facts ?line) => ?expected)
      ?line                                               ?expected
      "Where do aardvarks live?"                          "Africa"

      ;; Sample q
      ;; "Where do anteaters live?"                          "Africa"
      "How many reptiles do you know?"                    "5"
      "What hairy reptiles do you know?"               "none"
      ;; "Mention a bat that lives in South America."     "vampire"
      ;; "Which animal eats worms?"                       "mongoose"
      ;; "What kind of a tail does a lynx have?"          "short"
      ;; "Which is smaller: dolphin or crayfish?"         "crayfish"
      ;; "Is a lobster smaller than a crayfish?"          "no"
      ;; "Are girls slower than a cheetah?"               "yes"
      ;; "Mention an animal that is a national symbol. "  "kiwi"
      ;; "Can aardvarks swim?"                            "yes"
      ;; "Which animal has ears?"                         "elephant"
      ;; "Do bears and dogfish have fins?"                "no"
      ;; "How many legs does a crayfish have?"            "6"
      ;; "Is it true that deer are not poisonous?"        "yes"
      ;; "Which animals are able to meow?"                "girl, pussycat"
      ;; "Is it true that elephants do not lay eggs?"     "yes"
      ;; "Is it false that girls cannot meow?"            "yes"
      )))

