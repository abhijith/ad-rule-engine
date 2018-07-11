(ns rpm.advert
  (:require [java-time]))

;; {:label "ktm"
;;          :start "jul 1"
;;          :end "jul 7"
;;          :limits {:country {"germany" {:lim 10 :view 20 }, "india" {:lim 100 :view 10} }
;;                   :channel {"team-bhp.com" {:lim 10 :view 20},
;;                             "ktm.com" {:lim 10 :view 20}
;;                             :global {:lim 100 :view 20} }}
;;          :rule '(or (member country ["india", "germany"])
;;                            (= language "english")
;;                            (in categories ["bike", "car"]))}

;; crud => create/make, read/find, update/edit, remove/destroy

(def db (atom {:coll '() :count 0}))

;; operates on single entity

(defn make
  [label & {:keys [limits start end rule] :as m, :or {limits {:global 0}}}]
  (merge {:label label} m))

(defn edit [ad]
  "find ad and update attrs"
  :edit)

(defn set-limits [ad limits] (swap! db assoc :limits limits))

(defn limits [ad] (:limits ad))

(defn limit [ad type] (type (limits ad)))

(defn save [elem]
  (swap! db (fn [a]
              (-> a
                  (update-in [:coll] conj elem)
                  (update-in [:count] inc))))
  elem)

(defn live?
  "does not differentiate if ad has expired or yet to become live"
  [x]
  (let [now (java-time.local/local-date-time)]
    (and
     (java-time.core/after?  now (:start x))
     (java-time.core/before? now (:end x)))))

(defn expired?
  "is current date past the end-date"
  [x]
  (java-time.core/after? (java-time.local/local-date-time) (:end x)))

;; operates on coll

(defn table [] (deref db))

(defn rows [] (:coll (deref db)))

(def all rows)

(defn empty [] {:coll '() :count 0})

(defn count [] (:count (deref db)))

(defn find [label] (first (filter (fn [ad] (= (:label ad) label)) (all))))

(defn find-by [attr value]
  (first (filter (fn [x] (= (attr x) value)) (rows))))

(defn destroy-by [attr value]
  (if-let [elem (find-by attr value)]
    (do
      (swap! db
             (fn [a]
               (-> a
                   (update-in [:coll] #(remove (fn [row] (= (attr row) value)) %1))
                   (update-in [:count] dec))))
      true)))

(defn destroy [label]
  (destroy-by :label label))

(defn destroy-all []
  (reset! db {:coll '() :count 0}))

(defn live [] (into [] (filter live? (all))))

(defn expired [] (filter expired? (all)))

(defn views-exceeded? [x])

(defn update-views [x])
