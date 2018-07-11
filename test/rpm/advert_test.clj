(ns rpm.advert-test
  (:require [clojure.test :refer :all]
            [rpm.advert]
            [rpm.lib]))

(use-fixtures :each (fn [f] (f) (rpm.state/reset-db)))

(deftest make-test
  (let [a (rpm.advert/make "a-1")]
    (testing "make"
      (is (= {:label "a-1"} a)))))

(deftest empty-test
  (let [a (rpm.advert/empty)]
    (testing "empty"
      (is (= {:coll [] :count 0} a)))))

(deftest table-test
  (let [a (rpm.advert/table)]
    (testing "table"
      (is (= {:coll [] :count 0} a)))))

(deftest rows-test
  (let [a (rpm.advert/rows)]
    (testing "rows"
      (is (= [] a)))))

(deftest all-test
  (let [a (rpm.advert/all)]
    (testing "all"
      (is (= [] a)))))

(deftest save-test
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "save"
      (is (= {:label "a-1"} a)))))

(deftest find-by-test
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "find-by"
      (is (= a (rpm.advert/find-by :label "a-1"))))))

(deftest find-test
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "find-by"
      (is (= a (rpm.advert/find "a-1"))))))

(deftest count-test
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "count"
      (is (= 1 (rpm.advert/count))))))

(deftest destroy-by-test
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "destroy-by"
      (is (= true (rpm.advert/destroy-by :label "a-1")))
      (is (= [] (rpm.advert/rows))))))

(deftest destroy-test
  (let [a (rpm.advert/save (rpm.advert/make "a-1"))]
    (testing "destroy"
      (is (true? (rpm.advert/destroy "a-1")))
      (is (nil? (rpm.advert/destroy "a-2")))
      (is (empty? (rpm.advert/rows))))))

(defn sample-days []
  (let [now (java-time.local/local-date-time)]
    {:now now
     :yesterday (java-time.core/minus now (java-time.amount/days 1))
     :tomorrow (java-time.core/plus now (java-time.amount/days 1))
     :day-before-yest (java-time.core/minus now (java-time.amount/days 2))
     :day-after-tomorrow (java-time.core/plus now (java-time.amount/days 2))}))

(deftest live?-test
  (let [{:keys [now yesterday tomorrow day-before-yest day-after-tomorrow]} (sample-days)
        live {:start-date yesterday :end-date tomorrow}
        not-live {:start-date tomorrow :end-date day-after-tomorrow}]
    (testing "live?"
      (is true? (rpm.advert/live? live))
      (is false? (rpm.advert/live? not-live)))))

(deftest expired?-test
  (let [{:keys [now yesterday tomorrow day-before-yest day-after-tomorrow]} (sample-days)
        expired {:start-date day-before-yest :end-date yesterday}
        not-expired {:start-date day-before-yest :end-date tomorrow}]
    (testing "expired?"
      (is true? (rpm.advert/expired? expired))
      (is false? (rpm.advert/expired? not-expired)))))

(deftest live-test
  (let [{:keys [now yesterday tomorrow day-before-yest day-after-tomorrow]} (sample-days)
        expired {:start-date day-before-yest :end-date yesterday}
        not-expired {:start-date day-before-yest :end-date tomorrow}
        live (rpm.advert/save (rpm.advert/make "a-1" :start-date yesterday :end-date tomorrow))
        not-live (rpm.advert/save (rpm.advert/make "a-2" :start-date tomorrow :end-date day-after-tomorrow))]
    (testing "live"
      (is (= 1 (count (rpm.advert/live)))))))

(deftest expired-test
  (let [{:keys [now yesterday tomorrow day-before-yest day-after-tomorrow]} (sample-days)
        expired (rpm.advert/save (rpm.advert/make "a-1" :start-date day-before-yest :end-date yesterday))
        not-expired (rpm.advert/save (rpm.advert/make "a-2" :start-date yesterday :end-date day-after-tomorrow))]
    (testing "expired"
      (is (= 1 (count (rpm.advert/expired)))))))
