(ns ai-zootson.questions
  (:require [slingshot.slingshot :refer [throw+]]
            [ai-zootson.util :refer :all]
            [ai-zootson.sentence :as sentence]))

#_(def question-language
  (insta/parser (clojure.java.io/resource "questions.bnf")))

(defn some-set-contains [& col]
  (fn [words]
    "Returns either set of qwords which were found, or nil"
    (some (fn [qwords]
            (if (clojure.set/subset? qwords words)
             qwords
             nil)) col)))

(def is-location-q? (some-set-contains #{"where"}))
(def is-boolean-q? (some-set-contains #{"is" "it" "true"} #{"is" "it" "false"} #{"can"} #{"do" "have"}))
(def is-numeric-q? (some-set-contains #{"how" "many"}))
(def is-generic-q? (some-set-contains #{"what"}))

(defn format-continents [facts]
  (cond
    (not (empty? facts)) (clojure.string/capitalize (clojure.string/join "," (filter #(not (:not %)) facts)))
    :else "nowhere"))

(defn format-boolean [facts]
    (empty? facts))

(defn format-numeric [facts]
  (if (= (count facts) 1)
    (str (first facts))
    (str (count facts))))

(defn format-general [facts]
  (clojure.string/join "," facts))

(def questions {:location [is-location-q? format-continents]
                :boolean  [is-boolean-q? format-boolean]
                :numeric [is-numeric-q? format-numeric]
                :general [is-generic-q? format-general]})

(defn find-qwords [{:keys [words] :as input}]
  (let [[qtype found-qwords] (some (fn [[k [qtest _]]]
                                     (let [r (qtest words)]
                                       (if r
                                         [k r]
                                         nil)))
                                   questions)]
    (-> input
        (assoc :q found-qwords
               :qtype qtype)
        (update-in [:words] clojure.set/difference found-qwords))))

(defn create-input [line]
  {:words line})

(defn parse-question [data question]
  (->> question
       sentence/parse-line
       create-input
       find-qwords
       (sentence/find-subjects data)
       (sentence/find-facts data)))

(defn get-fact-values [data {:keys [subjects facts] :as parsed-q}]
  (->> data
       (filter (fn [{:keys [subject]}] (contains? subjects subject)))
       (map :facts)
       (apply concat)
       (filter (fn [{:keys [property]}] (contains? facts property)))
       (map (fn [{:keys [value not]}]
              (if-not not
                value
                {:not value})))))

(defn format-answer [data {:keys [q qtype] :as parsed-question} fact-values]
  (let [[_ format :as qdata] (qtype questions)]
    (if qdata
      (format fact-values)
      "no idea")))

(defn answer-question [data question]
  (let [parsed (parse-question data question)
        fact-values (get-fact-values data parsed)]
    (format-answer data parsed fact-values)))
