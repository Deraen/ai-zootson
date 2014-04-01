(ns ai-zootson.core-test
  (:refer-clojure :exclude [==])
  (:require [midje.sweet :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [ai-zootson.core :refer :all]
            [ai-zootson.domain :refer :all]
            [ai-zootson.questions :refer :all]
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
    (run* [q] (has-prop "deer" "venomous" false)) => seq?

    ;; From facts.txt
    ;; Examples
    (run* [q] (fresh [x] (some-animal "anteater" x) (lives-in x q true))) => (just "africa")
    (run* [q] (lives-in q "south america" true) (is-smth q "bat")) => (just "vampire")
    (run* [q] (do-smth q "feed" "worm")) => (just "mongoose")
    (run* [q] (has-prop "lynx" "tail" q)) => (contains "short")

    (run* [q] (fresh [x] (is-smth-of q "national symbol" x))) => (just "kiwi")

    ;; FIXME: swim
    (run* [q] (can-animal "aardvark" "swimmer")) => seq?

    (run* [q] (fresh [x] (has-prop q "ear" x))) => (just "elephant")

    (run* [q]
          (has-prop "bear" "fins" true)
          (has-prop "dogfish" "fins" true)) => empty

    (run* [q] (check-fact "deer" "poisonous" false)) => seq?

    (run* [q] (is-able q "meow")) => (just "girl" "pussycat")

    (run* [q] (check-fact "elephant" "eggs" false)) => seq?

    (run* [q] (is-able "girl" "meow")) => seq?

    ;; Others
    (run* [q] (is-smth "aardvark" "good" "swimmer")) => seq?
    (run* [q] (fresh [x] (is-smth q x "swimmer"))) => (just "aardvark")
    (run* [q] (is-smth "cheetah" "fastest" "land animal")) => seq?


    ;; own_facts.txt
    (run* [q] (conde
                [(== q "crayfish") (is-less q "dolphin" "size")]
                [(== q "dolphin") (is-less q "crayfish" "size")]))
    => (just "crayfish")

    (run* [q] (conde
                [(== q "crayfish") (is-more q "dolphin" "size")]
                [(== q "dolphin") (is-more q "crayfish" "size")]))
    => (just "dolphin")

    (run* [q] (is-less "girl" "boy" "speed")) => seq?
    (run* [q] (is-less "boy" "girl" "speed")) => seq?
    (run* [q] (is-more "girl" "boy" "speed")) => empty?
    (run* [q] (is-more "boy" "girl" "speed")) => seq?
    ))

(facts "questions"
  (let [db (-> (read-files)
               (ai-zootson.facts/read-fact "Girls and wolves can howl."))]
    (tabular
      (fact answer-question
        (answer-question db ?line) => ?expected)
      ?line ?expected
      "Where do aardvarks live?"
      "Africa"

      ;; Sample q
      "Where do anteaters live?"
      "Africa"

      "How many reptiles do you know?" "5"
      "How many mammals do you know?" "41"

      "What hairy reptiles do you know?" "none"

      ;; variant of previous where own_facts contains an answer
      ;; "What stupid reptiles do you know?" "seasnake"

      "Mention a bat that lives in South America."
      "vampire"

      "What kind of a tail does a lynx have?" "short"

      "How many legs does a crayfish have?"
      "6"

      "Is it true that deer are not poisonous?" "yes"
      "Is it true that deer are poisonous?" "no"
      "Is it false that deer are not poisonous?" "no"
      "Is it false that deer are poisonous?" "yes"

      "Which animals are able to meow?"
      "girl, pussycat"

      "Is it true that elephants do not lay eggs?" "yes"
      "Is it true that elephants do lay eggs?" "no"
      "Is it false that elephants do not lay eggs?" "no"
      "Is it false that elephants do lay eggs?" "yes"

      "Is it false that girls cannot meow?" "yes"
      "Is it true that girls cannot meow?" "no"
      "Is it false that girls can meow?" "no"
      "Is it true that girls can meow?" "yes"

      "Which is smaller: dolphin or crayfish?" "crayfish"
      "Which is smaller: crayfish or dolphin?" "crayfish"
      "Which is larger: crayfish or dolphin?" "dolphin"
      "Which is bigger: crayfish or dolphin?" "dolphin"

      "Is a lobster smaller than a crayfish?" "no"
      "Is a crayfish smaller than a lobster" "yes"
      "Is a lobster larger than a crayfish?" "yes"
      "Is a crayfish larger than a lobster" "no"
      "Is a crayfish larger than a girl" "no idea"

      ;; Cheetah is fastest animal
      "Are girls slower than a cheetah?" "yes"
      "Are cheetah faster than a girls" "yes"
      "Are cheetah faster than a worm" "yes"
      "Are worm slower than a cheetah" "yes"
      "Which is faster: cheetah or girl" "cheetah"

      "Are boys faster than a girls" "yes"
      "Are girls faster than a boys" "no"
      "Are boys slower than a girls" "no"

      "Are worms less intelligent than a octopuses" "yes"
      "Are worms more intelligent than a octopuses" "no"
      "Are worms more intelligent than a girls" "no idea"
      "Which is more intelligent: octopus or worm" "octopus"
      "Which is less intelligent worm or octopus" "worm"
      "Which is more intelligent: girl or octopus" "girl"

      "Do bears and dogfish have fins?" "no"

      "Mention an animal that is a national symbol. " "kiwi"

      "Which animal has ears?" "elephant"

      "What venomous reptiles do you know?" "seasnake, pitviper"

      "Can aardvarks swim?" "yes"

      "Which animal eats worms?" "mongoose"
      "What mongoose eats?" "no idea"

      "Where do pumas live?" "South America, North America"
      "What kind of legs does a lynx have" "no idea"
      "Which animals are able to howl" "girl, wolf"
      )))
