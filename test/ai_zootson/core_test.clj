(ns ai-zootson.core-test
  (:refer-clojure :exclude [==])
  (:require [midje.sweet :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [ai-zootson.core :refer :all]
            [ai-zootson.domain :refer :all]
            ))

(fact init
  (pldb/with-db (read-files)
    ;; There should be as many animals as in test input...
    (count (run* [q] (animal q))) => 100

    ;; From zoo.data and continents.txt
    (run* [q] (lives-in "aardvark" q true)) => (just "africa")
    (count (run* [q] (classify q "reptile"))) => 5
    (run* [q] (classify q "reptile") (has-prop q "hair" true)) => empty?
    (run* [q] (has-prop "crayfish" "legs" q)) => (just 6)
    (run* [q] (has-prop "deer" "venomous" false)) =not=> empty?

    ;; From facts.txt
    ;; Examples
    (run* [q] (fresh [x] (some-animal "anteater" x) (lives-in x q true))) => (just "africa")
    (run* [q] (lives-in q "south-america" true) (is-smth q "bat")) => (just "vampire")
    (run* [q] (eats q "worm")) => (just "mongoose")
    (run* [q] (has-prop "lynx" "tail" q)) => (contains "short")

    (run* [q] (fresh [x] (is-smth-of q "national symbol" x))) => (just "kiwi")

    ;; FIXME: swim
    (run* [q] (can-animal "aardvark" "swimmer")) =not=> empty?

    (run* [q] (fresh [x] (has-prop q "ear" x))) => (just "elephant")

    (run* [q]
          (has-prop "bear" "fins" true)
          (has-prop "dogfish" "fins" true)) => empty

    (run* [q] (check-fact "deer" "poisonous" false)) =not=> empty?

    (run* [q] (is-able q "meow")) => (just "girl" "pussycat")

    (run* [q] (check-fact "elephant" "eggs" false)) =not=> empty?

    (run* [q] (is-able "girl" "meow")) =not=> empty?

    ;; Others
    (run* [q] (is-smth "aardvark" "good" "swimmer")) =not=> empty?
    (run* [q] (is-smth "cheetah" "fastest" "land animal")) =not=> empty?


    ;; own_facts.txt
    (run* [q] (conde
                [(is-more q "dolphin" "small")]
                [(is-more q "crayfish" "small")])) => (just "crayfish")

    (run* [q] (is-better "girl" "cheetah" "slow")) =not=> empty?

    )

  #_(facts "questions"
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
        ))
  )

