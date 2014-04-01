(ns ai-zootson.sentence
  (:require [slingshot.slingshot :refer [throw+]]
            [ai-zootson.util :refer :all]))

(defn fix-words [parsed]
  (map (fn [i]
         (if (sequential? i)
           (let [[f & rst] i]
             (cond
               (= f :NOUN) [f (singularize (clojure.string/join "" rst))]
               (= f :ADJ) [f (clojure.string/join "" rst)]
               :else (fix-words i)))
           i))
       parsed))

(def shared-bnf
  "<letter> = #'\\p{L}'
   <space> = <#'\\s'>
   <space-visible> = #'\\s'
   <word> = #'\\p{L}+'

   <articles> = <('a' | 'an' | 'the')>
   <conjunctions> = ('and' | 'than' | 'of' | 'for' | 'as')

   <noun-word> = !(articles space) !(conjunctions space) !('on' space) letter+
   NOUN = <[articles space]> noun-word (space-visible noun-word)*
   <HIDE-NOUNS> = NOUN ((<','> [space <'and'>] | space <'and'>) space (<'etc'> | NOUN))*
   NOUNS = HIDE-NOUNS

   <ADJWORDS> = 'short' <'er' | 'est'>? | 'big' <'ger'>? | 'biggest' | 'small' <'er'>? | 'smallest' | 'good' | 'large' <'r'>? | 'bad' | 'fast' <'er'>? | 'fastest' | 'largest' | 'nocturnal' | 'slow' <'er'>?
   animal-class = ('reptile' | 'mammal' | 'bird' | 'amphibian' | 'insect' | 'invertebrate') <'s'>? | 'fish' <'es'>?

   TERMINATOR = '.' | '?' | '!'
   ")

(defn get-parsed [parsed]
  (if (sequential? parsed)
    (first parsed)
    (throw+ parsed)))
