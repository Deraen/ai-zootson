(ns ai-zootson.csv
  (:require [clojure.data.csv :as csv]))

;; Fields
(defn field [prop valuefn]
  (fn [acc value]
    (assoc acc prop (valuefn value))))

(defn fact-field [property valuefn & [realvalue]]
  (fn [acc value]
    (let [value (valuefn value)
          fact {:property property
                :value (if realvalue realvalue value)}
          fact (if-not value
                 (assoc fact :not true)
                 fact)]
      (update-in acc [:facts] conj fact))))

(defn numeric-field [value]
  (Integer/parseInt value))

(defn type-field [typemap]
  (fn [acc value]
    (let [value (numeric-field value)
          fact {:property (typemap value)
                :value true}]
      (update-in acc [:facts] conj fact))))

(defn boolean-field [value]
  (= value "1"))

;; Read
(defn read-field [acc [fun input]]
  "Read a field using given function. If keyword is given instead of
   function, new function is created using `boolean-field`"
  (fun acc input))

(defn read-data [data fields]
  "Read csv data (vector of vectors)"
  (vec (map (fn [line]
              (reduce read-field {} (zipmap fields line)))
            (csv/read-csv data))))
