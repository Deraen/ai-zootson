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
     <NOUN> = <[('a' | 'an' | 'the') space]> word
     SUBJECT = NOUN [space <'and'> space SUBJECT] space?
     VERB = ('is' | 'are' | 'have' | 'has' | 'can' | 'feed' | 'exist') space?
     OBJECT = words
     TERMINATOR = '.' | '?' | '!'"))

(defn parse-fact [fact-str]
  (->> fact-str
       clojure.string/lower-case
       fact-language
       ))

(defn read-fact [data fact-str]
  nil)

(defn read-facts [data]
  nil)

