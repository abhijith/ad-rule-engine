(ns rpm.channel
  (:require [rpm.lib])
  (:gen-class))

(defn make
  [label & {:keys [limit start-date end-date constraints] :as m, :or {limit 1}}]
  (merge {:label label} m))

(defn empty
  []
  {:coll [] :count 0})

(defn table []
  (rpm.lib/table :channel))

(defn rows []
  (rpm.lib/rows :channel))

(defn all []
  (rpm.lib/all :channel))

(defn count []
  (rpm.lib/count :channel))

(defn find [label]
  (rpm.lib/find :channel label))

(defn find-by [attr value]
  (rpm.lib/find-by :channel attr value))

(defn destroy-all []
  (rpm.lib/destroy-all :channel))

(defn save [elem]
  (rpm.lib/save :channel elem))

(defn destroy-by [attr value]
  (rpm.lib/destroy-by :channel attr value))

(defn destroy [label]
  (rpm.lib/destroy :channel label))
