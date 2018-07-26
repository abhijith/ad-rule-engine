(ns rpm.advert
  (:require [java-time]))

;; TODO: spec for all fns and entities

(def db (atom {:coll '() :count 0}))

(defn make-limit
  "Takes a number and returns a map with limit type."
  [n]
  {:limit n :views 0})

(defn make
  "Takes a label, optional keyword args and creates an entity map."
  [label & {:keys [limits start end rule] :as m}]
  (merge {:label label} m))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; advert attrs specific fns ;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn limits
  "Returns the limits, given an advert"
  [ad]
  (:limits ad))

(defn limit
  "Returns the limit for type, given an advert and type (channel |
  country | global)"
  [ad type]
  (type (limits ad)))

(defn views
  "Returns the views for a type, given an advert and type (channel |
  country | global)"
  [ad type]
  (type (limits ad)))

(defn live?
  "Returns true if current time is between advert's start and end
  time."
  [ad]
  (let [now (java-time.local/local-date-time)]
    (and
     (java-time.core/after?  now (:start ad))
     (java-time.core/before? now (:end ad)))))

(defn expired?
  "Returns true if current time is after advert's end time"
  [ad]
  (java-time.core/after? (java-time.local/local-date-time) (:end ad)))

(defn limit-exceeded?
  "Returns true if views are >= limit, given a limit entity"
  [limit]
  (if limit
    (>= (:views limit) (:limit limit))
    false))

(defn global-limit-exceeded?
  "Returns true if total ad views are >= limit given an ad"
  [ad]
  (limit-exceeded? (:global (limits ad))))

(defn exceeded?
  "Returns true if ad views are >= limit, given an ad and attr->value"
  [ad type-attr type-value]
  (limit-exceeded? (get-in (limits ad) [type-attr type-value])))

(defn country-limit-exceeded?
  "Returns true if ad views are >= limit, given an ad and country
  label"
  [ad c]
  (exceeded? ad :country c))

(defn channel-limit-exceeded?
  "Returns true if ad views are >= limit, given an ad and channel
  label"
  [ad c]
  (exceeded? ad :channel c))

(defn live
  "Returns coll of live ads"
  []
  (filter live? (all)))

(defn expired
  "Returns coll of expired ads"
  []
  (filter expired? (all)))

(defn exhausted?
  "Returns true if any of ad views (global | country | channel) are >=
  than corresponding limit, given country and channel"
  [ad {:keys [channel country]}]
  (or (global-limit-exceeded? ad)
      (country-limit-exceeded? ad country)
      (channel-limit-exceeded? ad channel)))

(defn available?
  "Returns true if ad is live? and not exhausted?, given country and
  channel"
  [ad {:keys [channel country] :as m}]
  (and (live? ad) (not (exhausted? ad m)) true))

(defn exhausted
  "Returns coll of exhausted ads"
  [{:keys [channel country] :as m}]
  (filter (fn [ad] (exhausted? ad m)) (all)))

(defn available
  "Returns coll of available ads"
  [{:keys [channel country] :as m}]
  (filter (fn [ad] (available? ad m)) (all)))

(defn edit-views
  "Returns ad with views incremented, given country and channel"
  [ad {:keys [country channel]}]
  (let [paths [[:limits :global :views]
               [:limits :country country :views]
               [:limits :channel channel :views]]]
    (reduce (fn [acc ks]
              (if (get-in acc ks)
                (update-in acc ks inc) acc))
            ad paths)))

(defn inc-views-aux
  "Returns a new coll of ads with views incremented, given country and channel"
  [coll ad {:keys [country channel]}]
  (replace {ad (edit-views ad {:country country :channel channel})} coll))

(defn inc-views
  "Atomically update the views in db for ad, given country and channel"
  [ad {:keys [country channel] :as m}]
  (when-let [updated-ad (inc-views-aux (all) ad m)]
    (swap! db assoc :coll updated-ad)
    ad))
