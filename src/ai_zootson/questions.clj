(ns ai-zootson.questions
  (:require [slingshot.slingshot :refer [throw+]]
            [ai-zootson.util :refer :all]
            [ai-zootson.sentence :as sentence]))

#_(def question-language
  (insta/parser (clojure.java.io/resource "questions.bnf")))

(def questions {:locations {:qwords #{"where"}
                            :answers (fn [facts]
                                       (cond
                                         (not (empty? facts)) (clojure.string/capitalize (clojure.string/join "," (filter #(not (:not %)) facts)))
                                         :else "nowhere"))}
                :boolean {:qwords #{}
                          :answers (fn [facts]
                                     (empty? facts))}
                :general {:qwords #{"what"}
                          :answers (fn [facts]
                                     (clojure.string/join "," facts))}
                :numbers {:qwords #{"how"}
                          :words #{"many"}
                          :answers (fn [facts]
                                     (str (first facts)))}})


(def qwords-set (->> questions
                     vals
                     (map :qwords)
                     (apply concat)
                     set))

(defn find-qword [{:keys [words] :as input}]
  (let [found-qwords (clojure.set/intersection words qwords-set)
        qword (cond
                (empty? found-qwords) (throw+ {:message "Couldn't find a question word"})
                (> (count found-qwords) 1) (throw+ {:message "Found multiple qestion words??" :qwords found-qwords})
                :else (first found-qwords))]
    (-> input
        (assoc :q qword)
        (update-in [:words] disj qword))))

(defn create-input [line]
  {:words line})

(defn parse-question [data question]
  (->> question
       sentence/parse-line
       create-input
       find-qword
       (sentence/find-subjects data)
       (sentence/find-facts data)))

(defn get-fact-values [data {:keys [subjects facts] :as parsed-q}]
  (->> data
       (filter (fn [{:keys [subject]}] (contains? subjects subject)))
       (map :facts)
       (apply concat)
       (filter (fn [{:keys [property]}] (contains? facts property)))
       (map (fn [{:keys [value negate]}]
              (if-not negate
                value
                {:not value})))))

(defn format-answer [data {:keys [q] :as parsed-question} fact-values]
  (let [qdata (first (filter (fn [{:keys [qwords]}] (contains? qwords q)) (vals questions)))]
    (if qdata
      ((:answers qdata) fact-values)
      "no idea")))

(defn answer-question [data question]
  (let [parsed (parse-question data question)
        fact-values (get-fact-values data parsed)]
    (format-answer data parsed fact-values)))
