(ns uk.org.potentialdifference.darknet.server
  (:require [happy.core :as h]
            [happy.client.okhttp :as ok]
            [uk.org.potentialdifference.darknet.config :refer [config]]))

(defn stream-video [from to width height]
  (let [api-key (get config :api-key)
        api-url (get config :api-url)
        url (str api-url "/broadcast/" to "/streamVideo?from=" from "&width=" width "&height=" height)]
    (h/send! {:url url
              :method "PUT"
              :headers {"authorization" api-key
                        "content-type" "text/text"}
              :body "payload"}
             {:client (ok/create)})))
