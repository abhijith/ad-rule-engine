(ns rpm.channel-test
  (:require [clojure.test :refer :all]
            [rpm.channel]))

(use-fixtures :each (fn [f] (f) (rpm.channel/destroy-all)))

(deftest test-make
  (let [a (rpm.channel/make "ch-1")]
    (testing "make"
      (is (= {:label "ch-1" :categories #{}} a)))))

(deftest test-empty-table
  (let [a (rpm.channel/empty-table)]
    (testing "empty"
      (is (= {:coll [] :count 0} a)))))

(deftest test-table
  (let [a (rpm.channel/table)]
    (testing "table"
      (is (= {:coll [] :count 0} a)))))

(deftest test-all
  (let [a (rpm.channel/all)]
    (testing "all"
      (is (= [] a)))))

(deftest test-save
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "save"
      (is (= {:label "ch-1" :categories #{}} a)))))

(deftest test-find-by
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "find-by"
      (is (= a (rpm.channel/find-by :label "ch-1"))))))

(deftest test-find-entry
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "find-by"
      (is (= a (rpm.channel/find-entry "ch-1"))))))

(deftest test-get-count
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "count"
      (is (= 1 (rpm.channel/get-count))))))

(deftest test-destroy-by
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "destroy-by"
      (is (= true (rpm.channel/destroy-by :label "ch-1")))
      (is (= [] (rpm.channel/all))))))

(deftest test-destroy
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "destroy"
      (is (true? (rpm.channel/destroy "ch-1")))
      (is (nil? (rpm.channel/destroy "ch-2")))
      (is (empty? (rpm.channel/all))))))

(deftest test-save-aux
  (let [a (rpm.channel/save-aux (rpm.channel/empty-table) (rpm.channel/make "ch-1"))]
    (testing "save"
      (is (= {:coll '({:label "ch-1" :categories #{}}) :count 1} a)))))

(deftest test-find-by-aux
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "find-by-aux"
      (is (= a (rpm.channel/find-by-aux (rpm.channel/all) :label "ch-1"))))))

(deftest test-delete
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "delete"
      (is (= (rpm.channel/empty-table) (rpm.channel/delete (deref rpm.channel/db) a))))))
