(ns eden-garden.components
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [com.stuartsierra.component :as csc]
            [clojure.tools.logging :as ctl]
            [eden-garden.mongo :as mongo]))


(defrecord Mongo [host port mongo-conn]
  csc/Lifecycle

  (start [this]
    (if (nil? mongo-conn)
      (do
        (ctl/info "Starting Mongo Client Component")
        (let [conn (mongo/init {:host host
                                :port port})]
          (assoc this
                 :mongo-conn conn)))
      (do
        (ctl/info "Mongo Connection already Exists")
        this)))

  (stop [this]
    (if mongo-conn
      (do
        (ctl/info "Disconnecting from Mongo")
        (assoc this
               :mongo-conn nil))
      (do
        (ctl/info "Mongo Component is nil.")
        this))))


;; Component to setup the APP routes
(defrecord Routes [app routes mongo]
  csc/Lifecycle

  (start [this]
    (if (nil? routes)
      (do
        (ctl/info "Setting up routes component")
        (assoc this
               :routes (app mongo)))
      (do
        (ctl/info "Routes component already started")
        this)))

  (stop [this]
    (if routes
      (do
        (ctl/info "Removing routes component")
        (assoc this
               :routes nil))
      (do
        (ctl/info "Routes component is nil")
        this))))


;; Component to manage the Jetty Server Lifecycle
(defrecord HttpServer [port routes http-server]
  csc/Lifecycle

  (start [this]
    (if (nil? http-server)
      (let [server (run-jetty (:routes routes)
                              {:port port
                               :join? false})]
        (ctl/info "Starting HTTP server")
        (assoc this
               :http-server server))
      (do
        (ctl/info "HTTP server already started")
        this)))

  (stop [this]
    (if http-server
      (do
        (ctl/info "Stopping HTTP server")
        (.stop ^org.eclipse.jetty.server.Server (:http-server this))
        (assoc this
               :http-server nil))
      (do
        (ctl/info "HTTP server component is nil")
        this))))
