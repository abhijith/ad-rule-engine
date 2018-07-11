(ns rpm.country-test
  (:require [clojure.test :refer :all]
            [rpm.country]))

(use-fixtures :each (fn [f] (f) (rpm.country/destroy-all)))

(deftest test-make
  (let [a (rpm.country/make "country-1")]
    (testing "make"
      (is (= {:label "country-1"} a)))))

(deftest test-empty
  (let [a (rpm.country/empty)]
    (testing "empty"
      (is (= {:coll [] :count 0} a)))))

(deftest test-table
  (let [a (rpm.country/table)]
    (testing "table"
      (is (= {:coll [] :count 0} a)))))

(deftest test-rows
  (let [a (rpm.country/rows)]
    (testing "rows"
      (is (= [] a)))))

(deftest test-all
  (let [a (rpm.country/all)]
    (testing "all"
      (is (= [] a)))))

(deftest test-save
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "save"
      (is (= {:label "country-1"} a)))))

(deftest test-find-by
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "find-by"
      (is (= a (rpm.country/find-by :label "country-1"))))))

(deftest test-find
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "find-by"
      (is (= a (rpm.country/find "country-1"))))))

(deftest test-count
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "count"
      (is (= 1 (rpm.country/count))))))

(deftest test-destroy-by
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "destroy-by"
      (is (= true (rpm.country/destroy-by :label "country-1")))
      (is (= [] (rpm.country/rows))))))

(deftest test-destroy
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "destroy"
      (is (true? (rpm.country/destroy "country-1")))
      (is (nil? (rpm.country/destroy "country-2")))
      (is (empty? (rpm.country/rows))))))
