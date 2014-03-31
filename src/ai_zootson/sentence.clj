(ns ai-zootson.sentence
  (:require [slingshot.slingshot :refer [throw+]]
            [ai-zootson.util :refer :all]))

(defn fix-words [parsed]
  (map (fn [i]
         (if (sequential? i)
           (cond
             (#{:NOUN :ADJ} (get i 0)) [(get i 0) (clojure.string/join "" (rest i))]
             :else (fix-words i))
           i))
       parsed))

(def shared-bnf
  "<letter> = #'\\p{L}'
   <space> = <#'\\s'>
   <space-visible> = #'\\s'
   <word> = #'\\p{L}+'

   <articles> = <('a' | 'an' | 'the')>
   <conjunctions> = ('and' | 'than' | 'of' | 'for' | 'as')

   <noun-word> = !(articles space) !(conjunctions space) !('on' space) letter+ (('us'|'h') <'es'> | 'se' <'s'> | ('d'|'s'|'m'|'l'|'t'|'r'|'k'|'ge'|'e'|'e'|'i'|'n')<'s'>)?
   NOUN = <[articles space]> noun-word (space-visible noun-word)*
   NOUNS = NOUN ((<','> [space <'and'>] | space <'and'>) space (<'etc'> | NOUN))*

   <ADJWORDS> = 'short' <'er' | 'est'>? | 'big' <'ger'>? | 'biggest' | 'small' <'er'>? | 'smallest' | 'good' | 'large' <'r'>? | 'bad' | 'fast' <'er'>? | 'fastest' | 'largest' | 'nocturnal' | 'slow' <'er'>?

   TERMINATOR = '.' | '?' | '!'
   ")

(defn get-parsed [parsed]
  (if (sequential? parsed)
    (first parsed)
    (throw+ parsed)))
