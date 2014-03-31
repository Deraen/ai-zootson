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
    [:how-many [:what-kind [:ADJ "reptiles"]]]

    "What hairy reptiles do you know?"
    [:what [:what-kind [:ADJ "hairy"]] [:animal-class "reptile"]]

    "Mention a bat that lives in South America."
    [:mention [:what-kind [:ADJ "bat"]] [:lives-in [:NOUN "south america"]]]

    "Which animal eats worms?"
    [:which [:does-smth "eats" [:food [:NOUN "worm"]]]]

    "What kind of a tail does a lynx have?"
    [:what-kind-of [:what-kind [:ADJ "tail"]] [:animal [:NOUN "lynx"]]]

;;     "Which is smaller: dolphin or crayfish?"
;;     []
;;
;;     "Is a lobster smaller than a crayfish?"
;;     []
;;
;;     "Are girls slower than a cheetah?"
;;     []
;;
;;     "Mention an animal that is a national symbol. "
;;     []
;;
;;     "Can aardvarks swim?"
;;     []
;;
;;     "Which animal has ears?"
;;     []
;;
;;     "Do bears and dogfish have fins?"
;;     [:do-have [:]]

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
     :boolean-type "true"
     :animal "elephant"
     ;; lay eggs, smash eggs, eat eags... what ever
     :do-not "eggs"}
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
