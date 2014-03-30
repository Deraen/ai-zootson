(ns ai-zootson.facts-test
  (:refer-clojure :exclude [==])
  (:require [midje.sweet :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [ai-zootson.facts :refer :all]
            [ai-zootson.domain :refer :all]))

(fact singularize
  (tabular
    (fact (singularize ?plural) => ?singular)
    ?plural ?singular
    "octopuses" "octopus"
    "vampires" "vampire"
    "boys" "boy"
    "girls" "girl"))

(fact parse-fact
  (tabular
    (fact (parse-fact-sentence ?line) => ?expected)
    ?line ?expected

    "Anteater is a synonym for aardvark."
    [:is-alias [:NOUN "anteater"] [:NOUN "aardvark"]]

    "Aardvark is a good swimmer."
    [:is-smth [:NOUNS [:NOUN "aardvark"]] [:ADJ "good"] [:NOUN "swimmer"]]

    "Cheetah is the fastest land animal."
    [:is-smth [:NOUNS [:NOUN "cheetah"]] [:ADJ "fastest"] [:NOUN "land animal"]]

    "Crayfish are smaller than lobsters."
    [:is-more [:NOUN "crayfish"] [:ADJ "small"] [:NOUN "lobster"]]

    "Pussycats exist."
    [:animal [:NOUNS [:NOUN "pussycat"]]]

    "An elephants has a trunk."
    [:has-prop [:NOUNS [:NOUN "elephant"]] [:NOUN "trunk"]]

    "Elephants have big ears."
    [:some-kind-prop [:NOUNS [:NOUN "elephant"]] [:ADJ "big"] [:NOUN "ear"]]

    "Male African elephant is the largest living terrestrial animal."
    [:is-smth [:NOUNS [:NOUN "male african elephant"]] [:ADJ "largest"] [:NOUN "living terrestrial animal"]]

    "Eastern lowland gorilla is the largest living primate."
    [:is-smth [:NOUNS [:NOUN "eastern lowland gorilla"]] [:ADJ "largest"] [:NOUN "living primate"]]

    "Kiwis are nocturnal birds."
    [:is-smth [:NOUNS [:NOUN "kiwi"]] [:ADJ "nocturnal"] [:NOUN "bird"]]

    "The kiwi is a national symbol of New Zealand."
    [:is-smth-of [:NOUNS [:NOUN "kiwi"]] [:NOUN "national symbol"] [:NOUN "new zealand"]]

    "Ladybird is also known as ladybug."
    [:is-alias-reverse [:NOUN "ladybird"] [:NOUN "ladybug"]]

    "Lynx have short tails."
    [:some-kind-prop [:NOUNS [:NOUN "lynx"]] [:ADJ "short"] [:NOUN "tail"]]

    "Mongooses feed on insects, worms, snakes, and birds, etc."
    [:eats [:NOUNS [:NOUN "mongoose"]] [:NOUNS [:NOUN "insect"] [:NOUN "worm"] [:NOUN "snake"] [:NOUN "bird"]]]

    "Octopuses are intelligent."
    [:is-smth [:NOUNS [:NOUN "octopus"]] [:NOUN "intelligent"]]

    "Octopuses are more intelligent than worms."
    [:is-more [:NOUN "octopus"] [:ADJ "intelligent"] [:NOUN "worm"]]

    "Vampires and fruitbats are bats."
    [:is-smth [:NOUNS [:NOUN "vampire"] [:NOUN "fruitbat"]] [:NOUN "bat"]]

    "Pussycats and girls can meow."
    [:is-able [:NOUNS [:NOUN "pussycat"] [:NOUN "girl"]] [:NOUN "meow"]]

    ;; Own facts
    "Crayfish are smaller than dolphins."
    [:is-more [:NOUN "crayfish"] [:ADJ "small"] [:NOUN "dolphin"]]

    "The cheetah are faster than girls."
    [:is-more [:NOUN "cheetah"] [:ADJ "fast"] [:NOUN "girl"]]
))

(fact expand
  (expand [:animal [:NOUNS [:NOUN "pussycat"] [:NOUN "girl"]]]) =>
  [[:animal [:NOUN "pussycat"]]
   [:animal [:NOUN "girl"]]]

  (expand [:eats [:NOUNS [:NOUN "mongoose"] [:NOUN "foo"]] [:NOUNS [:NOUN "insect"] [:NOUN "worm"]]]) =>
  (contains
    [[:eats [:NOUN "mongoose"] [:NOUN "insect"]]
     [:eats [:NOUN "mongoose"] [:NOUN "worm"]]
     [:eats [:NOUN "foo"] [:NOUN "insect"]]
     [:eats [:NOUN "foo"] [:NOUN "worm"]]]
    :in-any-order)
  )

#_(fact add-facts
  (let [db (pldb/db)
        db (add-facts db [['is-able "pussycat" "meow"] ['is-able "girl" "meow"]])]
    (pldb/with-db db
      (run* [q] (is-able q "meow")) => (just "girl" "pussycat"))))
