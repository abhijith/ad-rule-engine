(ns rpm.category-test
  (:require [clojure.test :refer :all]
            [rpm.category :refer :all]))

(use-fixtures :each (fn [f] (f) (rpm.category/destroy-all)))

(deftest test-make
  (let [a (rpm.category/make "cat-1")]
    (testing "make"
      (is (= {:label "cat-1" :parent nil} a)))))

(deftest test-empty-table
  (let [a (rpm.category/empty-table)]
    (testing "empty"
      (is (= {:coll [] :count 0} a)))))

(deftest test-table
  (let [a (rpm.category/table)]
    (testing "table"
      (is (= {:coll [] :count 0} a)))))

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

(deftest test-find-entry
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "find-by"
      (is (= a (rpm.category/find-entry "cat-1"))))))

(deftest test-get-count
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "count"
      (is (= 1 (rpm.category/get-count))))))

(deftest test-destroy-by
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "destroy-by"
      (is (= true (rpm.category/destroy-by :label "cat-1")))
      (is (= [] (rpm.category/all))))))

(deftest test-destroy
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "destroy"
      (is (true? (rpm.category/destroy "cat-1")))
      (is (nil? (rpm.category/destroy "cat-2")))
      (is (empty? (rpm.category/all))))))

(deftest test-save-aux
  (let [a (rpm.category/save-aux (rpm.category/empty-table) (rpm.category/make "cat-1"))]
    (testing "save"
      (is (= {:coll '({:label "cat-1" :parent nil}) :count 1} a)))))

(deftest test-find-by-aux
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "find-by-aux"
      (is (= a (rpm.category/find-by-aux (rpm.category/all) :label "cat-1"))))))

(deftest test-delete
  (let [a (rpm.category/save (rpm.category/make "cat-1"))]
    (testing "delete"
      (is (= (rpm.category/empty-table) (rpm.category/delete (deref rpm.category/db) a))))))
