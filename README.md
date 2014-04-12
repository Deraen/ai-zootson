# ai-zootson

Artificial Intelligence course programming excercise.

## Running

To run main program (read inputs, read questions, write answers.txt):
```
lein run
```

To run tests:
```
lein midje :autotest
```

## Requirements

- Read facts from two csv files
- Read facts given as English sentences
  - "Cheetah is the fastest land animal."
  - "The kiwi is a national symbol of New Zealand."
- Read questions given as English sentences
  - "Are girls slower than a cheetah?" => "Yes" (because Cheetah is the fastest land animal)
  - "Mention an animal that is a national symbol." => "Kiwi"
- Answer questions using known facts
- Do not use NLP libraries (NLTK or such).
- Do not use 3rd party word lists
  - Implementation should only need to know few verbs and adjectives

## Implementation

- [Clojure](http://clojure.org/)
- Uses [Core.logic](https://github.com/clojure/core.logic) for fact storage and logical queries
- Processes sentences using BNF
  - Separate BNF language for facts and questions
  - [Instaparse](https://github.com/Engelberg/instaparse)
- [Midje](https://github.com/marick/Midje) for tests

## Possible improvements

- Large part of logical queries on `questions.clj` was written in hurry, could use refactoring
- Core.logic might me unnecessary, it might be simpler to store facts as vector of maps and find matching facts manually
  - http://programming-puzzler.blogspot.fi/2013/03/logic-programming-is-overrated.html
- Refactor fact-language to tag different parts of sentences like question-language does
  - That way it would be easier to turn parse results into facts
