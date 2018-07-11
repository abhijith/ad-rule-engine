(ns rpm.expr-group)

(defn make [cond & exprs]
  {:group {:exprs exprs} :cond cond})
