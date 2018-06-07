(ns eden-garden.handlers.apis
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]
            [eden-garden.handlers.products :as edhp]
            [slingshot.slingshot :refer [throw+]]
            [eden-garden.http-util :refer [bad-request]]
            [eden-garden.handlers.schema :as ehs]
            [eden-garden.handlers.products :as eghp]))


(s/def ::id string?)


(s/def ::fetch-product-request (s/keys :req [::id]))


(defn get-products
  [mongo-conn params]
  (if (->> params
           (s/conform ::fetch-product-request)
           (s/valid? ::fetch-product-request))
    (eghp/get-product mongo-conn
                      params)
    (throw+ (bad-request "Invalid Request"))))


(defn get-product
  [mongo-conn params]
  (->> params
       ehs/coerce-get-product-request
       (eghp/get-product mongo-conn)))


(defn add-product
  [mongo-conn params]
  (->> params
       ehs/coerce-add-product-request
       (eghp/add-product mongo-conn)))


(defn update-product
  [mongo-conn params]
  (->> params
       ehs/coerce-update-product-request
       (eghp/update-product mongo-conn)))
