(ns rpm.core-test
  (:require [clojure.test :refer :all]
            [rpm.advert]
            [rpm.channel]
            [rpm.country]
            [rpm.core]))

(defn reset-db []
  (do
    (rpm.advert/destroy-all)
    (rpm.channel/destroy-all)
    (rpm.country/destroy-all)
    (rpm.category/destroy-all)))

(defn sample-days []
  (let [now (java-time.local/local-date-time)]
    {:now now
     :yest (java-time.core/minus now (java-time.amount/days 1))
     :tom (java-time.core/plus now (java-time.amount/days 1))
     :bef-yest (java-time.core/minus now (java-time.amount/days 2))
     :aft-tom (java-time.core/plus now (java-time.amount/days 2))}))

(defn init []
  (let [{:keys [now yest tom bef-yest aft-tom]} (sample-days)
        r1 '(and (or (= rpm.core/language "English")
                     (= rpm.core/language "Kannada"))
                 (contains? rpm.core/categories "bikes")
                 (= rpm.core/country "India")
                 (= rpm.core/channel "team-bhp.com"))
        r2 '(and (= rpm.core/language "German")
                 (contains? rpm.core/categories "bikes")
                 (= rpm.core/country "Germany")
                 (= rpm.core/channel "team-bhp.com"))]

    (rpm.channel/save (rpm.channel/make "team-bhp.com" :categories #{"bikes", "cars"}))
    (rpm.country/save (rpm.country/make "India"))
    (rpm.country/save (rpm.country/make "Germany"))

    (rpm.advert/save (rpm.advert/make "ktm"
                                      :start yest
                                      :end aft-tom
                                      :limits {:channel {"team-bhp.com" {:limit 1 :views 0}}
                                               :country {"India" {:limit 1 :views 0}
                                                         "Germany" {:limit 1 :views 0}}
                                               :global {:limit 10 :views 0}}
                                      :rule r1))
    (rpm.advert/save (rpm.advert/make "yamaha"
                                      :start yest
                                      :end aft-tom
                                      :limits {:channel {"team-bhp.com" {:limit 1 :views 0}}
                                               :country {"India" {:limit 1 :views 0}
                                                         "Germany" {:limit 1 :views 0}}
                                               :global {:limit 1 :views 0}}
                                      :rule r2))
    :done))

(use-fixtures :each (fn [f] (init) (f) (reset-db)))

(deftest run
  (testing "run"
    (is (= 1 (count (rpm.core/run {:channel "team-bhp.com" :country "Germany" :language "German"}))))
    (is (= 0 (count (rpm.core/run {:channel "team-bhp.com" :country "Germany" :language "English"}))))
    (is (= 1 (count (rpm.core/run {:channel "team-bhp.com" :country "India" :language "English"}))))
    (is (= 1 (count (rpm.core/run {:channel "team-bhp.com" :country "India" :language "Kannada"}))))
    (is (= 0 (count (rpm.core/run {:channel "team-bhp.com" :country "India" :language "Hindi"}))))))

(deftest match
  (testing "run"
    (rpm.core/match {:channel "team-bhp.com" :country "Germany" :language "German"})
    (rpm.core/match {:channel "team-bhp.com" :country "India" :language "English"})
    (rpm.core/match {:channel "team-bhp.com" :country "India" :language "Kannada"})
    (is (= nil (rpm.core/match {:channel "team-bhp.com" :country "Germany" :language "German"})))
    (is (= nil (rpm.core/match {:channel "team-bhp.com" :country "India" :language "English"})))
    (is (= nil (rpm.core/match {:channel "team-bhp.com" :country "India" :language "Kannada"})))))
