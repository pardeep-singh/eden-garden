(ns eden-garden.mongo
  (:require [monger.core :as mongo]
            [monger.collection :as collection]
            [monger.query :as mq])
  (:import [com.mongodb DB WriteConcern TagSet Tag Tag]))


(def default-options
  {:read-preference (com.mongodb.ReadPreference/secondaryPreferred (java.util.ArrayList. [(TagSet. (Tag. "type" "hidden"))
                                                                                          (TagSet. (Tag. "type" "secondary"))]))
   :auto-connect-retry true
   :socket-timeout 10000
   :connect-timeout 10000
   :write-concern WriteConcern/W1})


(defmulti init
  (fn [conn-type _]
    (keyword conn-type)))


(defmethod init :direct
  [_ {:keys [host port]}]
  (let [conn (mongo/connect {:host host
                             :port port})]
    conn))


(defmethod init :replica-set
  [_ {:keys [servers opts]
      :or {opts default-options}}]
  (let [options (mongo/mongo-options opts)
        servers-address (map (fn [{:keys [host port]}]
                               (mongo/server-address host
                                                     port))
                             servers)]
    (mongo/connect servers-address
                   options)))


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
              sort :sort
              limit :limit
              only :only
              skip :skip
              :or {sort {}
                   only []
                   skip 0
                   limit 10
                   query {}}}]
  (let [empty-query (mq/empty-query (.getCollection ^DB db coll))
        constructed-query {:query query
                           :sort sort
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


(defn insert
  [db coll record]
  (collection/insert db
                     coll
                     record))

(defn insert-and-return
  [db coll record]
  (collection/insert-and-return db
                                coll
                                record))


(defn find-one
  [db coll query & {only :only
                    :or {only []}}]
  (collection/find-one-as-map db
                              coll
                              query
                              only))


(defn update-doc
  [db coll query doc]
  (collection/update db
                     coll
                     query
                     doc))
