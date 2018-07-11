(ns rpm.lib
  (:require [rpm.state]))

(defn rows [type]
  (get-in (deref rpm.state/db) [type :coll]))

(def all rows)

(defn table [type]
  ((deref rpm.state/db) type))

(defn save [type elem]
  (swap! rpm.state/db (fn [a]
              (-> a
                  (update-in [type :coll] conj elem)
                  (update-in [type :count] inc))))
  elem)

(defn find-by [type attr value]
  (first (filter (fn [x] (= (attr x) value)) (rows type))))

(defn find [type label]
  (find-by type :label label))

(defn destroy-by [type attr value]
  (if-let [elem (find-by type attr value)]
    (do
      (swap! rpm.state/db
             (fn [a]
               (-> a
                   (update-in [type :coll]
                              (fn [rows]
                                (into [] (remove (fn [row] (= (attr row) value)) rows))))
                   (update-in [type :count] dec))))
      true)))


(defn destroy [type label]
  (destroy-by type :label label))

(defn count [type]
  (get-in (deref rpm.state/db) [type :count]))

(defn empty [type]
  "this has to be per domain type"
  {:coll [] :count 0})

(defn destroy-all [type]
  (swap! rpm.state/db (fn [a] (update-in a [type] (fn [_] (empty type))))))
