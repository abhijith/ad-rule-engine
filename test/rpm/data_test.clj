(ns rpm.data-test
  (:require [clojure.set])
  (:require [clojure.test :refer :all]
            [rpm.advert]
            [rpm.channel]
            [rpm.country]
            [rpm.core]
            [rpm.data]))

(use-fixtures :each (fn [f]
                      (rpm.data/init-data)
                      (f)
                      (rpm.data/reset-db)))

(defn assert-ad
  [req label]
  (let [in (rpm.advert/find-entry label)
        out (rpm.core/match req)]
    (is (= (:label in) (:label out)))))

(deftest flow
  (testing "flow"
    (is (= 7 (rpm.advert/get-count)))
    (is (= 3 (rpm.country/get-count)))
    (is (= 3 (rpm.channel/get-count)))
    (is (= 10 (rpm.category/get-count)))

    (assert-ad {:channel "team-bhp.com"     :country "Sweden" :language "English"} "volvo-s40")
    (assert-ad {:channel "team-bhp.com"     :country "Sweden" :language "Swedish"} "volvo-s40")

    (assert-ad {:channel "team-bhp.com"     :country "Germany" :language "English"} "bmw-i8")
    (assert-ad {:channel "team-bhp.com"     :country "Germany" :language "German"} "bmw-i8")

    (assert-ad {:channel "trip-advisor.com" :country "India" :language "English"} "master-chef")
    (assert-ad {:channel "trip-advisor.com" :country "Germany" :language "German"} "master-chef")
    (assert-ad {:channel "trip-advisor.com" :country "Sweden" :language "Swedish"} "master-chef")

    (assert-ad {:channel "trip-advisor.com" :country "Sweden" :language "English"} "air-berlin")
    (assert-ad {:channel "trip-advisor.com" :country "Germany" :language "German"} "air-berlin")

    (assert-ad {:channel "trip-advisor.com" :country "India" :language "English"} "catch-all-ad")

    (doseq [x (range 1 6)]
      (assert-ad {:channel "reddit.com"} "coke"))

    (doseq [x (range 1 20)]
      (assert-ad {:channel "reddit.com"} "catch-all-ad"))))
