(ns rpm.country-test
  (:require [clojure.test :refer :all]
            [rpm.country]))

(use-fixtures :each (fn [f] (f) (rpm.country/destroy-all)))

(deftest test-make
  (let [a (rpm.country/make "country-1")]
    (testing "make"
      (is (= {:label "country-1"} a)))))

(deftest test-empty
  (let [a (rpm.country/empty-table)]
    (testing "empty"
      (is (= {:coll [] :count 0} a)))))

(deftest test-table
  (let [a (rpm.country/table)]
    (testing "table"
      (is (= {:coll [] :count 0} a)))))

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

(deftest test-find-entry
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "find-entry"
      (is (= a (rpm.country/find-entry "country-1"))))))

(deftest test-get-count
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "count"
      (is (= 1 (rpm.country/get-count))))))

(deftest test-destroy-by
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "destroy-by"
      (is (= true (rpm.country/destroy-by :label "country-1")))
      (is (= [] (rpm.country/all))))))

(deftest test-destroy
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "destroy"
      (is (true? (rpm.country/destroy "country-1")))
      (is (nil? (rpm.country/destroy "country-2")))
      (is (empty? (rpm.country/all))))))

(deftest test-save-aux
  (let [a (rpm.country/save-aux (rpm.country/empty-table) (rpm.country/make "country-1"))]
    (testing "save"
      (is (= {:coll '({:label "country-1"}) :count 1} a)))))

(deftest test-find-by-aux
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "find-by-aux"
      (is (= a (rpm.country/find-by-aux (rpm.country/all) :label "country-1"))))))

(deftest test-delete
  (let [a (rpm.country/save (rpm.country/make "country-1"))]
    (testing "delete"
      (is (= (rpm.country/empty-table) (rpm.country/delete (deref rpm.country/db) a))))))
