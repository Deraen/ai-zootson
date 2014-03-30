(ns ai-zootson.sentence
  (:require [slingshot.slingshot :refer [throw+]]
            [ai-zootson.util :refer :all]))

(defn fix-words [parsed]
  (map (fn [i]
         (if (sequential? i)
           (cond
             (#{:NOUN :ADJ} (get i 0)) [(get i 0) (clojure.string/join "" (rest i))]
             :else (fix-words i))
           i))
       parsed))

(defn get-parsed [parsed]
  (if (sequential? parsed)
    (first parsed)
    (throw+ parsed)))
