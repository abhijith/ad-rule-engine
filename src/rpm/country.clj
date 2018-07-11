(ns rpm.country)

(def db (atom {:coll '() :count 0}))

(defn make [label] {:label label})

(defn table [] (deref db))

(defn all [] (:coll (deref db)))

(defn empty-table [] {:coll '() :count 0})

(defn get-count [] (:count (deref db)))

(defn find-entry [label] (first (filter (fn [country] (= (:label country) label)) (all))))

(defn find-by [attr value]
  (first (filter (fn [x] (= (attr x) value)) (all))))

(defn destroy-all []
  (reset! db (empty-table)))

(defn save [elem]
  (do (swap! db #(-> % (update :coll conj elem) (update :count inc)))
      elem))

(defn destroy-by [attr value]
  (when-let [elem (find-by attr value)]
    (do
      (swap! db
             (fn [a]
               (-> a
                   (update :coll #(remove (fn [country] (= (attr country) value)) %))
                   (update :count dec))))
      true)))

(defn destroy [label]
  (destroy-by :label label))
