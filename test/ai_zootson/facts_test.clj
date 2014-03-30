(ns ai-zootson.facts-test
  (:refer-clojure :exclude [==])
  (:require [midje.sweet :refer :all]
            [clojure.core.logic :refer :all]
            [clojure.core.logic.pldb :as pldb]
            [ai-zootson.facts :refer :all]
            [ai-zootson.domain :refer :all]))

(fact singularize
  (tabular
    (fact (singularize ?plural) => ?singular)
    ?plural ?singular
    "octopuses" "octopus"
    "vampires" "vampire"
    "boys" "boy"
    "girls" "girl"))

(fact parse-fact
  (tabular
    (fact (parse-fact-sentence ?line) => ?expected)
    ?line ?expected

    "Anteater is a synonym for aardvark."
    [[:SUBJECT [:NOUN "anteater"]]
     [:VERB "is"]
     [:OBJECT [:conj [:NOUN "synonym"] "for" [:NOUN "aardvark"]]]]

    "Aardvark is a good swimmer."
    [[:SUBJECT [:NOUN "aardvark"]]
     [:VERB "is"]
     [:OBJECT [:adj [:ADJ "good"] [:NOUN "swimmer"]]]]

    "Cheetah is the fastest land animal."
    [[:SUBJECT [:NOUN "cheetah"]]
     [:VERB "is"]
     [:OBJECT [:adj [:ADJ "fastest"] [:NOUN "land" "animal"]]]]

    "Crayfish are smaller than lobsters."
    [[:SUBJECT [:NOUN "crayfish"]]
     [:VERB "are"]
     [:OBJECT [:than [:NOUN "smaller"] [:NOUN "lobsters"]]]]

    "Pussycats exist."
    [[:SUBJECT [:NOUN "pussycats"]]
     [:VERB "exist"]]

    "An elephants has a trunk."
    [[:SUBJECT [:NOUN "elephants"]]
     [:VERB "has"]
     [:OBJECT [:simple [:NOUN "trunk"]]]]

    "Elephants have big ears."
    [[:SUBJECT [:NOUN "elephants"]]
     [:VERB "have"]
     [:OBJECT [:adj [:ADJ "big"] [:NOUN "ears"]]]]

    "Male African elephant is the largest living terrestrial animal."
    [[:SUBJECT [:NOUN "male" "african" "elephant"]]
     [:VERB "is"]
     [:OBJECT [:adj [:ADJ "largest"] [:NOUN "living" "terrestrial" "animal"]]]]

    "Eastern lowland gorilla is the largest living primate."
    [[:SUBJECT [:NOUN "eastern" "lowland" "gorilla"]]
     [:VERB "is"]
     [:OBJECT [:adj [:ADJ "largest"] [:NOUN "living" "primate"]]]]

    "Kiwis are nocturnal birds."
    [[:SUBJECT [:NOUN "kiwis"]]
     [:VERB "are"]
     [:OBJECT [:simple [:NOUN "nocturnal" "birds"]]]]

    "The kiwi is a national symbol of New Zealand."
    [[:SUBJECT [:NOUN "kiwi"]]
     [:VERB "is"]
     [:OBJECT [:conj [:NOUN "national" "symbol"] "of" [:NOUN "new" "zealand"]]]]

    "Ladybird is also known as ladybug."
    [[:SUBJECT [:NOUN "ladybird"]]
     [:VERB "is"]
     [:OBJECT [:conj [:NOUN "also" "known"] "as" [:NOUN "ladybug"]]]]

    "Lynx have short tails."
    [[:SUBJECT [:NOUN "lynx"]]
     [:VERB "have"]
     [:OBJECT [:adj [:ADJ "short"] [:NOUN "tails"]]]]

    "Mongooses feed on insects, worms, snakes, and birds, etc."
    [[:SUBJECT [:NOUN "mongooses"]]
     [:VERB "feed"]
     [:OBJECT [:prep "on" [:NOUN "insects"] [:NOUN "worms"] [:NOUN "snakes"] [:NOUN "birds"] [:NOUN "etc"]]]]

    "Octopuses are intelligent."
    [[:SUBJECT [:NOUN "octopuses"]]
     [:VERB "are"]
     [:OBJECT [:simple [:NOUN "intelligent"]]]]

    "Octopuses are more intelligent than worms."
    [[:SUBJECT [:NOUN "octopuses"]]
     [:VERB "are"]
     [:OBJECT [:than "more" [:NOUN "intelligent"] [:NOUN "worms"]]]]

    "Vampires and fruitbats are bats."
    [[:SUBJECT [:NOUN "vampires"] [:NOUN "fruitbats"]]
     [:VERB "are"]
     [:OBJECT [:simple [:NOUN "bats"]]]]

    "Pussycats and girls can meow."
    [[:SUBJECT [:NOUN "pussycats"] [:NOUN "girls"]]
     [:VERB "can"]
     [:OBJECT [:simple [:NOUN "meow"]]]]

    ;; Own facts
    "Crayfish are smaller than dolphins."
    [[:SUBJECT [:NOUN "crayfish"]]
     [:VERB "are"]
     [:OBJECT [:than [:NOUN "smaller"] [:NOUN "dolphins"]]]]

    "The cheetah are faster than girls."
    [[:SUBJECT [:NOUN "cheetah"]]
     [:VERB "are"]
     [:OBJECT [:than [:NOUN "faster"] [:NOUN "girls"]]]]

    ;; For deus
    "Python is a bad programming language."
    [[:SUBJECT [:NOUN "python"]]
     [:VERB "is"]
     [:OBJECT [:adj [:ADJ "bad"] [:NOUN "programming" "language"]]]]
))

