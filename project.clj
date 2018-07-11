(defproject rpm "0.1.0-SNAPSHOT"
  :plugins [[cider/cider-nrepl "0.17.0"]
            [lein-cljfmt "0.5.7"]]
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.1.8"]
                 [http-kit "2.3.0-beta2"]
                 [clojure.java-time "0.3.2"]]
  :main ^:skip-aot rpm.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
