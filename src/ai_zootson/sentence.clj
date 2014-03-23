(ns ai-zootson.sentence
  (:require [slingshot.slingshot :refer [throw+]]
            [ai-zootson.util :refer :all]))

(defn strip-special-chars [line]
  (clojure.string/replace line #"[\!\.\?]" ""))

(defn parse-line [line]
  (-> line
      clojure.string/lower-case
      strip-special-chars
      ;; contractions
      (clojure.string/split #"\s")
      set))

(defn subjects-set [data]
  (set (map :subject data)))

(defn facts-set [data]
  (set (map :property (apply concat (map :facts data)))))

(defn find-subjects [data {:keys [words] :as input}]
  (let [subjects (subjects-set data)
        words-with-singular (with-singular words)
        found-subject-words (clojure.set/intersection words-with-singular subjects)
        found-with-plural (with-plural found-subject-words)]
    (-> input
        (assoc :subjects found-subject-words)
        (update-in [:words] clojure.set/difference found-with-plural))))

(defn find-facts [data {:keys [words] :as input}]
  (let [facts (facts-set data)
        found-fact-words (clojure.set/intersection words facts)]
    (-> input
        (assoc :facts found-fact-words)
        (update-in [:words] clojure.set/difference found-fact-words))))
