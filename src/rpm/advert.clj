(ns rpm.advert
  (:require [java-time]))

(def db (atom {:coll '() :count 0}))

(defn make-limit [n] {:limit n :views 0})

(defn make
  [label & {:keys [limits start end rule] :as m}]
  (merge {:label label} m))

(defn empty-table [] {:coll '() :count 0})

(defn table [] (deref db))

(defn all [] (:coll (deref db)))

(defn save-aux
  [db elem]
  (-> db
      (update :coll conj elem)
      (update :count inc)))

(defn save
  [elem]
  (do (swap! db save-aux elem)
      elem))

(defn find-by-aux [coll attr value]
  (first (filter (fn [x] (= (attr x) value)) coll)))

(defn find-by [attr value]
  (find-by-aux (all) attr value))

(defn find-entry [label] (find-by :label label))

(defn get-count [] (:count (deref db)))

(defn delete [db elem]
  (-> db
      (update :coll #(remove (constantly elem) %))
      (update :count dec)))

(defn destroy-by
  [attr value]
  (when-let [elem (find-by attr value)]
    (do (swap! db delete elem)
        true)))

(defn destroy [label] (destroy-by :label label))

(defn destroy-all []  (reset! db (empty-table)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; advert attrs specific fns ;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn limits [ad] (:limits ad))

(defn limit [ad type] (type (limits ad)))

(defn views [ad type] (type (limits ad)))

(defn live?
  [x]
  (let [now (java-time.local/local-date-time)]
    (and
     (java-time.core/after?  now (:start x))
     (java-time.core/before? now (:end x)))))

(defn expired? [x] (java-time.core/after? (java-time.local/local-date-time) (:end x)))

(defn limit-exceeded? [limit] (>= (:views limit) (:limit limit)))

(defn global-limit-exceeded? [ad] (limit-exceeded? (:global (limits ad))))

(defn exceeded? [ad k v] (limit-exceeded? (get-in (limits ad) [k v])))

(defn country-limit-exceeded? [ad c] (exceeded? ad :country c))

(defn channel-limit-exceeded? [ad c] (exceeded? ad :channel c))

(defn live [] (filter live? (all)))

(defn expired [] (filter expired? (all)))

(defn exhausted?
  [ad {:keys [channel country]}]
  (or (global-limit-exceeded? ad)
      (country-limit-exceeded? ad country)
      (channel-limit-exceeded? ad channel)))

(defn available?
  [ad {:keys [channel country] :as m}]
  (and (live? ad) (not (exhausted? ad m)) true))

(defn exhausted
  [{:keys [channel country] :as m}]
  (filter (fn [ad] (exhausted? ad m)) (all)))

(defn available
  [{:keys [channel country] :as m}]
  (filter (fn [ad] (available? ad m)) (all)))

(defn edit-views
  [ad {:keys [country channel]}]
  (let [paths [[:limits :global :views]
               [:limits :country country :views]
               [:limits :channel channel :views]]]
    (reduce (fn [acc ks] (update-in acc ks inc)) ad paths)))

(defn inc-views-aux
  [coll ad {:keys [country channel]}]
  (replace {ad (edit-views ad {:country country :channel channel})} coll))

(defn inc-views
  [ad {:keys [country channel] :as m}]
  (swap! db assoc :coll (inc-views-aux (all) ad m)))
