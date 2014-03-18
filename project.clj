(defproject ai-zootson "0.0.1-SNAPSHOT"
  :description "MAT-75006 AI Course, programming excercise"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.csv "0.1.2"]
                 [instaparse "1.2.16"]
                 [slingshot "0.10.3"]]
  :profiles {:dev {:dependencies [[midje "1.6.2"]]}})
