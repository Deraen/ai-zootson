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
  (insta/parser
    "<S> = (is-alias | is-alias-reverse | is-smth | is-more | animal | has-prop | some-kind-prop | is-able | eats | is-smth-of) <TERMINATOR? space*>

     (* these names are directly transleted to core.logic relations *)
     is-alias = NOUN space <'is' space (articles space)? ('alias' | 'synonym')> space <'for'> space NOUN
     (* except in reverse case... *)
     is-alias-reverse = NOUN space <'is also known as'> space NOUN

     is-smth = NOUNS space <('is' | 'are') space (articles space)?> (ADJ space)? NOUN (space ADJ)?
     is-more = NOUN space <('is' | 'are') (space 'more')?> space ADJ space <'than'> space NOUN
     animal = NOUNS space <'exist'>
     has-prop = NOUNS space <'has' | 'have'> space NOUN
     some-kind-prop = NOUNS space <'has' | 'have'> space ADJ space NOUN
     is-able = NOUNS space <'can'> space NOUN
     eats = NOUNS space <'feed on' | 'eat'> space NOUNS
     is-smth-of = NOUNS space <'is' | 'are'> space NOUN space <'of'> space NOUN

     <letter> = #'\\p{L}'
     <space> = <#'\\s'>
     <space-visible> = #'\\s'

     <articles> = <('a' | 'an' | 'the')>
     <conjunctions> = ('and' | 'than' | 'of' | 'for' | 'as')

     (* strip plural... mongoose_s, octopus_es *)
     <noun-word> = !(articles space) !(conjunctions space) !('on' space) letter+ ('us' <'es'> | 'ose' <'s'> | <'s'>)?
     NOUN = <[articles space]> noun-word (space-visible noun-word)*
     NOUNS = NOUN ((<','> [space <'and'>] | space <'and'>) space (<'etc'> | NOUN))*

     ADJ = 'short' <'er' | 'est'>? | 'big' <'ger'>? | 'biggest' | 'small' <'er'>? | 'smallest' | 'good' | 'large' <'r'>? | 'bad' | 'fast' <'er'>? | 'fastest' | 'largest' | 'nocturnal' | 'intelligent'

     TERMINATOR = '.' | '?' | '!'
     "))

(defn parse-fact-sentence [fact-str]
  (->> fact-str
       clojure.string/lower-case
       fact-language
       sentence/fix-words
       first
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
              (cond
                (= fact 'is-alias-reverse) (pldb/db-fact db is-alias (first rest) animal)
                (= fact 'some-kind-prop) (pldb/db-fact db has-prop animal (nth rest 1) (nth rest 0))
                (= fact 'is-more') (pldb/db-fact db is-more animal (real-animal db (nth rest 0) (nth rest 1)))
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
