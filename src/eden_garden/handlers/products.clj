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


(defonce sort-field-mappings
  {"average_reviews" "average_reviews"
   "retail_price" "pricing.retail"
   "sale_price" "pricing.sale"})


(defonce sort-order-mappings
  {"asc" 1
   "desc" -1})


(defn construct-sorting-value
  [sort-field sort-order]
  (let [coerced-sort-field (sort-field-mappings sort-field)
        coerced-sort-order (sort-order-mappings sort-order)]
    {coerced-sort-field coerced-sort-order}))


(defn list-products
  [mongo-conn {:keys [page page_size sort_by sort_order]
               :as params
               :or {page "0"
                    page_size "10"
                    sort_by "average_reviews"
                    sort_order "asc"}}]
  (let [products-db (egm/get-db (:mongo-conn mongo-conn)
                                "garden")
        query (construct-query params)
        page (Integer/parseInt page)
        page-size (Integer/parseInt page_size)
        sorting-value (construct-sorting-value sort_by sort_order)
        products (-> (egm/query products-db
                                "products"
                                :query query
                                :only default-response-fields
                                :skip (* page page-size)
                                :limit page-size
                                :sort sorting-value)
                     egm/remove-id)
        total (egm/count-docs products-db
                              "products"
                              :query query)]
    {:total total
     :page page
     :page_size page-size
     :products products}))
