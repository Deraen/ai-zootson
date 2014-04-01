(ns ai-zootson.questions
  (:refer-clojure :exclude [==])
  (:require [instaparse.core :as insta]
            [slingshot.slingshot :refer [try+ throw+]]
            [clojure.tools.macro :refer [deftemplate]]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [ai-zootson.util :refer :all]
            [ai-zootson.domain :refer :all]
            [ai-zootson.sentence :as sentence]))

(def question-language
  (insta/parser (str
    "<S> = (where | how-many | what | mention | which | which-is | what-kind-of | boolean | compare | can-do | do-have) <TERMINATOR? space*>

     animal = NOUN
     what-kind = ADJ
     lives-in = <'that lives in'> space NOUN

     food = NOUN

     where = <'where do'> space animal space <'live'>

     legs = <'does'> space animal space <'have'>
     how-many = <'how many'> space (animal-class / what-kind) space <(('do' | 'does') space)>? (animal space)? <'have' | 'you know'>

     what = <'what'> space what-kind (space animal-class)? space <('do' space)? 'you know'>

     is-smth2 = NOUN
     mention = <'mention'> space (what-kind space lives-in | <'an animal that is'> space is-smth2)

     can-do = <'can'> space animal space verb

     does-smth = word
     does-target = NOUN
     are-able = <'are able to'> space NOUN
     has-smth = <'has' | 'have'> space NOUN
     which = <'which' (space 'animal' 's'?)?> space (are-able / has-smth / does-smth space does-target)

     animals = HIDE-NOUNS
     do-have = <'do'> space animals space has-smth

     less = 'less'
     more = 'more'
     comp = ADJWORDS
     comp2 = ADJ
     animal1 = NOUN
     animal2 = NOUN
     which-is = <'which is'> space ((less | <more>) space comp2 | comp) <':'>? space animal1 space <'or'> space animal2

     compare = <'is' | 'are'> space animal1 space ((less | <more>) space comp2 | comp) space <'than'> space animal2

     what-kind-of = <'what kind of'> space what-kind space <'does' | 'do'> space animal space <'have'>

     verb = word
     do = <'do'> space <verb> space ADJ
     can = <'can'> space ADJ
     are = <'are'> space ADJ
     do-not = <'do not' | 'don\\'t'> space <verb> space ADJ
     cannot = <'cannot'> space ADJ
     arent = <'are not' | 'aren\\'t'> space ADJ

     boolean-type = 'true' | 'false'
     boolean = <'is it'> space boolean-type space <'that'> space animal space (do | can | are | do-not | cannot | arent)

     ADJ = <[articles space]> letter+
     " sentence/shared-bnf)))

(defn process-value [value]
  (cond
    (and (sequential? value) (= (count value) 1)) (first value)
    (sequential? value) (let [[f foo :as bar] value]
                          (if (#{:NOUN :ADJ} f)
                            foo
                            bar))
    :else value))

(defn process-question [[type & rst]]
  (assoc
    (reduce (fn [acc [f foo :as bar]]
              (cond
                (= f :boolean-type) (assoc acc f (= foo "true"))
                (= f :animals) (assoc acc f (map process-value (rest bar)))
                :else (assoc acc f (process-value foo))))
            {} rst)
    :type type))


(defn parse-question [question]
  (->> question
       clojure.string/lower-case
       question-language
       sentence/get-parsed
       sentence/fix-words
       ))

;; (defn build-query [{:keys [animal] :as processed}]
;;   (reduce (fn [acc [k v]]
;;             (cond
;;               (and (= k :type) (= v :where)) (conj acc (list :lives-in animal :q true))
;;               (= k :lives-in) (conj acc (list :lives-in (or animal :q) (or v :q) true))
;;               (= k :what-kind) (conj acc (list :check-fact (or animal :q) (or v :q)))
;;               :else acc
;;               ))
;;           '() processed))
;;
;; (deftemplate suck-it [db facts]
;;   (concat (list 'run-db* db '[q])
;;           (map (fn [[& rest]]
;;                  (let [rest (map (fn [i]
;;                                    (if (keyword? i)
;;                                      (symbol (name i))
;;                                      i))
;;                                  rest)]
;;                    `(~@rest)))
;;                facts)))

(defn reverse-foo [foo]
  (map not foo))

(defn get-facts [db {:keys [type what-kind animal animal-class lives-in does-smth
                            are-able has-smth is-smth2
                            cannot can do do-not are arent verb
                            animal1 animal2 comp
                            animals
                            does-smth does-target
                            ] :as processed}]
  (cond
    ;; FIXME: Aliases only work with some question types...
    (= type :where) (run* [q] (check-lives-in animal q))

    ;; If every animal fulfils some query...
    (and animals) [(every? (fn [animal]
                            (let [foo (-> processed
                                          (assoc :animal animal)
                                          (dissoc :animals))
                                  bar (get-facts db foo)]
                              (first bar)))
                         animals)]

    (and what-kind animal-class) (run* [q] (classify q animal-class) (check-fact q what-kind))
    (and what-kind lives-in) (run* [q] (check-fact q what-kind) (check-lives-in q lives-in))
    (and animal what-kind) (run* [q] (check-fact animal what-kind q))

    (and animal can) [(if (empty? (run* [q] (is-able animal can))) false true)]
    (and animal are) (run* [q] (check-fact animal are q))
    (and animal do) (run* [q] (check-fact animal do q))

    (and animal cannot) [(if (empty? (run* [q] (is-able animal cannot))) true false)]
    (and animal arent) (reverse-foo (run* [q] (check-fact animal arent q)))
    (and animal do-not) (reverse-foo (run* [q] (check-fact animal do-not q)))

    (and does-smth does-target) (let [verb (get {"eats" "feed"} does-smth does-smth)]
                                  ;; (println verb does-target)
                                  (run* [q] (do-smth q verb does-target)))

    (= type :which-is) (let [{:keys [prop less]} (get adjectives comp {:prop comp})
                             prop (or prop (:comp2 processed))
                             less (or less (:less processed))]
                         ;; (println prop less)
                         (if less
                           (run* [q]
                                 (conde [(== q animal1) (is-less q animal2 prop)]
                                        [(== q animal2) (is-less q animal1 prop)]
                                        [(== q animal1) (is-least q prop)]
                                        [(== q animal2) (is-least q prop)]))
                           (run* [q]
                                 (conde [(== q animal1) (is-more q animal2 prop)]
                                        [(== q animal2) (is-more q animal1 prop)]
                                        [(== q animal1) (is-most q prop)]
                                        [(== q animal2) (is-most q prop)]))
                           ))

    (= type :compare) (let [{:keys [prop less]} (get adjectives comp {:prop comp})
                             prop (or prop (:comp2 processed))
                             less (or less (:less processed))]
                        ;; (println prop less)
                        (if less
                          (cond
                            (seq (run* [q] (is-less animal1 animal2 prop))) [true]
                            (seq (run* [q] (is-more animal1 animal2 prop))) [false]
                            (seq (run* [q] (is-least animal1 prop))) [true]
                            (seq (run* [q] (is-most animal2 prop))) [true]
                            :else [])
                          (cond
                            (seq (run* [q] (is-more animal1 animal2 prop))) [true]
                            (seq (run* [q] (is-less animal1 animal2 prop))) [false]
                            (seq (run* [q] (is-most animal1 prop))) [true]
                            (seq (run* [q] (is-least animal2 prop))) [true]
                            :else [])
                          ))

    (and (= type :can-do) animal verb) (let [target (get {"swim" "swimmer"} verb verb)]
                                         (run* [q]
                                               (fresh [x]
                                                      (is-smth animal x target))))

    are-able (run* [q] (is-able q are-able))
    animal-class (run* [q] (classify q animal-class))
    what-kind (run* [q] (check-fact q what-kind))
    ;; does-smth (run* [q] (check-fact q does-smth))
    is-smth2 (run* [q] (check-fact q is-smth2))

    has-smth (run* [q] (check-has-smth q has-smth))

    :else nil
    ))

(defn format-answer [{:keys [type boolean-type can cannot]} [f & _ :as facts]]
  (cond
    (and (= type :where) (empty? facts)) "nowhere"
    (= type :where) (->> (map clojure.string/capitalize
                              (-> (clojure.string/join ", " facts)
                                  (clojure.string/split #"\s")))
                         (clojure.string/join " "))

    (and (= type :how-many) (number? f) (= (count facts) 1)) (str f)
    (= type :how-many) (str (count facts))

    (and (#{:what :mention :which} type) (empty? facts)) "none"
    (#{:what :mention :which} type) (clojure.string/join ", " facts)

    (and (= type :what-kind-of) (empty? (filter string? facts))) "no idea"
    (= type :what-kind-of) (clojure.string/join ", " (filter string? facts))

    (= type :boolean) (if (empty? facts)
                        "no idea"
                        (if (= (first facts) boolean-type)
                          "yes"
                          "no"))

    (= type :which-is) (let [f (first facts)]
                         (if f
                           f
                           "no idea"))

    (#{:compare :do-have} type) (if (empty? facts)
                        "no idea"
                        (if (first facts)
                          "yes"
                          "no"))

    (= type :can-do) (if (empty? facts)
                       "no idea"
                       "yes")

    :else "no idea"
    ))

(defn answer-question [db question-str]
  (try+
    (pldb/with-db db
      (let [{:keys [type animal] :as processed}
            (-> question-str
                parse-question
                process-question)

            foo (get-facts db processed)
            ]
        ;; (println processed)
        ;; (println foo)
        (format-answer processed foo)))
    (catch Object _
      "no idea"))
    )
