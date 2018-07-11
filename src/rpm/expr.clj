(ns rpm.expr)

(defn make [field type operator value]
  {:expr {:field    field,
          :type     type,
          :operator operator,
          :value    value}})

;; {:operator 'clojure.core/=, :field :country, :value "india" :type "str"}

(defn satisfies? [m expr]
  (let [{:keys [operator value field]} expr]
    ((find-var (:operator expr)) (:value expr) ((:field expr) m))))
