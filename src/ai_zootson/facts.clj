(ns ai-zootson.facts
  (:refer-clojure :exclude [==])
  (:require [instaparse.core :as insta]
            [ai-zootson.sentence :as sentence]
            [clojure.core.logic :refer :all]
            ))

(def fact-language
  (insta/parser
    "<S> = SUBJECT VERB OBJECT <TERMINATOR?> | SUBJECT VERB <TERMINATOR?>
     <word> = #'\\p{L}+'
     <space> = <#'\\s'>
     <words> = word space? | word space words

     <articles> = <('a' | 'an' | 'the')>
     <conjunctions> = ('and' | 'than' | 'of' | 'for' | 'as')

     <noun-word> = !(articles space) !(conjunctions space) !('on' space) word
     NOUN = <[articles space]> noun-word (space noun-word)*
     <NOUNS> = NOUN ((<','> [space <'and'>] | space <'and'>) space NOUN)* space?
     SUBJECT = NOUNS
     VERB = ('is' | 'are' | 'have' | 'has' | 'can' | 'feed' | 'exist') space?

     (* ADJ = !(articles space) word *)
     ADJ = 'short' | 'big' | 'smaller' | 'good' | 'largest' | 'fastest'

     conj = [ADJ space] NOUN space conjunctions space [ADJ space] NOUN
     adj = <[articles space]> ADJ (space ADJ)* space NOUNS
     than = [('more') space] NOUN space <'than'> space NOUN

     prep = 'on'

     OBJECT = conj | adj | than | [prep space] NOUNS
     TERMINATOR = '.' | '?' | '!'
     "))

(defn parse-fact [fact-str]
  (->> fact-str
       clojure.string/lower-case
       fact-language
       ))

(defn read-fact [data fact-str]
  nil)

(defn read-facts [data]
  nil)

