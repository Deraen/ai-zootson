(ns ai-zootson.util)

(def plural-affixes
  "List of pairs containing regex to test if word
   is plural and string to replace that affix with.
   These are tested in this order and order matters
   in some cases."
  [
   [#"uses$" "us"]
   [#"hes$" "h"]
   [#"oes$" "o"]
   [#"ges$" "ge"]
   [#"lves$" "lf"]
   [#"ies$" "y"]
   [#"axes$" "axis"]
   [#"xes$" "x"]
   [#"crises$" "crisis"] ;; crises - crisis
   [#"crisis$" "crisis"] ;; crisis - crisis
   [#"cactus$" "cacti"]
   [#"sses$" "ss"]
   [#"ses$" "se"]
   ;; "baSS" and "octupUS"
   [#"([^su])s$" "$1"]
   ])

(defn singularize [word]
  (or (some (fn [[regex replace]]
              (if (re-find regex word)
                (clojure.string/replace word regex replace)))
            plural-affixes)
      word))
