(ns ai-zootson.util)

(defn unpluralize [word]
  (if (= (last word) \s)
    (subs word 0 (- (count word) 1))
    word))

(defn pluralize [word]
  (if (= (last word) \s)
    word
    (str word \s)))

(defn with-singular [words]
  (clojure.set/union words (set (map unpluralize words))))

(defn with-plural [words]
  (clojure.set/union words (set (map pluralize words))))
