(ns eden-garden.handlers.schema
  (:require [schema.core :as s]
            [schema.coerce :as scoerce]))


(s/defschema GetProductRequest
  {:id s/Str})


(s/defschema Pricing
  {:retail s/Int
   :sale s/Int})


(s/defschema Product
  {:description s/Str
   :name s/Str
   :tags [s/Str]
   :pricing Pricing})


(s/defschema PriceQuery
  {(s/optional-key :gte) s/Int
   (s/optional-key :lte) s/Int
   (s/optional-key :eq) s/Int})


(s/defschema GetProductsRequest
  {(s/optional-key :page) s/Int
   (s/optional-key :page_size) s/Int
   (s/optional-key :sort_by) s/Str
   (s/optional-key :sort_order) s/Str
   (s/optional-key :tags) [s/Str]
   (s/optional-key :retail_price) PriceQuery
   (s/optional-key :sale_price) PriceQuery})


(s/defschema AddProductRequest
  Product)


(s/defschema UpdateProductRequest
  (assoc Product
         :id s/Str))


(defn get-product-request-matchers
  [schema]
  (scoerce/json-coercion-matcher schema))


(defn add-product-request-matchers
  [schema]
  (scoerce/json-coercion-matcher schema))


(defn update-product-request-matchers
  [schema]
  (scoerce/json-coercion-matcher schema))


(defn get-products-request-matchers
  [schema]
  (scoerce/json-coercion-matcher schema))


(def coerce-get-product-request
  (scoerce/coercer! GetProductRequest
                    get-product-request-matchers))


(def coerce-add-product-request
  (scoerce/coercer! AddProductRequest
                    add-product-request-matchers))


(def coerce-update-product-request
  (scoerce/coercer! UpdateProductRequest
                    update-product-request-matchers))


(def coerce-get-products-request
  (scoerce/coercer! GetProductsRequest
                    get-products-request-matchers))
