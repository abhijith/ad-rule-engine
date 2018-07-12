(ns rpm.advert-test
  (:require [clojure.test :refer :all]
            [rpm.advert]))

(use-fixtures :each (fn [f] (f) (rpm.advert/destroy-all)))

(deftest test-make
  (let [a (rpm.advert/make "a-1")]
    (testing "make"
      (is (= {:label "a-1"} a)))))

(deftest test-empty
  (let [a (rpm.advert/empty-table)]
    (testing "empty"
      (is (= {:coll '() :count 0} a)))))

(deftest test-table
  (let [a (rpm.advert/table)]
    (testing "table"
      (is (= {:coll '() :count 0} a)))))

(deftest test-all
  (let [a (rpm.advert/all)]
    (testing "all"
      (is (= '() a)))))

(deftest test-save
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "save"
      (is (= {:label "a-1"} a)))))

(deftest test-find-by
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "find-by"
      (is (= a (rpm.advert/find-by :label "a-1"))))))

(deftest test-find-entry
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "find-entry"
      (is (= a (rpm.advert/find-entry "a-1"))))))

(deftest test-get-count
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "count"
      (is (= 1 (rpm.advert/get-count))))))

(deftest test-destroy-by
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "destroy-by"
      (is (= true (rpm.advert/destroy-by :label "a-1")))
      (is (= '() (rpm.advert/all))))))

(deftest test-destroy
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "destroy"
      (is (true? (rpm.advert/destroy "a-1")))
      (is (nil? (rpm.advert/destroy "a-2")))
      (is (empty? (rpm.advert/all))))))

(deftest test-destroy-all
  (doseq [a (range 1 3)] (rpm.advert/save (rpm.advert/make (format "a-%s" a))))
  (testing "destroy"
    (is (= {:coll '() :count 0} (rpm.advert/destroy-all)))))

(defn sample-days []
  (let [now (java-time.local/local-date-time)]
    {:now now
     :yest (java-time.core/minus now (java-time.amount/days 1))
     :tom (java-time.core/plus now (java-time.amount/days 1))
     :bef-yest (java-time.core/minus now (java-time.amount/days 2))
     :aft-tom (java-time.core/plus now (java-time.amount/days 2))}))

(deftest test-live?
  (let [{:keys [now yest tom bef-yest aft-tom]} (sample-days)
        live {:start yest :end tom}
        not-live {:start tom :end aft-tom}]
    (testing "live?"
      (is true? (rpm.advert/live? live))
      (is false? (rpm.advert/live? not-live)))))

(deftest test-expired?
  (let [{:keys [now yest tom bef-yest aft-tom]} (sample-days)
        expired {:start bef-yest :end yest}
        not-expired {:start bef-yest :end tom}]
    (testing "expired?"
      (is true? (rpm.advert/expired? expired))
      (is false? (rpm.advert/expired? not-expired)))))

(deftest test-live
  (let [{:keys [now yest tom bef-yest aft-tom]} (sample-days)
        expired {:start bef-yest :end yest}
        not-expired {:start bef-yest :end tom}
        live (rpm.advert/save (rpm.advert/make "a-1" :start yest :end tom))
        not-live (rpm.advert/save (rpm.advert/make "a-2" :start tom :end aft-tom))]
    (testing "live"
      (is (= 1 (count (rpm.advert/live)))))))

(deftest test-expired
  (let [{:keys [now yest tom bef-yest aft-tom]} (sample-days)
        expired (rpm.advert/save (rpm.advert/make "a-1" :start bef-yest :end yest))
        not-expired (rpm.advert/save (rpm.advert/make "a-2" :start yest :end aft-tom))]
    (testing "expired"
      (is (= 1 (count (rpm.advert/expired)))))))

(deftest test-limit-exceeded?
  (testing "limit exceeded?"
    (is (= false (rpm.advert/limit-exceeded? {:limit 1 :views 0})))
    (is (= true (rpm.advert/limit-exceeded?  {:limit 1 :views 1})))))

(deftest test-global-limit-exceeded?
  (let [yes (rpm.advert/make :a :limits {:global {:limit 1 :views 1}})
        no (rpm.advert/make :a :limits {:global {:limit 1 :views 0}})]
    (testing "limit exceeded?"
      (is (= false (rpm.advert/global-limit-exceeded? no)))
      (is (= true  (rpm.advert/global-limit-exceeded? yes))))))

(deftest test-country-limit-exceeded?
  (let [yes (rpm.advert/make :a :limits {:country {:india {:limit 1 :views 1}}})
        no (rpm.advert/make :a :limits {:country {:india {:limit 1 :views 0}}})]
    (testing "limit exceeded?"
      (is (= false (rpm.advert/country-limit-exceeded? no :india)))
      (is (= true  (rpm.advert/country-limit-exceeded? yes :india))))))

(deftest test-channel-limit-exceeded?
  (let [yes (rpm.advert/make :a :limits {:channel {:a.com {:limit 1 :views 1}}})
        no (rpm.advert/make :a :limits {:channel {:a.com {:limit 1 :views 0}}})]
    (testing "limit exceeded?"
      (is (= false (rpm.advert/channel-limit-exceeded? no :a.com)))
      (is (= true  (rpm.advert/channel-limit-exceeded? yes :a.com))))))

(deftest test-exhausted?
  (let [yes (rpm.advert/make :a :limits {:channel {:a.com {:limit 1 :views 1}}
                                         :country {:india {:limit 1 :views 1}}
                                         :global {:limit 1 :views 1}})
        no (rpm.advert/make :a :limits {:channel {:a.com {:limit 1 :views 0}}
                                        :country {:india {:limit 1 :views 0}}
                                        :global {:limit 1 :views 0}})]
    (is (= false (rpm.advert/exhausted? no {:country :india :channel :a.com })))
    (is (= true  (rpm.advert/exhausted? yes {:country :india :channel :a.com })))))

(deftest test-exhausted
  (let [yes (rpm.advert/save (rpm.advert/make :a :limits {:channel {:a.com {:limit 1 :views 1}}
                                                          :country {:india {:limit 1 :views 1}}
                                                          :global {:limit 1 :views 1}}))
        no (rpm.advert/save (rpm.advert/make :a :limits {:channel {:a.com {:limit 1 :views 0}}
                                                         :country {:india {:limit 1 :views 0}}
                                                         :global {:limit 1 :views 0}}))]
    (is (= 2 (rpm.advert/get-count)))
    (is (= 1 (count (rpm.advert/exhausted {:country :india :channel :a.com }))))
    (is (= 1 (count (rpm.advert/exhausted {:country :india :channel :a.com }))))))

(deftest test-available?
  (let [{:keys [now yest tom bef-yest aft-tom]} (sample-days)
        yes (rpm.advert/make :a :start yest :end aft-tom
                             :limits {:channel {:a.com {:limit 1 :views 0}}
                                      :country {:india {:limit 1 :views 0}}
                                      :global {:limit 1 :views 0}})
        no (rpm.advert/make :a :start bef-yest :end yest
                            :limits {:channel {:a.com {:limit 1 :views 1}}
                                     :country {:india {:limit 1 :views 1}}
                                     :global {:limit 1 :views 1}})]
    (is (= false (rpm.advert/available? no {:country :india :channel :a.com })))
    (is (= true  (rpm.advert/available? yes {:country :india :channel :a.com })))))

(deftest test-available
  (let [{:keys [now yest tom bef-yest aft-tom]} (sample-days)
        yes (rpm.advert/save (rpm.advert/make :a :start yest :end aft-tom
                                              :limits {:channel {:a.com {:limit 1 :views 0}}
                                                       :country {:india {:limit 1 :views 0}}
                                                       :global {:limit 1 :views 0}}))
        no (rpm.advert/save (rpm.advert/make :a :start bef-yest :end yest
                                             :limits {:channel {:a.com {:limit 1 :views 1}}
                                                      :country {:india {:limit 1 :views 1}}
                                                      :global {:limit 1 :views 1}}))]
    (is (= 2 (rpm.advert/get-count)))
    (is (= 1 (count (rpm.advert/available {:country :india :channel :a.com}))))
    (is (= 1 (count (rpm.advert/available {:country :india :channel :a.com}))))))
