(ns eden-garden.handlers.schema
  (:require [schema.core :as s]
            [schema.coerce :as scoerce]))


(s/defschema GetProductRequest
  {:id s/Str})


(defn get-product-request-matchers
  [schema]
  (scoerce/json-coercion-matcher schema))


(def coerce-get-product-request
  (scoerce/coercer! GetProductRequest
                    get-product-request-matchers))
