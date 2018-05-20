(ns eden-garden.handlers.products
  (:require [cheshire.core :as cc]
            [eden-garden.mongo :as egm]))


(defonce default-response-fields
  [:description :slug :tags :name :totol_reviews :sku
   :details :price_history :average_reviews :pricing])


;; filter products by tags
;; filter products by retail price(range query)
;; filter products by sale price(range query)
;; filter products by avg reviews (range query)
(defn construct-query
  [{:keys [tags]
    :as query
    :or {tags []}}]
  (merge {}
         (when (seq tags)
           {:tags {:$in (cc/parse-string tags)}})))


(defn list-products
  [mongo-conn params]
  (let [products-db (egm/get-db (:mongo-conn mongo-conn)
                                "garden")
        query (construct-query params)
        products (-> (egm/query products-db
                                "products"
                                :query query
                                :only default-response-fields)
                     egm/remove-id)]
    {:products products
     :total (count products)}))
