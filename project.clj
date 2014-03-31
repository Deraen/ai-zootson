(defproject ai-zootson "0.0.1-SNAPSHOT"
  :description "MAT-75006 AI Course, programming excercise"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.logic "0.8.7"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/core.match "0.2.1"]
                 [org.clojure/tools.macro "0.1.2"]
                 [instaparse "1.2.16"]
                 [slingshot "0.10.3"]]
  :main ai-zootson.core
  :aot [ai-zootson.core]
  :plugins [[lein-midje "3.1.3"]]
  :profiles {:dev {:dependencies [[midje "1.6.2"]]}})
