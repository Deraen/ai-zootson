(ns ai-zootson.facts
  (:require [instaparse.core :as insta]))

(def fact-language
  (insta/parser (clojure.java.io/resource "facts.bnf")))

(def skip-words #{"a" "an" "the"})

(defn filter-words [words]
  (filter #(not (contains? skip-words %)) words))

(defn read-fact [fact-str]
  (reduce
    (fn [out part]
      (assoc out
             (first part)
             (-> (rest part)
                 filter-words)))
    {} (fact-language (clojure.string/lower-case fact-str))))

(defn read-facts [data]
  nil)
