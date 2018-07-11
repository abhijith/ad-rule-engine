(ns rpm.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]])) ; httpkit is a server

(defroutes myapp
  (GET "/" [] "Show something")
  (POST "/" [] "Create something")
  (PUT "/" [] "Replace something")
  (PATCH "/" [] "Modify Something")
  (DELETE "/" [] "Annihilate something")
  (OPTIONS "/" [] "Appease something")
  (HEAD "/" [] "Preview something"))

(defn -main []
  (run-server myapp {:port 5000}))
