(ns rpm.category)

(def db (atom {:coll '() :count 0}))

(defn make [label & parent] {:label label :parent nil})

(defn edit [e] :edit)

(defn table [] (deref db))

(defn all [] (:coll (deref db)))

(defn empty-table [] {:coll '() :count 0})

(defn get-count [] (:count (deref db)))

(defn find-entry [label] (first (filter (fn [category] (= (:label category) label)) (all))))

(defn find-by [attr value] (first (filter (fn [category] (= (attr category) value)) (all))))

(defn destroy-all []
  (reset! db {:coll '() :count 0}))

(defn save [elem]
  (do (swap! db #(-> % (update :coll conj elem) (update :count inc)))
      elem))

(defn destroy-by [attr value]
  (when-let [elem (find-by attr value)]
    (do
      (swap! db
             (fn [a]
               (-> a
                   (update :coll #(remove (constantly elem) %))
                   (update :count dec))))
      true)))

(defn destroy [label]
  (destroy-by :label label))
