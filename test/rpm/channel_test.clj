(ns rpm.channel-test
  (:require [clojure.test :refer :all]
            [rpm.channel]))

(use-fixtures :each (fn [f] (f) (rpm.channel/destroy-all)))

(deftest test-make
  (let [a (rpm.channel/make "ch-1")]
    (testing "make"
      (is (= {:label "ch-1" :categories #{}} a)))))

(deftest test-empty
  (let [a (rpm.channel/empty)]
    (testing "empty"
      (is (= {:coll [] :count 0} a)))))

(deftest test-table
  (let [a (rpm.channel/table)]
    (testing "table"
      (is (= {:coll [] :count 0} a)))))

(deftest test-rows
  (let [a (rpm.channel/rows)]
    (testing "rows"
      (is (= [] a)))))

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

(deftest test-find
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "find-by"
      (is (= a (rpm.channel/find "ch-1"))))))

(deftest test-count
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "count"
      (is (= 1 (rpm.channel/count))))))

(deftest test-destroy-by
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "destroy-by"
      (is (= true (rpm.channel/destroy-by :label "ch-1")))
      (is (= [] (rpm.channel/rows))))))

(deftest test-destroy
  (let [a (rpm.channel/save (rpm.channel/make "ch-1"))]
    (testing "destroy"
      (is (true? (rpm.channel/destroy "ch-1")))
      (is (nil? (rpm.channel/destroy "ch-2")))
      (is (empty? (rpm.channel/rows))))))
