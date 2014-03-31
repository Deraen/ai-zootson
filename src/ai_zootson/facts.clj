(ns ai-zootson.facts
  (:refer-clojure :exclude [==])
  (:require [instaparse.core :as insta]
            [ai-zootson.sentence :as sentence]
            [ai-zootson.domain :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            ))

(def fact-language
  "Notes:
   - Some adjectives are classified as Nouns.
   But that won't matter as reason for having separate adjectives
   is just to separate multi word nouns etc.
   - One fact in examples ends in trailing whitespace so allow spaces at the end of sentence."
  (insta/parser (str
    "<S> = (is-alias | is-alias-reverse | is-smth | is-more | is-less | animal | has-prop | some-kind-prop | is-able | eats | is-smth-of) <TERMINATOR? space*>

     (* these names are nearly... directly transleted to core.logic relations *)
     is-alias = NOUN space <'is' space (articles space)? ('alias' | 'synonym')> space <'for'> space NOUN
     is-alias-reverse = NOUN space <'is also known as'> space NOUN

     is-smth = NOUNS space <('is' | 'are') space (articles space)?> (ADJ space)? NOUN (space ADJ)?

     is-more = NOUN space <('is' | 'are')> space (<'more'> space NOUN | ADJ) space <'than'> space NOUN
     is-less = NOUN space <('is' | 'are') space 'less'> space NOUN space <'than'> space NOUN

     animal = NOUNS space <'exist'>
     has-prop = NOUNS space <'has' | 'have'> space NOUN
     some-kind-prop = NOUNS space <'has' | 'have'> space ADJ space NOUN
     is-able = NOUNS space <'can'> space NOUN
     eats = NOUNS space <'feed on' | 'eat'> space NOUNS
     is-smth-of = NOUNS space <'is' | 'are'> space NOUN space <'of'> space NOUN

     ADJ = ADJWORDS
     " sentence/shared-bnf)))

(defn parse-fact-sentence [fact-str]
  (->> fact-str
       clojure.string/lower-case
       fact-language
       sentence/get-parsed
       sentence/fix-words
       ))

(defn expand [parsed]
  (reduce
    (fn [acc i]
      (cond
        (sequential? i) (let [[f & foo] i]
                          (reduce
                            (fn [acc2 bar]
                              (reduce
                                (fn [acc2 lol]
                                  (conj acc2 (conj lol bar)))
                                acc2 acc))
                            [] foo))
        :else (map #(conj % i) acc)
        ))
    [[]] parsed))

(defn flatten-fact [fact]
  (map (fn [i]
         (if (sequential? i)
           (nth i 1)
           i))
       fact))

(defn flatten-facts [facts]
  (map flatten-fact facts))

(defn add-facts [db facts]
  (reduce (fn [db [fact animal & rest :as full]]
            ;; In case animal is alias, use the real animal
            (let [animal (real-animal db animal)
                  fact (symbol (name fact))]
              (println full)
              (cond
                (= fact 'is-alias-reverse) (pldb/db-fact db is-alias (first rest) animal)
                (= fact 'some-kind-prop) (pldb/db-fact db has-prop animal (nth rest 1) (nth rest 0))

                (#{'is-more 'is-less} fact)
                (let [[adj animal2] rest
                      animal2 (real-animal db animal2)
                      {:keys [prop less]} (get adjectives adj {:prop adj})

                      less (or less (= fact 'is-less))
                      ]
                  (if less
                    ;; reverse reverse... seems to work now
                    (pldb/db-fact db is-more animal2 animal prop)
                    (pldb/db-fact db is-more animal animal2 prop)))

                :else (apply pldb/db-fact db @(resolve fact) animal rest)
                )))
          db (filter #(not (nil? %)) facts)))

(defn read-facts [db data]
  (reduce (fn [db line]
            (->> line
                 (parse-fact-sentence)
                 (expand)
                 (flatten-facts)
                 (add-facts db)))
          db (line-seq data)))
