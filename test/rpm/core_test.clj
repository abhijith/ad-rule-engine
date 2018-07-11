(ns rpm.core-test
  (:require [clojure.test :refer :all]
            [rpm.advert]
            [rpm.channel]
            [rpm.country]
            [rpm.category]))

(defn init []
  (doseq [x (range 1 2)]
    (rpm.advert/make "a-1")))

(deftest run
  (testing "run"
    (is (= 1 1))))
