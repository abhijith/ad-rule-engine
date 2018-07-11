(ns rpm.main
  (:gen-class)
  (:require [rpm.advert :as advert]
            [rpm.category :as category]
            [rpm.channel :as channel]
            [rpm.country :as country]))

(do
  (doseq [x (range 1 4)]
    (advert/save {:id x :label (format "advert-%s" x)}))
  (doseq [x (range 1 4)]
    (category/save {:id x :label (format "category-%s" x)}))
  (doseq [x (range 1 4)]
    (channel/save {:id x :label (format "channel-%s" x)}))
  (doseq [x (range 1 4)]
    (country/save {:id x :label (format "country-%s" x)})))

(println (deref rpm.lib/db))

[(advert/find 0)
 (category/find 0)
 (channel/find 0)
 (country/find 0)]

[(advert/destroy 1)
 (category/destroy 1)
 (channel/destroy 1)
 (country/destroy 1)]

[(advert/count)
 (category/count)
 (channel/count)
 (country/count)]

(println (deref rpm.lib/db))
[(advert/destroy-all)
 (category/destroy-all)
 (channel/destroy-all)
 (country/destroy-all)]
(println (deref rpm.lib/db))
