(ns rpm.data
  (:require [rpm.advert]
            [rpm.channel]
            [rpm.country]
            [rpm.core]))

;; | name         |live?| ad-limit | country-limit | channel-limit | constraints |
;; |:------------:|:---:|:--------:|:-------------:|:-------------:|:------------|
;; | volvo-s40    | Yes | 10       | 2             | 2             | country in `Sweden` and channel is `team-bhp.com` and `cars` is in channel categories and language in (English, Swedish)
;; | bmw-i8       | yes | 2        | 2             | 2             | country in `Germany` and channel is `team-bhp.com` and `cars` in channel categories and language in (English, German)
;; | master-chef  | yes | 10       | 3             | 3             | country in `(Germany, Sweden, India)` and channel is `trip-advisor.com` and `(food, travel)` in channel categories
;; | air-berlin   | yes | 10       | 3             | 3             | country in `(Germany, Sweden)` and channel is `trip-advisor.com` and `travel` is in channel categories and language in (Swedish, English)
;; | coke         | yes | 10       | -             | -             | channel is `reddit.com`
;; | catch-all-ad | yes | 20       | -             | -             | always true
;; | jogurt       | no  | 10       | -             | -             | always true


(def countries ["India",
                "Germany",
                "Sweden"])

(def channels [{:label "team-bhp.com" :categories #{"automobiles", "bikes", "cars", "bmw", "volvo"}},
               {:label "trip-advisor.com", :categories #{"travel", "airlines", "food"}}
               {:label "reddit.com", :categories #{"social", "news"}}])

(def ads ["volvo-s40",
          "bmw-i8",
          "master-chef",
          "air-berlin",
          "coke",
          "catch-all-ad",
          "jogurt"])

(def categories ["automobiles",
                 "bikes",
                 "cars",
                 "bmw",
                 "volvo",
                 "travel",
                 "airlines",
                 "food",
                 "social",
                 "news"])

(def now (java-time.local/local-date-time))
(def yest (java-time.core/minus now (java-time.amount/days 1)))
(def tom (java-time.core/plus now (java-time.amount/days 1)))
(def bef-yest (java-time.core/minus now (java-time.amount/days 2)))

(defn make-limit
  [channel country]
  {:global (rpm.advert/make-limit (+ country channel))
   :country (into {} (map (fn [x] [x (rpm.advert/make-limit country)]) countries))
   :channel (into {} (map (fn [x] [(x :label) (rpm.advert/make-limit channel)]) channels))})

(defn make-ad
  [label]
  (rpm.advert/make label
                   :limits (make-limit 2 2)
                   :start yest
                   :end tom))

(defn init-data
  []
  (doseq [x countries]
    (rpm.country/save (rpm.country/make x)))

  (doseq [x channels]
    (rpm.channel/save (rpm.channel/make (x :label) :categories (x :categories))))

  (doseq [x categories]
    (rpm.category/save (rpm.category/make x)))

  (let [xs (list (assoc (make-ad "volvo-s40")
                        :rule '(and (clojure.core/= rpm.core/country "Sweden")
                                    (clojure.core/= rpm.core/channel "team-bhp.com")
                                    (rpm.core/categories "cars")
                                    (or (clojure.core/= rpm.core/language "Swedish")
                                        (clojure.core/= rpm.core/language "English"))))

                 (assoc (make-ad "bmw-i8")
                        :rule '(and (clojure.core/= rpm.core/country "Germany")
                                    (clojure.core/= rpm.core/channel "team-bhp.com")
                                    (rpm.core/categories "cars")
                                    (or (clojure.core/= rpm.core/language "German")
                                        (clojure.core/= rpm.core/language "English"))))

                 (assoc (make-ad "master-chef")
                        :limits (make-limit 3 3)
                        :rule '(and (clojure.core/some #(= rpm.core/country %) ["Germany", "Sweden", "India"])
                                    (clojure.core/= rpm.core/channel "trip-advisor.com")
                                    (clojure.set/subset? #{"food", "travel"} rpm.core/categories)
                                    (or (clojure.core/= rpm.core/language "Swedish")
                                        (clojure.core/= rpm.core/language "English")
                                        (clojure.core/= rpm.core/language "German"))))

                 (assoc (make-ad "air-berlin")
                        :limits (make-limit 3 3)
                        :rule '(and (clojure.core/some #(= rpm.core/country %) ["Germany", "Sweden"])
                                    (clojure.core/= rpm.core/channel "trip-advisor.com")
                                    (rpm.core/categories "travel")))

                 (assoc (make-ad "coke")
                        :limits {:global {:limit 5 :views 0}}
                        :rule '(clojure.core/= rpm.core/channel "reddit.com"))

                 (assoc (make-ad "catch-all-ad")
                        :limits {:global {:limit 20 :views 0}}
                        :rule 'true)

                 (assoc (make-ad "jogurt") :rule true :start bef-yest :end yest))]
    (doseq [a (reverse xs)]
      (rpm.advert/save a))))

(defn reset-db []
  (rpm.country/destroy-all)
  (rpm.channel/destroy-all)
  (rpm.category/destroy-all)
  (rpm.advert/destroy-all))
