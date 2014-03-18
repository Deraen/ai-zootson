(ns ai-zootson.questions-test
  (:require [midje.sweet :refer :all]
            [ai-zootson.questions :refer :all]))

(facts "questions"
  (tabular
    (fact parse-question
      (parse-question ?line) => ?expected)
    ?line                                            ?expected
    "Where do anteaters live?"                       {}
    "How many reptiles do you know?"                 {}
    "What hairy reptiles do you know?"               {}
    "Mention a bat that lives in South America."     {}
    "Which animal eats worms?"                       {}
    "What kind of a tail does a lynx have?"          {}
    "Which is smaller: dolphin or crayfish?"         {}
    "Is a lobster smaller than a crayfish?"          {}
    "Are girls slower than a cheetah?"               {}
    "Mention an animal that is a national symbol. "  {}
    "Can aardvarks swim?"                            {}
    "Which animal has ears?"                         {}
    "Do bears and dogfish have fins?"                {}
    "How many legs does a crayfish have?"            {}
    "Is it true that deer are not poisonous?"        {}
    "Which animals are able to meow?"                {:type :which :object "animals" :subject "meow"}
    "Is it true that elephants do not lay eggs?"     {:type :is-it-true :object "elephants" :subject "do not lay eggs"}
    "Is it false that girls cannot meow?"            {:type :is-it-false :object "girls" :subject "cannot meow"}))
