(ns clojure.core.match.regex
  (:require
   [clojure.core.match.protocols :as mp]
   [clojure.core.match :as m :refer [emit-pattern to-source groupable?]])
  (:import [java.util.regex Pattern]))

;; # Regular Expression Extension
;;
;; This extension adds support for Clojure's regular expression syntax.

(extend-type Pattern
  mp/ISyntaxTag
  (syntax-tag [_] ::m/regex))

(defrecord RegexPattern [regex])

(defn regex-pattern [pat]
  (assoc (RegexPattern. pat) ::m/tag ::m/regex))

(defmethod emit-pattern ::m/regex
  [pat]
  (regex-pattern pat))

;; Regular expressions are matched with `re-matches`.
;;
;; For example, given a pattern `#"olive"` and occurance `q`, a match occurs
;; when this expression is true:
;;
;; `(re-matches #"olive" q)`

(defmethod to-source ::m/regex
  [pat ocr]
  `(re-matches ~(:regex pat) ~ocr))

;; `java.util.regex.Pattern` doesn't override `equals`, so we reinvent it here.
;;
;; Two `Pattern`s are equal if they have the same pattern and the same flags.

(defmethod groupable? [::m/regex ::m/regex]
  [a b]
  (let [^Pattern ra (:regex a)
        ^Pattern rb (:regex b)]
    (and (= (.pattern ra) (.pattern rb))
         (= (.flags ra) (.flags rb)))))

