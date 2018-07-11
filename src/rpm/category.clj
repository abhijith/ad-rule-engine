(ns rpm.category
  (:require [rpm.lib])
  (:gen-class))

;; {:label "team-bhp.com" :categories ["bike", "motosport", "ktm"]}

(defn make [label categories & parent-id])

(defn empty [] {:coll [] :count 0})

(defn table [] (rpm.lib/table :category))

(defn rows [] (rpm.lib/rows :category))

(defn all [] (rpm.lib/all :category))

(defn count [] (rpm.lib/count :category))

(defn find [label] (rpm.lib/find :category label))

(defn find-by [attr value] (rpm.lib/find-by :category attr value))

(defn destroy-all [] (rpm.lib/destroy-all :category))

(defn save [elem] (rpm.lib/save :category elem))

(defn destroy-by [attr value] (rpm.lib/destroy-by :category attr value))

(defn destroy [label] (rpm.lib/destroy :category label))

(defn make [label & parent-id])
(defn parent [x])
(defn children [x])
(defn ancestors [x])
(defn descendants [x])
(defn ancestor? [x y])
(defn add-child [x y])
