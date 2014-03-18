(ns ai-zootson.core
  (:require [slingshot.slingshot :refer [throw+]]
            [ai-zootson.csv :refer :all]
            [ai-zootson.questions :refer :all]
            [ai-zootson.facts :refer [read-facts]]))

(def ai-facts (atom nil))

(defn read-file [file-name f & rest]
  (with-open [rdr (clojure.java.io/reader file-name)]
    (apply f rdr rest)))

(def data-fields [(str-field :name)
                  :hair
                  :feathers
                  :eggs
                  :milk
                  :airborne
                  :aquatic
                  :predator
                  :toothed
                  :backbone
                  :breathes
                  :venomous
                  :fins
                  (numeric-field :legs)
                  :tail
                  :domestic
                  :catsize
                  (numeric-field :type {:min 1 :max 7})])

(def continent-fields [(str-field :name)
                       :africa
                       :europe
                       :asia
                       :north-america
                       :south-america
                       :australia
                       :antarctica])

(def q-words #{"where" "what"})

(defn find-q-word [words]
  (let [found-q-words (clojure.set/intersection words q-words)]
    (println found-q-words)
    (cond
      (empty? found-q-words) (throw+ "Couldn't find a question word")
      (> (count found-q-words) 1)  (throw+ "Found multiple qestion words??")
      :else  (first found-q-words))))

(defn find-subjects [data words]
  (let [subjects (set (map :subject data))
        with-singular (set (concat words (filter (partial not= nil) (map (fn [word]
                                                                           (if (= (last word) \s)
                                                                             (subs word 0 (- (count word) 1)))) words))))]
    with-singular))

(defn answer-question [data question]
  (let [q (parse-question question)
        words (set q)
        q-word (find-q-word words)
        subject (find-subjects data (disj words q-word))]
    (if subject
      subject
      "no idea")))

(defn init []
  (reset! ai-facts (concat
                     (read-file "zoo.data" read-data data-fields)
                     (read-file "continents.txt" read-data continent-fields)
                     (read-file "facts.txt" read-facts))))
