(ns ai-zootson.questions
  (:require [instaparse.core :as insta]))

(def question-language
  (insta/parser (clojure.java.io/resource "questions.bnf")))

(defn strip-special-chars [line]
  (clojure.string/replace line #"[\!\.\?]" ""))

(defn contractions [line]
  line)

(defn parse-question [line]
  (-> line
      clojure.string/lower-case
      strip-special-chars
      contractions
      (clojure.string/split #"\s")))
