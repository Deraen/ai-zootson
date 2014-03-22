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

(defn init []
  (reset! ai-facts (concat
                     (read-file "zoo.data" read-data data-fields)
                     (read-file "continents.txt" read-data continent-fields)
                     (read-file "facts.txt" read-facts))))
