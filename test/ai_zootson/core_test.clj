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
    (run* [q] (classify q "reptile") (has-property q "hair" true)) => empty?
    (run* [q] (has-property "crayfish" "legs" q)) => (just 6)
    (run* [q] (has-property "deer" "venomous" false)) =not=> empty?

    ;; From facts.txt
    ;; Examples
    ;; (run* [q] (lives-in "anteaters" q true)) => (just "africa")
    (run* [q] (lives-in q "south-america" true) (is-smth q "bat")) => (just "vampire")
    ;; (run* [q] (eats q "worm")) => (just "mongoose")
    (run* [q] (is-smth "lynx" "tail" q)) => (just "short")

    ;; (run* [q] (fresh [x] (national-symbol q x))) => (just "kiwi")

    ;; (run* [q] (is-able "aardvark" "swim")) =not=> empty?

    (run* [q] (fresh [x] (is-smth q "ear" x))) => (just "elephant")

    ;; (run* [q]
    ;;       (has-property "bear" "fins" true)
    ;;       (has-property "dogfish" "fins" true)) => empty

    ;; (run* [q] (has-property "deer" "poisonous" false)) =not=> empty?

    (run* [q] (is-able q "meow")) => (just "girl" "pussycat")

    ;; (run* [q] (is-able-to "elephant" "lay-eggs" false)) =not=> empty?

    (run* [q] (is-able "girl" "meow")) =not=> empty?

    ;; Others
    (run* [q] (is-smth "aardvark" "swimmer" "good")) =not=> empty?
    (run* [q] (is-smth "cheetah" "land animal" "fastest")) =not=> empty?


    ;; own_facts.txt
    ;; (run* [q] (conde
    ;;             [(== q "crayfish") (is-smaller q "dolphin")]
    ;;             [(== q "dolphin") (is-smaller q "crayfish")])) => (just "crayfish")

    ;; (run* [q] (is-slower "girl" "cheetah")) =not=> empty?

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

