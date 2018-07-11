(ns rpm.expr-test
  (:require [clojure.test :refer :all]
            [rpm.expr]))

;; (find-var (symbol "clojure.core" "=")) ;; to get around using symbols
(deftest make-test
  (testing "make"
    (is (= {:expr {:field 'clojure.core/=,
                   :type "str",
                   :operator :country,
                   :value "india"}}
           (rpm.expr/make 'clojure.core/= "str" :country "india")))))

(deftest satisfies?-test
  (testing "satisfies?"
    (is true? (rpm.expr/satisfies?
               {:channel "team-bhp.com", :country "india", :language "english"}
               {:operator 'clojure.core/=, :field :country, :value "india"}))))