(fact to-map
  (to-map [[:SUBJECT [:NOUN "python"]]
           [:VERB "is"]
           [:OBJECT [:adj [:ADJ "bad"] [:NOUN "programming" "language"]]]]) =>
  {:SUBJECT [[:NOUN "python"]]
   :VERB "is"
   :OBJECT [:adj [:ADJ "bad"] [:NOUN "programming" "language"]]}
  )

(fact process-fact
  (tabular
    (fact (process-fact ?input) => ?result)
    ?input ?result
    {:SUBJECT [[:NOUN "pussycats"] [:NOUN "girls"]]
     :VERB "can"
     :OBJECT [:simple [:NOUN "meow"]]}
    {:verb "can"
     :subjects ["pussycat" "girl"]
     :type :simple
     :objects [[:NOUN "meow"]]}

    ;; {:SUBJECT [[:NOUN "octopuses"]]
    ;;  :VERB "are"
    ;;  :OBJECT [:than "more" [:NOUN "intelligent"] [:NOUN "worms"]]}
    ;; {:verb "are"
    ;;  :type :than
    ;;  :than "more"
    ;;  :than-prop "intelligent"
    ;;  :objects ["worms"]}
  ))

(fact build-facts
  (tabular
    (fact (build-facts ?input) => ?result)
    ?input ?result

    {:verb "can"
     :subjects ["pussycat" "girl"]
     :type :simple
     :objects ["meow"]}
    [['is-able "pussycat" "meow"] ['is-able "girl" "meow"]]

;;     {:verb "is"
;;      :subjects ["kiwi"]
;;      :type :conj
;;      :objects ["national symbol" "of" "new zealand"]}
;;     [["national-symbol" "kiwi" "new zealand"]]
;;
    {:verb "are"
     :type :than
     :subject ["cheetah"]
     :objects ["faster" "girl"]}
    [['is-more "cheetah" "girl" "faster"]]

    {:verb "are"
     :type :than
     :subjects ["octopuses"]
     :objects ["more" "intelligent" "worm"]}
    [['is-more "octopus" "worm" "intelligent"]]

    {:verb "is"
     :subjects ["male african elephant"]
     :type :adj
     :objects ["largest" "living terrestial animal"]}
    [['is-smth "male african elephant" "living terrestial animal" "largest"]]
    ))

(fact add-facts
  (let [db (pldb/db)
        db (add-facts db [['is-able "pussycat" "meow"] ['is-able "girl" "meow"]])]
    (pldb/with-db db
      (run* [q] (is-able q "meow")) => (just "girl" "pussycat"))))
