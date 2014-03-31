(ns ai-zootson.questions-test
  (:refer-clojure :exclude [==])
  (:require [midje.sweet :refer :all]
            [ai-zootson.questions :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [clojure.tools.macro :refer :all]))

(facts parse-questions
  (tabular
    (fact answer-question
      (parse-question ?line) => ?expected)
    ?line                                               ?expected
    "Where do anteaters live?"
    [:where [:animal [:NOUN "anteater"]]]

    "How many reptiles do you know?"
    [:how-many [:animal-class "reptile"]]

    "What hairy reptiles do you know?"
    [:what [:what-kind [:ADJ "hairy"]] [:animal-class "reptile"]]

    "Mention a bat that lives in South America."
    [:mention [:what-kind [:ADJ "bat"]] [:lives-in [:NOUN "south america"]]]

    "Which animal eats worms?"
    [:which [:does-smth "eats" [:food [:NOUN "worm"]]]]

    "What kind of a tail does a lynx have?"
    [:what-kind-of [:what-kind [:ADJ "tail"]] [:animal [:NOUN "lynx"]]]

    "Which is smaller: dolphin or crayfish?"
    [:which-is [:comp "small"] [:animal1 [:NOUN "dolphin"]] [:animal2 [:NOUN "crayfish"]]]

    "Is a lobster smaller than a crayfish?"
    [:compare [:animal1 [:NOUN "lobster"]] [:comp "small"] [:animal2 [:NOUN "crayfish"]]]

    "Are girls slower than a cheetah?"
    [:compare [:animal1 [:NOUN "girl"]] [:comp "slow"] [:animal2 [:NOUN "cheetah"]]]

    "Mention an animal that is a national symbol. "
    [:mention [:is-smth [:NOUN "national symbol"]]]

    "Can aardvarks swim?"
    [:can-do [:animal [:NOUN "aardvark"]] [:verb "swim"]]

    "Which animal has ears?"
    [:which [:has-smth [:NOUN "ear"]]]

    "Do bears and dogfish have fins?"
    [:do-have [:animals [:NOUN "bear"] [:NOUN "dogfish"]] [:has-smth [:NOUN "fin"]]]

    "How many legs does a crayfish have?"
    [:how-many [:what-kind [:ADJ "legs"]] [:animal [:NOUN "crayfish"]]]

    "Is it true that deer are not poisonous?"
    [:boolean [:boolean-type "true"] [:animal [:NOUN "deer"]] [:arent [:ADJ "poisonous"]]]

    "Which animals are able to meow?"
    [:which [:are-able [:NOUN "meow"]]]

    "Is it true that elephants do not lay eggs?"
    [:boolean [:boolean-type "true"] [:animal [:NOUN "elephant"]] [:do-not [:ADJ "eggs"]]]

    "Is it false that girls cannot meow?"
    [:boolean [:boolean-type "false"] [:animal [:NOUN "girl"]] [:cannot [:ADJ "meow"]]]

    "Are worms less intelligent than a octopuses"
    [:compare [:animal1 [:NOUN "worm"]] [:less "less"] [:comp2 [:ADJ "intelligent"]] [:animal2 [:NOUN "octopus"]]]

    "Are worms more intelligent than a octopuses"
    [:compare [:animal1 [:NOUN "worm"]] [:comp2 [:ADJ "intelligent"]] [:animal2 [:NOUN "octopus"]]]

    "Which is more intelligent: dolphin or crayfish?"
    [:which-is [:comp2 [:ADJ "intelligent"]] [:animal1 [:NOUN "dolphin"]] [:animal2 [:NOUN "crayfish"]]]

    "Which is less intelligent dolphin or crayfish?"
    [:which-is [:less "less"] [:comp2 [:ADJ "intelligent"]] [:animal1 [:NOUN "dolphin"]] [:animal2 [:NOUN "crayfish"]]]

    "What kind of a tail do mongooses have?"
    [:what-kind-of [:what-kind [:ADJ "tail"]] [:animal [:NOUN "mongoose"]]]
    ))

(fact process-question
  (tabular
    (fact (process-question ?input) => ?output)
    ?input ?output

    [:where [:animal [:NOUN "anteater"]]]
    {:type :where
     :animal "anteater"}

    [:boolean [:boolean-type "true"] [:animal [:NOUN "elephant"]] [:do-not [:ADJ "eggs"]]]
    {:type :boolean
     :boolean-type true
     :animal "elephant"
     ;; lay eggs, smash eggs, eat eags... what ever
     :do-not "eggs"}


    [:do-have [:animals [:NOUN "bear"] [:NOUN "dogfish"]] [:has-smth [:NOUN "fin"]]]
    {:type :do-have
     :animals ["bear" "dogfish"]
     :has-smth "fin"}
    ))

;; (fact suck-it
;;   (suck-it {} [[:check-fact :q "bat"]
;;             [:lives-in :q "south america" true]]) =>
;;   '(run-db* {} [q]
;;          (check-fact q "bat")
;;          (lives-in q "south america" true))
;;
;;   (let [query [[:check-fact :q "bat"]
;;                [:lives-in :q "south america" true]]]
;;     (suck-it {} query) =>
;;     '(run-db* {} [q]
;;            (check-fact q "bat")
;;            (lives-in q "south america" true)))
;;   )
;;
;; (fact build-query
;;   (tabular
;;     (fact (build-query ?input) => ?output)
;;     ?input ?output
;;
;;     {:type :where
;;      :animal "anteater"}
;;     [[:lives-in "anteater" :q true]]
;;
;;     {:type :mention
;;      :what-kind "bat"
;;      :lives-in "south america"}
;;     [[:check-fact :q "bat"]
;;      [:lives-in :q "south america" true]]
;;     ))
