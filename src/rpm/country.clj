(ns rpm.country
  (:require [rpm.lib])
  (:gen-class))

(defn empty
  []
  {:coll [] :count 0})

(defn table []
  (rpm.lib/table :country))

(defn rows []
  (rpm.lib/rows :country))

(defn all []
  (rpm.lib/all :country))

(defn count []
  (rpm.lib/count :country))

(defn find [label]
  (rpm.lib/find :country label))

(defn find-by [attr value]
  (rpm.lib/find-by :country attr value))

(defn destroy-all []
  (rpm.lib/destroy-all :country))

(defn save [elem]
  (rpm.lib/save :country elem))

(defn destroy-by [attr value]
  (rpm.lib/destroy-by :country attr value))

(defn destroy [label]
  (rpm.lib/destroy :country label))
