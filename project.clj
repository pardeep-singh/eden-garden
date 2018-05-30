(defproject eden-garden "0.1.0-SNAPSHOT"
  :description "REST API Service exposing APIs for Ecommerce APP for selling garden products."
  :url "http://example.com/FIXME"
  :license {:name "Proprietary"}
  :uberjar-exclusions [#"(?i)^META-INF/[^/]*\.(SF|RSA)$"]
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [cheshire "5.7.0"]
                 [com.stuartsierra/component "0.2.3"]
                 [org.clojure/tools.logging "0.4.0"]
                 [com.novemberain/monger "3.1.0"]
                 [ring/ring-json "0.4.0"]
                 [slingshot "0.10.3"]]
  :source-paths ["src"]
  :global-vars {*warn-on-reflection* true}
  :manifest {"Project-Name" ~#(:name %)
             "Project-Version" ~#(:version %)
             "Build-Date" ~(str (java.util.Date.))}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.0"]]}}
  :aot [eden-garden.core]
  :main eden-garden.core)
