(ns ai-zootson.core
  (:require [slingshot.slingshot :refer [throw+]]
            [ai-zootson.csv :refer :all]
            [ai-zootson.questions :refer :all]
            [ai-zootson.util :refer :all]
            [ai-zootson.facts :refer [read-facts]]))

(def ai-facts (atom nil))

(defn read-file [file-name f & rest]
  (with-open [rdr (clojure.java.io/reader file-name)]
    (apply f rdr rest)))

(def data-fields [(field :subject str)
                  (fact-field "hair" boolean-field)
                  (fact-field "feathers" boolean-field)
                  (fact-field "eggs" boolean-field)
                  (fact-field "milk" boolean-field)
                  (fact-field "airborne" boolean-field)
                  (fact-field "aquatic" boolean-field)
                  (fact-field "predator" boolean-field)
                  (fact-field "toothed" boolean-field)
                  (fact-field "backbone" boolean-field)
                  (fact-field "breathes" boolean-field)
                  (fact-field "venomous" boolean-field)
                  (fact-field "fins" boolean-field)
                  (fact-field "legs"numeric-field)
                  (fact-field "tail" boolean-field)
                  (fact-field "domestic" boolean-field)
                  (fact-field "catsize" boolean-field)
                  (fact-field "type" numeric-field)])

(def continent-fields [(field :subject str)
                       (fact-field "live" boolean-field "africa")
                       (fact-field "live" boolean-field "europe")
                       (fact-field "live" boolean-field "asia")
                       (fact-field "live" boolean-field "north-america")
                       (fact-field "live" boolean-field "south-america")
                       (fact-field "live" boolean-field "australia")
                       (fact-field "live" boolean-field "antarctica")])

(defn merge-fields [val & rest]
  (if (sequential? val)
    (vec (apply concat val rest))
    val))

(defn merge-facts [& cols]
  (->> cols
       (apply concat)
       (group-by :subject)
       (map (fn [[k v]]
              (apply merge-with merge-fields v)))))

(defn init []
  (reset! ai-facts (merge-facts
                     (read-file "inputs/zoo.data" read-data data-fields)
                     (read-file "inputs/continents.txt" read-data continent-fields)
                     #_(read-file "inputs/facts.txt" read-facts))))
