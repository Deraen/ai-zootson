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
  (apply conj words (map unpluralize words)))

(defn with-plural [words]
  (apply conj words (map pluralize words)))
