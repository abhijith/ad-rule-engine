(ns rpm.country)

(def db (atom {:coll '() :count 0}))

(defn make
  [label]
  {:label label})

(defn table [] (deref db))

(defn rows [] (:coll (deref db)))

(def all rows)

(defn empty [] {:coll '() :count 0})

(defn count [] (:count (deref db)))

(defn find [label] (first (filter (fn [ad] (= (:label ad) label)) (all))))

(defn find-by [attr value]
  (first (filter (fn [x] (= (attr x) value)) (rows))))

(defn destroy-all []
  (reset! db {:coll '() :count 0}))

(defn save [elem]
  (swap! db (fn [a]
              (-> a
                  (update-in [:coll] conj elem)
                  (update-in [:count] inc))))
  elem)

(defn destroy-by [attr value]
  (if-let [elem (find-by attr value)]
    (do
      (swap! db
             (fn [a]
               (-> a
                   (update-in [:coll]
                              (fn [rows]
                                (into [] (remove (fn [row] (= (attr row) value)) rows))))
                   (update-in [:count] dec))))
      true)))


(defn destroy [label]
  (destroy-by :label label))
