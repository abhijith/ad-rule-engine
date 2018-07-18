(ns rpm.country)

(def db (atom {:coll '() :count 0}))

(defn make [label] {:label label})

(defn table [] (deref db))

(defn all [] (:coll (deref db)))

(defn empty-table [] {:coll '() :count 0})

(defn get-count [] (:count (deref db)))

(defn find-by-aux [coll attr value]
  (first (filter (fn [x] (= (attr x) value)) coll)))

(defn find-by [attr value]
  (find-by-aux (all) attr value))

(defn find-entry [label] (find-by :label label))

(defn destroy-all []
  (reset! db (empty-table)))

(defn save-aux [db elem]
  [db elem]
  (-> db
      (update :coll conj elem)
      (update :count inc)))

(defn save
  [elem]
  (do (swap! db save-aux elem)
      elem))

(defn delete [db elem]
  (-> db
      (update :coll #(remove (constantly elem) %))
      (update :count dec)))

(defn destroy-by
  [attr value]
  (when-let [elem (find-by attr value)]
    (do (swap! db delete elem)
        true)))

(defn destroy [label]
  (destroy-by :label label))
