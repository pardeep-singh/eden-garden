(ns eden-garden.middleware
  (:require [eden-garden.http-util :as ehu]
            [clojure.tools.logging :as ctl]
            [slingshot.slingshot :refer [try+]]))


(defn wrap-exceptions
  [handler]
  (fn [req]
    (try+
     (handler req)
     (catch clojure.lang.ExceptionInfo e
       (ctl/error e
                  {:invalid-data (:error (ex-data e))})
       (ehu/bad-request {:invalid-keys (keys (:error (ex-data e)))}))
     (catch eden_garden.http_util.HTTPError http-exception
       (ctl/error http-exception
                  "HTTP Exception")
       (ehu/http-error http-exception))
     (catch Exception exception
       (ctl/error exception
                  "Internal Server Error")
       (ehu/internal-server-error "Internal Server Error")))))


(defn log-requests
  [handler]
  (fn [req]
    (let [timestamp (System/currentTimeMillis)
          response (handler req)]
      (ctl/info {:status (:status response)
                 :method (:request-method req)
                 :uri (:uri req)
                 :duration (str (- (System/currentTimeMillis)
                                   timestamp)
                                "ms")})
      response)))
