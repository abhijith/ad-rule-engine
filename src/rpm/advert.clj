(ns rpm.advert
  (:require [rpm.lib]
            [java-time]))

;; {:label "ktm"
;;          :start "jul 1"
;;          :end "jul 7"
;;          :limits {:country {"germany" 10, "india" 100}
;;                   :channel {"team-bhp.com" 100,
;;                             "ktm.com" 10
;;                             :global 200 }}
;;          :constraints '(or (member country ["india", "germany"])
;;                            (= language "english")
;;                            (in categories ["bike", "car"]))}

(def db (atom {:coll '() :count 0}))

(defn make
  [label & {:keys [limit start-date end-date constraints] :as m, :or {limit 1}}]
  (merge {:label label} m))

(defn empty [] {:coll [] :count 0})

(defn table [] @db)

(defn rows [] (:coll @db))

(def all rows)

(defn count [] (:count @db))

(defn find [label] (first (filter #(partial = (:label %1)) @db)

(defn find-by [attr value] (rpm.lib/find-by attr value))

(defn destroy-all [] (rpm.lib/destroy-all))

(defn save [elem] (rpm.lib/save elem))

(defn destroy-by [attr value] (rpm.lib/destroy-by attr value))

(defn destroy [label] (rpm.lib/destroy label))

(defn live?
  "does not differentiate if ad has expired or yet to become live"
  [x]
  (let [now (java-time.local/local-date-time)]
    (and
     (java-time.core/after?  now (:start-date x))
     (java-time.core/before? now (:end-date x)))))

(defn expired?
  "is current date past the end-date"
  [x]
  (java-time.core/after? (java-time.local/local-date-time) (:end-date x)))

(defn live [] (into [] (filter live? (all))))

(defn expired [] (filter expired? (all)))

(defn limits [x] (:limits x))

(defn views-exceeded? [x])

(defn update-views [x])
