(ns rpm.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.defaults :refer :all]
            [compojure.route :as route]
            [rpm.data]
            [rpm.advert]
            [rpm.channel]
            [rpm.country]
            [rpm.category]))

(def ^:dynamic channel nil)
(def ^:dynamic country nil)
(def ^:dynamic language nil)
(def ^:dynamic categories nil)

(defn bind
  "Returns a bindings map, given a map"
  [{:keys [channel country language categories]}]
  {#'channel channel
   #'country country
   #'language language
   #'categories categories})

;; TODO: Use core.spec for rule engine
;;       store expression -> result mapping in a data structure (for debugging)
(defn qualifies?
  "Returns true, given env (bindings) and rule."
  [env rule]
  (let [{:keys [channel country language categories]} env]
    (with-bindings (bind env) (eval rule))))

(defn run
  "Returns a coll of ads which qualifies?, given a request map"
  [req]
  (let [{:keys [channel country language]} req]
    (when-let [ch (rpm.channel/find-entry channel)]
      (filter (fn [ad]
                (qualifies? (assoc req :categories (:categories ch)) (:rule ad)))
              (rpm.advert/available {:channel channel :country country})))))

(defn match
  "Returns a coll of ads which qualifies?, given a request map"
  [req]
  (when-let [ad (first (run req))]
    (rpm.advert/inc-views ad req)))

;;; URLS

;; | url         | type   | params                     | desc                                      |
;; |:------------|:------:|:--------------------------:|:-----------------------------------------:|
;; | /           | POST   | None                       | Initializes sample data
;; | /           | DELETE | None                       | Clears the sample data
;; | /ads/:label | GET    | label                      | Gets the advertisement matching the label
;; | /ads/       | GET    | channel, country, language | returns a matching ad (if any)

(defroutes app-routes
  (GET "/" [] "hello")
  (POST "/" [] (do (rpm.data/init-data) "done"))
  (DELETE "/" [] (do (rpm.data/reset-db) "done"))
  (GET "/ads/:label" [label] (str (rpm.advert/find-entry label)))
  (GET "/ads" req (str (match (select-keys (:params req) [:channel :country :language])))))

(defn -main []
  (run-server (wrap-defaults app-routes api-defaults) {:port 5000}))
