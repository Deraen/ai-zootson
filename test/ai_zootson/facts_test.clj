(ns ai-zootson.facts-test
  (:refer-clojure :exclude [==])
  (:require [midje.sweet :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [ai-zootson.facts :refer :all]))

(fact parse-fact
  (tabular
    (fact (parse-fact ?line) => ?expected)
  ?line ?expected
  "Anteater is a synonym for aardvark."
  [[:SUBJECT "anteater"] [:VERB "is"] [:OBJECT "a" "synonym" "for" "aardvark"]]

  "Aardvark is a good swimmer."
  [[:SUBJECT "aardvark"] [:VERB "is"] [:OBJECT "a" "good" "swimmer"]]

  "Cheetah is the fastest land animal."
  [[:SUBJECT "cheetah"] [:VERB "is"] [:OBJECT "the" "fastest" "land" "animal"]]

  "Crayfish are smaller than lobsters."
  [[:SUBJECT "crayfish"] [:VERB "are"] [:OBJECT "smaller" "than" "lobsters"]]

  "Pussycats exist."
  [[:SUBJECT "pussycats"] [:VERB "exist"]]

  "An elephants has a trunk."
  [[:SUBJECT "elephants"] [:VERB "has"] [:OBJECT "a" "trunk"]]

  "Elephants have big ears."
  [[:SUBJECT "elephants"] [:VERB "have"] [:OBJECT "big" "ears"]]

  ;; "Male African elephant is the largest living terrestrial animal."
  ;; [[:SUBJECT "male" "african" "elephant"] [:VERB "is"] [:OBJECT "the" "largest" "living" "terrestial" "animal"]]

  ;; "Eastern lowland gorilla is the largest living primate."

  "Kiwis are nocturnal birds."
  [[:SUBJECT "kiwis"] [:VERB "are"] [:OBJECT "nocturnal" "birds"]]

  "The kiwi is a national symbol of New Zealand."
  [[:SUBJECT "kiwi"] [:VERB "is"] [:OBJECT "a" "national" "symbol" "of" "new" "zealand"]]

  ;; "Ladybird is also known as ladybug."
  ;; [[:SUBJECT "ladybird"] [:VERB "is"]

  "Lynx have short tails."
  [[:SUBJECT "lynx"] [:VERB "have"] [:OBJECT "short" "tails"]]

  ;; "Mongooses feed on insects, worms, snakes, and birds, etc."
  ;; [[:SUBJECT "mongooses"] [:VERB "feed"] [:OBJECT "on"]]

  "Octopuses are intelligent."
  [[:SUBJECT "octopuses"] [:VERB "are"] [:OBJECT "intelligent"]]

  ;; "Octopuses are more intelligent than worms."
  ;; [[:SUBJECT "octopuses"] [:VERB "are"] [:OBJECT "more" "intelligent" "than" "worms"]]

  "Vampires and fruitbats are bats."
  [[:SUBJECT "vampires" [:SUBJECT "fruitbats"]] [:VERB "are"] [:OBJECT "bats"]]

  "Pussycats and girls can meow."
  [[:SUBJECT "pussycats" [:SUBJECT "girls"]] [:VERB "can"] [:OBJECT "meow"]]
  ))
