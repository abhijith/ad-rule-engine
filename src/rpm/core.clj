(ns rpm.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [rpm.advert]
            [rpm.channel]
            [rpm.country]
            [rpm.category]))

(def ^:dynamic channel nil)
(def ^:dynamic country nil)
(def ^:dynamic language nil)
(def ^:dynamic categories nil)

(defn bind
  [{:keys [channel country language categories]}]
  {#'channel channel
   #'country country
   #'language language
   #'categories categories})

(defn qualifies? [env rule]
  (let [{:keys [channel country language categories]} env]
    (with-bindings (bind env) (eval rule))))

(defn run
  [req]
  (let [{:keys [channel country language]} req]
    (filter (fn [ad]
              (qualifies? (assoc req :categories (:categories channel)) (:rule ad)))
            (rpm.advert/available {:channel channel :country country}))))

(defn match [req]
  (when-let [ad (first (run req))]
    (rpm.advert/inc-views ad)
    ad))
