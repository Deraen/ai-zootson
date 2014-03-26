(ns ai-zootson.facts
  (:refer-clojure :exclude [==])
  (:require [instaparse.core :as insta]
            [ai-zootson.sentence :as sentence]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.dcg :refer [def-->e]]
            [clojure.core.logic.pldb :as pldb]))

(def fact-language
  (insta/parser (clojure.java.io/resource "facts.bnf")))

(defn parse-fact [data fact-str]
  (->> fact-str
       sentence/parse-line
       ((fn [words] {:words words}))
       (sentence/find-subjects data)
       (sentence/find-facts data)))

(defn read-fact [data fact-str]
  nil)

(defn read-facts [data]
  nil)

(defn strip-special-chars [line]
  "Strip sentence terminators, leave commas and colons alone."
  (clojure.string/replace line #"[\!\.\?]" ""))

;; (pldb/db-rel word w)
(pldb/db-rel is-noun word)
(pldb/db-rel is-verb word)

(def determiners #{;; http://en.wikipedia.org/wiki/English_determiners
                   ;; Definite determiners
                   "the" ;; Article
                   "this" "that"
                   ;; "my" "your" "his" "her" "its" "our" "their" "whose" ; Possessives
                   ;; "which" "what" ; Interrogatives
                   ;; Indefinite
                   "a" "an"
                   "some"
                   "any"
                   ;; Quantifiers
                   "both"
                   "much" "many" "more" "most"
                   "little" "few" "less" "fewer" "least" "fewest"
                   })
(def-->e det [d]
  ([[:det ?d]] [?d] (!dcg (project [?d] (== (contains? determiners ?d) true)))))

(def conjunctions #{
                    "as" "because" "for"
                    "and" "or" "nor"
                    "but" "yet" "so"
                    })
(def-->e conjuction [c]
  ([[:conj ?c]] [?c] (!dcg (project [?c] (== (contains? conjunctions ?c) true)))))

(def nouns #{"alias" "synonym"})
(def-->e noun [n]
  ([[:noun ?n]] [?n] (!dcg (is-noun ?n)))
  ([[:noun ?n]] [?n] (!dcg (project [?n] (== (contains? nouns ?n) true))))
  ([[?n1 ?c ?n2]] (noun ?n1) (conjuction ?c) (noun ?n2))
  )

(def-->e verb [v]
  ([[:verb ?v]] [?v] (!dcg (is-verb ?v)))
  )

(def adjectives #{"fast" "slow"})
(def-->e adjective [a]
  ([[:noun ?a]] [?a] (!dcg (project [?a] (== (contains? adjectives ?a) true))))
  )

(def prepositions #{
                    "of" "like" "for" "in" "by" "but"
                    "with"
                    })
(def-->e prep [p]
  ([[:prep ?p]] [?p] (!dcg (project [?p] (== (contains? prepositions ?p) true))))
  )

(def-->e noun-pharse [n]
  ([[:np ?n]] (noun ?n))
  ([[:np ?d ?n]] (det ?d) (noun ?n))
  )

(def-->e verb-pharse [n]
  ([[:vp ?v ?np]] (verb ?v) (noun-pharse ?np))
  )

(def-->e sentence [s]
  ([[:s ?np ?vp]] (noun-pharse ?np) (verb-pharse ?vp))
  )

(defn parse-foo [words]
  (let [words (-> words
                  clojure.string/lower-case
                  strip-special-chars
                  (clojure.string/split #"\s"))]
    (run* [tree] (sentence tree words []))))
