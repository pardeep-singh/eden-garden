(ns eden-garden.handlers.products
  (:require [eden-garden.mongo :as egm]))


(defn list-products
  [mongo-conn]
  (let [products-db (egm/get-db (:mongo-conn mongo-conn)
                                "garden")
        products (egm/find-docs products-db
                                "products"
                                :fields [:slug :name :description])]
    (mapv #(select-keys % ["description"]) products)))

