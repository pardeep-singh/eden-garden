(ns eden-garden.handlers.products
  (:require [cheshire.core :as cc]
            [eden-garden.mongo :as egm]))


(defonce default-response-fields
  [:description :slug :tags :name :totol_reviews :sku
   :details :price_history :average_reviews :pricing])


(defn transform-range-query
  [{:keys [gte lte eq]}]
  (merge {}
         (when gte
           {:$gte gte})
         (when lte
           {:$lte lte})
         (when eq
           {:$eq eq})))


(defn construct-query
  [{:keys [tags retail_price sale_price average_reviews]
    :as query}]
  (merge {}
         (when (seq tags)
           {:tags {:$in (cc/parse-string tags)}})
         (when (seq retail_price)
           {:pricing.retail (transform-range-query (cc/parse-string retail_price
                                                                    true))})
         (when (seq sale_price)
           {:pricing.sale (transform-range-query (cc/parse-string sale_price
                                                                  true))})
         (when (seq average_reviews)
           {:average_reviews (transform-range-query (cc/parse-string average_reviews
                                                                     true))})))


;; Add pagination parameters
;; Add field to specify fields in response
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
