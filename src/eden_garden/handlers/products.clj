(ns eden-garden.handlers.products
  (:require [eden-garden.mongo :as egm]))


(defn list-products
  [mongo-conn]
  (let [products-db (egm/get-db (:mongo-conn mongo-conn)
                                "garden")]
    (-> (egm/query products-db
                   "products"
                   :only [:description :slug :tags :name :totol_reviews :sku
                          :details :price_history :average_reviews :pricing])
        egm/remove-id)))
