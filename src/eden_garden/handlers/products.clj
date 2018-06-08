(ns eden-garden.handlers.products
  (:require [cheshire.core :as cc]
            [eden-garden.mongo :as egm]
            [slingshot.slingshot :refer [throw+]]
            [eden-garden.http-util :refer [not-found-exception]])
  (:import java.util.UUID))


(defonce default-response-fields
  [:id :description :slug :tags :name :totol_reviews :sku
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
  [{:keys [tags retail_price sale_price]
    :as query}]
  (merge {}
         (when (seq tags)
           {:tags {:$in tags}})
         (when (seq retail_price)
           {:pricing.retail (transform-range-query retail_price)})
         (when (seq sale_price)
           {:pricing.sale (transform-range-query sale_price)})))


(defonce sort-field-mappings
  {"retail_price" "pricing.retail"
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
               :or {page 0
                    page_size 10
                    sort_by "sale_price"
                    sort_order "asc"}}]
  (let [products-db (egm/get-db (:mongo-conn mongo-conn)
                                "garden")
        query (construct-query params)
        sorting-value (construct-sorting-value sort_by sort_order)
        products (-> (egm/query products-db
                                "products"
                                :query query
                                :only default-response-fields
                                :skip (* page page_size)
                                :limit page_size
                                :sort sorting-value)
                     egm/remove-id)
        total (egm/count-docs products-db
                              "products"
                              :query query)]
    {:total total
     :page page
     :page_size page_size
     :products products}))


(defn add-product
  [mongo-conn zmap]
  (let [products-db (egm/get-db (:mongo-conn mongo-conn)
                                "garden")
        product-id (.toString (UUID/randomUUID))
        doc (egm/insert-and-return products-db
                                   "products"
                                   (assoc zmap
                                          :id product-id))]
    (dissoc doc
            :_id)))


(defn fetch-product
  [products-db id]
  (if-let [product (egm/find-one products-db
                                 "products"
                                 {:id id}
                                 :only [:name :description
                                        :tags :pricing])]
    (dissoc product
            :_id)
    (throw+ (not-found-exception (format "Product with %s is not found"
                                         id)))))


(defn update-product
  [mongo-conn zmap]
  (let [products-db (egm/get-db (:mongo-conn mongo-conn)
                                "garden")
        product (fetch-product products-db
                               (:id zmap))
        tags (into (:tags product)
                   (:tags zmap))
        updated-product (merge product
                               zmap
                               (when (seq tags)
                                 {:tags (set tags)}))]
    (egm/update-doc products-db
                    "products"
                    {:id (:id zmap)}
                    updated-product)
    updated-product))


;; Throw Not found exception when product with given ID is not found
(defn get-product
  [mongo-conn {:keys [id]}]
  (let [products-db (egm/get-db (:mongo-conn mongo-conn)
                                "garden")
        product (fetch-product products-db
                               id)]
    product))
