(ns rpm.category)

;; TODO support hierachies: isa?

(def db (atom {:coll '() :count 0}))

(defn make
  "Takes a label, parent optionally and creates an entity map."
  [label & parent]
  {:label label :parent nil})

(defn empty-table
  "Returns an empty instance of an entity db."
  []
  {:coll '() :count 0})

(defn table
  "Returns the entity db."
  []
  (deref db))

(defn all
  "Returns a coll of entities."
  []
  (:coll (deref db)))

(defn save-aux
  "Returns a new db with elem added to it."
  [db elem]
  (-> db
      (update :coll conj elem)
      (update :count inc)))

(defn save
  "Atomically updates the entities db with elem. Returns elem."
  [elem]
  (do (swap! db save-aux elem)
      elem))

(defn find-by-aux
  "Finds entity, given coll and attr->value. Returns nil if not found."
  [coll attr value]
  (first (filter (fn [x] (= (attr x) value)) coll)))

(defn find-by [attr value]
  "Returns entity, given attr->value. Returns nil if not found."
  (find-by-aux (all) attr value))

(defn find-entry
  "Returns entity, given label. Returns nil if not found."
  [label]
  (find-by :label label))

(defn get-count
  "Returns the number of entities stored in the db."
  []
  (:count (deref db)))

(defn delete
  "Returns a new db that does not contain elem. Also decrements counter on the coll"
  [db elem]
  (when-let [e (find-entry (:label elem))]
    (-> db
        (update :coll #(remove (constantly e) %))
        (update :count dec))))

(defn destroy-by
  "Returns a new db that does not contain the elem with attr->val"
  [attr value]
  (when-let [elem (find-by attr value)]
    (do (swap! db delete elem)
        true)))

(defn destroy
  "Returns a new db that does not contain elem."
  [label]
  (destroy-by :label label))

(defn destroy-all
  "Returns the truncated advert db."
  []
  (reset! db (empty-table)))
