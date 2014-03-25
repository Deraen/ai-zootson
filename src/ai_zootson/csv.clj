(ns ai-zootson.csv
  (:require [clojure.data.csv :as csv]))

(defn read-data [db data fun]
  "Read csv data (vector of vectors)"
  (reduce (fn [db line]
            (fun db line))
          db (csv/read-csv data)))
