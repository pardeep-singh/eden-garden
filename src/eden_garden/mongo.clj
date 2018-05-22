(ns eden-garden.mongo
  (:require [monger.core :as mongo]
            [monger.collection :as collection]
            [monger.query :as mq])
  (:import [com.mongodb
            DB]))


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


(defn query
  [db coll & {query :query
              sort-by :sort-by
              limit :limit
              only :only
              skip :skip
              :or {sort-by {}
                   only []
                   skip 0
                   limit 10
                   query {}}}]
  (let [empty-query (mq/empty-query (.getCollection ^DB db coll))
        constructed-query {:query query
                           :sort sort-by
                           :fields only
                           :limit limit
                           :skip skip}
        final-query (merge empty-query
                           query)]
    (mq/exec (merge empty-query
                    constructed-query))))


(defn remove-id
  [result]
  (map (fn [doc]
         (dissoc doc
                 :_id))
       result))


(defn count-docs
  [db coll & {query :query
              :or {query {}}}]
  (collection/count db
                    coll
                    query))
