(defproject duct-off "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.2"]
                 [duct/core "0.8.0"]
                 [io.github.parencat/fx "0.1.8"]
                 [http-kit "2.3.0"]
                 [compojure "1.7.1"]
                 [hiccup "1.0.5"]]

  :plugins [[duct/lein-duct "0.12.3"]]
  :middleware [lein-duct.plugin/middleware]

  :main ^:skip-aot duct-off.main
  :resource-paths ["resources" "target/resources"]

  :profiles
  {:dev     {:source-paths   ["dev/src"]
             :resource-paths ["dev/resources"]
             :dependencies   [[integrant/repl "0.3.2"]
                              [hawk "0.2.11"]]}
   :repl    {:repl-options {:init-ns user}}
   :uberjar {:aot :all}})
