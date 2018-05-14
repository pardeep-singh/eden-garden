(ns eden-garden.core
  (:gen-class)
  (:require [clojure.tools.logging :as ctl]
            [compojure.core :as cc :refer [context defroutes POST GET PUT DELETE]]
            [compojure.route :as route]
            [com.stuartsierra.component :as csc]
            [eden-garden.components :as ec]
            [eden-garden.http-util :as ehu]
            [eden-garden.middleware :as em]))


(defonce ^{:doc "Server system representing HTTP server."}
  server-system nil)


(defn app-routes
  "Returns the APP routes and injects the dependency required by routes."
  []
  (cc/routes
   
   (GET "/ping" [] (ehu/ok {:ping "PONG"}))

   (GET "/hello" [] (ehu/ok {:message "Hello World from Clojure."}))

   (route/not-found "Not Found")))


(defn app
  "Constructs routes wrapped by middlewares."
  []
  (-> (app-routes)
      em/wrap-exceptions
      em/log-requests))


(defn start-system
  "Starts the system given system-map."
  [system]
  (let [server-system* (csc/start system)]
    (alter-var-root #'server-system (constantly server-system*))))


(defn stop-system
  "Stops the system given a system-map."
  [system]
  (let [server-system* (csc/stop system)]
    (alter-var-root #'server-system (constantly server-system*))))


(defn construct-system
  [configs]
  (let [routes-comp (ec/map->Routes {:app app})
        http-server-comp (ec/map->HttpServer {:port (:port configs)})]
    (csc/system-map
     :routes routes-comp
     :http-server (csc/using http-server-comp
                             [:routes]))))


(defn -main
  [& args]
  (try
    (let [configs {:port 9099}
          system (construct-system configs)]
      (start-system system)
      (.addShutdownHook (Runtime/getRuntime)
                        (Thread. (fn []
                                   (ctl/info "Running Shutdown Hook")
                                   (stop-system server-system)
                                   (shutdown-agents)
                                   (ctl/info "Done with shutdown hook")))))
    (catch Exception exception
      (ctl/error exception
                 "Exception while starting the application"))))