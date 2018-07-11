(ns rpm.category-test
  (:require [clojure.test :refer :all]
            [rpm.category :refer :all]))

(use-fixtures :each (fn [f] (f) (rpm.category/destroy-all)))

(deftest test-make
  (let [a (rpm.category/make "cat-1")]
    (testing "make"
      (is (= {:label "cat-1" :parent nil} a)))))

(deftest test-empty
  (let [a (rpm.category/empty)]
    (testing "empty"
      (is (= {:coll [] :count 0} a)))))

(deftest test-table
  (let [a (rpm.category/table)]
    (testing "table"
      (is (= {:coll [] :count 0} a)))))

(deftest test-rows
  (let [a (rpm.category/rows)]
    (testing "rows"
      (is (= [] a)))))

(deftest test-all
  (let [a (rpm.category/all)]
    (testing "all"
      (is (= [] a)))))

(deftest test-save
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "save"
      (is (= {:label "cat-1" :parent nil} a)))))

(deftest test-find-by
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "find-by"
      (is (= a (rpm.category/find-by :label "cat-1"))))))

(deftest test-find
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "find-by"
      (is (= a (rpm.category/find "cat-1"))))))

(deftest test-count
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "count"
      (is (= 1 (rpm.category/count))))))

(deftest test-destroy-by
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "destroy-by"
      (is (= true (rpm.category/destroy-by :label "cat-1")))
      (is (= [] (rpm.category/rows))))))

(deftest test-destroy
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "destroy"
      (is (true? (rpm.category/destroy "cat-1")))
      (is (nil? (rpm.category/destroy "cat-2")))
      (is (empty? (rpm.category/rows))))))
