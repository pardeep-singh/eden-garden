(ns eden-garden.mongo
  (:require [monger.core :as mongo]
            [monger.collection :as collection]))


(defn init
  [{:keys [host port]
    :as config}]
  {:pre [(seq host)
         (integer? port)]}
  (let [conn (mongo/connect {:host host
                             :port port})]
    conn))


(defn disconnect
  [conn]
  (mongo/disconnect conn))


(defn get-db
  [conn db-name]
  {:pre [(seq db-name)]}
  (mongo/get-db conn
                db-name))


(defn find-docs
  [db coll & {query :query
              fields :fields
              :or {query {}
                   fields []}}]
  (->> (collection/find db
                        coll
                        query
                        fields)
       (mapv identity)))
