#### Sample data

##### Countries

* Germany
* India
* Sweden

##### Channels

* reddit.com
* team-bhp.com
* trip-advisor.com

##### Categories

* automobiles
* bikes
* cars
* bmw
* volvo
* travel
* airlines
* food
* social
* news

##### Advertisements and rules

| name         |live?| ad-limit | country-limit | channel-limit | rule        |
|:------------:|:---:|:--------:|:-------------:|:-------------:|:------------|
| volvo-s40    | yes | 10       | 2             | 2             | country in `Sweden` and channel is `team-bhp.com` and `cars` is in channel categories and language in (English, Swedish)
| bmw-i8       | yes | 2        | 2             | 2             | country in `Germany` and channel is `team-bhp.com` and `cars` in channel categories and language in (English, German)
| master-chef  | yes | 10       | 3             | 3             | country in `(Germany, Sweden, India)` and channel is `trip-advisor.com` and `(food, travel)` in channel categories
| air-berlin   | yes | 10       | 3             | 3             | country in `(Germany, Sweden)` and channel is `trip-advisor.com` and `travel` is in channel categories and language in (Swedish, English)
| coke         | yes | 10       | -             | -             | channel is `reddit.com`
| catch-all-ad | yes | 20       | -             | -             | always true
| jogurt       | no  | 10       | -             | -             | always true
