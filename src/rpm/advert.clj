(ns rpm.advert
  (:require [java-time]))

(def db (atom {:coll '() :count 0}))

(defn make-limit [n] {:limit n :views 0})

(defn make
  [label & {:keys [limits start end rule]
            :as m,
            :or {limits {:global (make-limit 1)
                         :country {}
                         :channel {} }}}]
  (merge {:label label} m))

(defn empty-table [] {:coll '() :count 0})

(defn table [] (deref db))

(defn all [] (:coll (deref db)))

(defn save
  [elem]
  (do (swap! db #(-> % (update :coll conj elem) (update :count inc)))
      elem))

(defn find-by [attr value]
  (first (filter (fn [x] (= (attr x) value)) (all))))

(defn find-entry [label] (first (filter (fn [ad] (= (:label ad) label)) (all))))

(defn get-count [] (:count (deref db)))

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

(defn destroy-all []
  (reset! db (empty-table)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; advert attrs specific fns ;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn limits [ad] (:limits ad))

(defn limit [ad type] (get-in [type :limit] (limits ad)))

(defn views [ad type] (get-in [type :views] (limits ad)))

(defn live?
  [x]
  (let [now (java-time.local/local-date-time)]
    (and
     (java-time.core/after?  now (:start x))
     (java-time.core/before? now (:end x)))))

(defn expired? [x] (java-time.core/after? (java-time.local/local-date-time) (:end x)))

(defn limit-exceeded? [limit] (>= (:views limit) (:limit limit)))

(defn global-limit-exceeded? [ad] (limit-exceeded? (:global (limits ad))))

(defn country-limit-exceeded? [ad country] (limit-exceeded? (get-in (limits ad) [:country country])))

(defn channel-limit-exceeded? [ad channel] (limit-exceeded? (get-in (limits ad) [:channel channel])))

(defn live [] (filter live? (all)))

(defn expired [] (filter expired? (all)))

(defn exhausted?
  [ad {:keys [channel country]}]
  (and (global-limit-exceeded? ad)
       (country-limit-exceeded? ad country)
       (channel-limit-exceeded? ad channel)
       true))

(defn available?
  [ad {:keys [channel country] :as m}]
  (and (live? ad) (not (exhausted? ad m)) true))

(defn exhausted
  [{:keys [channel country] :as m}]
  (filter (fn [ad] (exhausted? ad m)) (all)))

(defn available
  [{:keys [channel country] :as m}]
  (filter (fn [ad] (available? ad m)) (all)))
