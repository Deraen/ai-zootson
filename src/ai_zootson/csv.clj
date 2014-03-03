(ns ai-zootson.csv
  (:require [clojure.data.csv :as csv]))

;; Fields
(defn boolean-field [field]
  (fn [value]
    [field (= value "1")]))

(defn str-field [field]
  (fn [value]
    [field (str value)]))

(defn numeric-field
  ([field]
   (numeric-field field {}))
  ([field opts]
   (fn [value]
     [field (int value)])))

;; Read
(defn read-field [[k v]]
  "Read a field using given function. If keyword is given instead of
   function, new function is created using `boolean-field`"
  (let [f (if (keyword? k)
            (boolean-field k)
            k)]
    (f v)))

(defn read-data [data fields]
  "Read csv data (vector of vectors)"
  (map (fn [line]
    (into {} (map read-field (zipmap fields line))))
    (csv/read-csv data)))
