(ns rpm.state)

(def db (atom {:advert   {:coll [] :count 0}
               :category {:coll [] :count 0}
               :channel  {:coll [] :count 0}
               :country  {:coll [] :count 0}}))

(defn reset-db []
  (reset! db {:advert   {:coll [] :count 0}
              :category {:coll [] :count 0}
              :channel  {:coll [] :count 0}
              :country  {:coll [] :count 0}}))
