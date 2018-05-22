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


;; Add sorting params
(defn list-products
  [mongo-conn {:keys [page page-size]
               :as params
               :or {page "0"
                    page-size "10"}}]
  (let [products-db (egm/get-db (:mongo-conn mongo-conn)
                                "garden")
        query (construct-query params)
        page (Integer/parseInt page)
        page-size (Integer/parseInt page-size)
        products (-> (egm/query products-db
                                "products"
                                :query query
                                :only default-response-fields
                                :skip (* page page-size)
                                :limit page-size)
                     egm/remove-id)
        total (egm/count-docs products-db
                              "products"
                              :query query)]
    {:total total
     :page page
     :page-size page-size
     :products products}))
