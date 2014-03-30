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
    "<S> = SUBJECT VERB OBJECT <TERMINATOR?> space* | SUBJECT VERB <TERMINATOR?> space*
     <word> = #'\\p{L}+'
     <space> = <#'\\s'>
     <words> = word space? | word space words

     <articles> = <('a' | 'an' | 'the')>
     <conjunctions> = ('and' | 'than' | 'of' | 'for' | 'as')

     <prepwords> = 'on'

     <noun-word> = !(articles space) !(conjunctions space) !('on' space) word
     NOUN = <[articles space]> noun-word (space noun-word)*
     <NOUNS> = NOUN ((<','> [space <'and'>] | space <'and'>) space NOUN)* space?
     SUBJECT = NOUNS
     VERB = ('is' | 'are' | 'have' | 'has' | 'can' | 'feed' | 'exist') space?

     (* ADJ = !(articles space) word *)
     ADJ = 'short' | 'big' | 'smaller' | 'good' | 'largest' | 'fastest' | 'bad'

     conj = [ADJ space] NOUN space conjunctions space [ADJ space] NOUN
     adj = <[articles space]> ADJ space NOUNS
     than = [('more') space] NOUN space <'than'> space NOUN
     prep = prepwords space NOUNS
     simple = NOUNS

     OBJECT = conj | adj | than | prep | simple
     TERMINATOR = '.' | '?' | '!'
     "))

(defn parse-fact-sentence [fact-str]
  (->> fact-str
       clojure.string/lower-case
       fact-language
       ))

(defn to-map [parsed-list]
  (reduce (fn [acc [k & rest]]
            (if (= k :SUBJECT)
              (assoc acc k rest)
              (apply assoc acc k rest)))
          {} parsed-list))

(defn singularize [string]
  (apply clojure.string/replace string
         (cond
           (.endsWith string "ses") [#"ses$" "s"]
           :else [#"s$" ""])))

(defn process-words [words]
  (singularize (clojure.string/join " " words)))

(defn process-fact [{objects :OBJECT verb :VERB subjects :SUBJECT :as parsed}]
  (let [; All subjects should be NOUN so type doesn't matter
        ; Join multi word nouns into one string and strip "s" from plural words
        subjects (map (fn [[_ & words]] (process-words words)) subjects)
        ; First property tells what "type" sentence is (conj, adj, than, prep, simple...)
        sentence-type (first objects)
        ; Get "real objects"
        objects (rest objects)
        objects (map (fn [object]
                       (if (sequential? object)
                         [(first object) (process-words (rest object))]
                         object))
                     objects)
        ]
    {:verb verb
     :objects objects
     :subjects subjects
     :type sentence-type}))

(defn process2-fact [{:keys [verb [f s & _ :as objects] subjects type] :as processed}]
  (cond
    (and (= type :than)) processed
    :else (assoc processed :objects (map (fn [[_ foo]] foo) objects))))

(def verbs-to-facts {"can" 'is-able
                     "has" 'has-property
                     "feed" 'eats
                     "are" 'is-smth
                     "is" 'is-smth})

;; Fact, reverse
(def than-map {"faster" ['is-faster false]
               "slower" ['is-faster true]
               "more" ['is-more false]
               "less" ['is-more true]})

(def a {:simple (fn [verb subject objects]
                  (map (fn [object]
                         [(get verbs-to-facts verb) subject object])
                       objects))
        :adj (fn [verb subject [adj & objects]]
               (map (fn [object]
                      ['is-smth subject object adj])
                    objects))
        :than (fn [verb subject [than foo & rst]]
                ;; (println than)
                ;; (println foo)
                nil)
        :conj (fn [verb subject object]
                nil)
        :prep (fn [verb subject object]
                nil)
        nil (fn [verb subject]
              nil)
        })

(defn build-facts [{:keys [subjects type objects verb] :as processed}]
  (reduce
    (fn [acc subject]
      (if type
        (concat acc ((type a) verb subject objects))
        acc))
    [] subjects))

(defn add-facts [db facts]
  (reduce (fn [db [fact target & rest]]
            ;; In case target is alias, use the real target
            (let [[real-target] (pldb/with-db db (run* [q] (some-animal q target)))
                  real-target (or real-target target)]
              (println fact real-target rest)
              (apply pldb/db-fact db @(resolve fact) real-target rest)))
          db (filter #(not (nil? %)) facts)))

(defn read-facts [db data]
  (reduce (fn [db line]
            (->> line
                 (parse-fact-sentence)
                 (to-map)
                 (process-fact)
                 (process2-fact)
                 (build-facts)
                 (add-facts db)))
          db (line-seq data)))
