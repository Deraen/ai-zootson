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
  (insta/parser
    "<S> = (where | how-many | what | mention | which | which-is | what-kind-of | boolean) <TERMINATOR? space*>

     animal = NOUN
     what-kind = ADJ
     animal-class = ('reptile' | 'mammal' | 'bird' | 'amphibian' | 'insect' | 'invertebrate') <'s'>? | 'fish' <'es'>?
     lives-in = <'that lives in'> space NOUN

     food = NOUN

     where = <'where do'> space animal space <'live'>

     legs = <'does'> space animal space <'have'>
     how-many = <'how many'> space (animal-class / what-kind) space <(('do' | 'does') space)>? (animal space)? <'have' | 'you know'>

     what = <'what'> space what-kind (space animal-class)? space <('do' space)? 'you know'>
     mention = <'mention'> space what-kind space lives-in

     does-smth = 'eats' space food
     are-able = <'are able to'> space NOUN
     which = <'which' (space 'animal' 's'?)?> space (does-smth | are-able)

     animal1 = NOUN
     animal2 = NOUN
     which-is = <'which is'> space what-kind <':'>? space animal1 space <'or'> space animal2

     what-kind-of = <'what kind of'> space what-kind space <'does'> space animal space <'have'>

     verb = word
     do = <'do'> space <verb> space ADJ
     can = <'can'> space ADJ
     are = <'are'> space ADJ
     do-not = <'do not' | 'don\\'t'> space <verb> space ADJ
     cannot = <'cannot'> space ADJ
     arent = <'are not' | 'aren\\'t'> space ADJ

     boolean-type = 'true' | 'false'
     boolean = <'is it'> space boolean-type space <'that'> space animal space (do | can | are | do-not | cannot | arent)

     <letter> = #'\\p{L}'
     <space> = <#'\\s'>
     <space-visible> = #'\\s'
     <word> = #'\\p{L}+'

     <articles> = <('a' | 'an' | 'the')>
     <conjunctions> = ('and' | 'than' | 'of' | 'for' | 'as')

     (* NOUN are not nouns nor are ADJ adjectives... what ever... *)
     <noun-word> = !(articles space) !(conjunctions space) !('on' space) letter+ ('us' <'es'> | 'ose' <'s'> | <'s'>)?
     NOUN = <[articles space]> noun-word (space-visible noun-word)*
     ADJ = <[articles space]> letter+

     TERMINATOR = '.' | '?' | '!'
     "))

(defn process-value [value]
  (cond
    (and (sequential? value) (= (count value) 1)) (first value)
    (sequential? value) (let [[f foo :as bar] value]
                          (if (#{:NOUN :ADJ} f)
                            foo
                            bar))
    :else value))

(defn process-question [[type & rest]]
  (assoc
    (reduce (fn [acc [f foo]]
              (cond
                (= f :boolean-type) (assoc acc f (= foo "true"))
                :else (assoc acc f (process-value foo))))
            {} rest)
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
                            are-able
                            cannot can do do-not are arent
                            ] :as processed}]
  (cond
    ;; FIXME: Aliases only work with some question types...

    (= type :where) (run* [q] (check-lives-in animal q))

    (and what-kind animal-class) (run* [q] (classify q animal-class) (check-fact q what-kind))
    (and what-kind lives-in) (run* [q] (check-fact q what-kind) (check-lives-in q lives-in))
    (and animal what-kind) (run* [q] (check-fact animal what-kind q))

    (and animal can) [(if (empty? (run* [q] (is-able animal can))) false true)]
    (and animal are) (run* [q] (check-fact animal are q))
    (and animal do) (run* [q] (check-fact animal do q))

    (and animal cannot) [(if (empty? (run* [q] (is-able animal cannot))) true false)]
    (and animal arent) (reverse-foo (run* [q] (check-fact animal arent q)))
    (and animal do-not) (reverse-foo (run* [q] (check-fact animal do-not q)))

    are-able (run* [q] (is-able q are-able))
    animal-class (run* [q] (classify q animal-class))
    what-kind (run* [q] (check-fact q what-kind))
    does-smth (run* [q] (check-fact q does-smth))
    :else nil
    ))

(defn format-answer [{:keys [type boolean-type can cannot]} [f & _ :as facts]]
  (cond
    (and (= type :where) (empty? facts)) "nowhere"
    (= type :where) (clojure.string/capitalize (clojure.string/join ", " facts))

    (and (= type :how-many) (number? f) (= (count facts) 1)) (str f)
    (= type :how-many) (str (count facts))

    (and (#{:what :mention :which} type) (empty? facts)) "none"
    (#{:what :mention :which} type) (clojure.string/join ", " facts)

    (and (= type :what-kind-of) (empty? facts)) "no idea"
    (= type :what-kind-of) (clojure.string/join ", " (filter string? facts))

    (= type :boolean) (if (empty? facts)
                        "no idea"
                        (if (= (first facts) boolean-type)
                          "yes"
                          "no"))

    :else "no idea (format)"
    ))

(defn answer-question [db question-str]
  ;; (try+
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
    ;; (catch Object _
    ;;   "no idea (exception)"))
    )
