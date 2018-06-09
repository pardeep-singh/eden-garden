(ns eden-garden.handlers.apis
  (:require [eden-garden.handlers.products :as eghp]
            [eden-garden.handlers.schema :as ehs]
            [cheshire.core :as cc]))


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


(defn get-products
  [mongo-conn params]
  (->> params
       ehs/coerce-get-products-request
       (eghp/list-products mongo-conn)))
