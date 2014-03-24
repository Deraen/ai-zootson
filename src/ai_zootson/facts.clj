(ns ai-zootson.facts
  (:require [ai-zootson.sentence :as sentence]))

#_(def fact-language
  (insta/parser (clojure.java.io/resource "facts.bnf")))

(defn filter-words [words]
  (filter #(not (contains? skip-words %)) words))

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
