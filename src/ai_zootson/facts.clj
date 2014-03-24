(ns ai-zootson.facts
  (:refer-clojure :exclude [==])
  (:require [instaparse.core :as insta]
            [ai-zootson.sentence :as sentence]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.dcg :refer [def-->e]]))

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

(def-->e subjects [v]
  ([[:subject "Lion"]] '[lion]))

(def-->e sentence [s]
  ([[:s s]] s))

(defn parse-foo [words]
  (run* [tree]
        (sentence tree words [])))
