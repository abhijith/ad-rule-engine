(ns rpm.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [rpm.advert]
            [rpm.channel]
            [rpm.country]
            [rpm.category]))


(defroutes myapp
  (GET "/" [] "Show something")
  (POST "/" [] "Create something")
  (PUT "/" [] "Replace something")
  (PATCH "/" [] "Modify Something")
  (DELETE "/" [] "Annihilate something")
  (OPTIONS "/" [] "Appease something")
  (HEAD "/" [] "Preview something"))

;; rename this to -main to enable
(defn main []
  (run-server myapp {:port 5000}))

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

;; complete flow
(defn run [req]
  (let [{:keys [channel country language]} req]
    (if-let [ch (rpm.channel/find channel)]
      (if-let [co (rpm.country/find country)]
        (if-let [cat (:categories ch)]
          (first (filter qualifies? (rpm.advert/available))))))))
