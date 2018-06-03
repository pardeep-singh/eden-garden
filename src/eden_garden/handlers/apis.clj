(ns eden-garden.handlers.apis
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]
            [eden-garden.handlers.products :as edhp]
            [eden-garden.handlers.products :as eghp]
            [slingshot.slingshot :refer [throw+]]
            [eden-garden.http-util :refer [bad-request]]))


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
