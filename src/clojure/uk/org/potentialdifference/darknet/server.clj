(ns uk.org.potentialdifference.darknet.server
  (:require [happy.core :as h]
            [happy.client.okhttp :as ok]
            [neko.log :as log]
            [neko.resource :as res]
            [uk.org.potentialdifference.darknet.config :refer [config]])
  (:import [java.security.cert CertificateFactory]
           [javax.net.ssl TrustManagerFactory]
           [java.security KeyStore]
           [javax.net.ssl SSLContext]
           [javax.net.ssl HostnameVerifier]
           [uk.org.potentialdifference.darknet R$raw]
           [com.squareup.okhttp OkHttpClient]))

(res/import-all)

(defn ssl-client [context]
  (let [client (OkHttpClient.)]
    (try
      (let [cf (CertificateFactory/getInstance "X.509")
            stream (.openRawResource (.getResources context) R$raw/server_cert)
            ca (try (.generateCertificate cf stream)
                    (finally
                      (.close stream)
                      nil))
            key-store (KeyStore/getInstance (KeyStore/getDefaultType))
            trust-factory (TrustManagerFactory/getInstance (TrustManagerFactory/getDefaultAlgorithm))
            ssl-context (SSLContext/getInstance "TLS")]
        (.load key-store nil nil)
        (.setCertificateEntry key-store "ca" ca)
        (.init trust-factory key-store)
        (.init ssl-context nil (.getTrustManagers trust-factory) nil)
        (.setSslSocketFactory client (.getSocketFactory ssl-context))
        (.setHostnameVerifier client (reify HostnameVerifier
                                       (verify [this host session]
                                         (= host (:api-host config)))))
        client)
      (catch Exception e
        (log/i "darknet" "exception setting up ssl client" (.getMessage e))
        client))))

(defn stream-video [context from to width height]
  (let [api-key (get config :api-key)
        api-url (get config :api-url)
        url (str api-url "/broadcast/" to "/viewStream?&width=" width "&height=" height "&from=" from)]
    (h/send! {:url url
              :method "PUT"
              :headers {"authorization" api-key
                        "content-type" "text/text"}
              :body ""}
             {:client (ok/create)
              :okhttp-client (ssl-client context)})))

(defn get-bytes [context url f]
  (h/GET url {}
    {:response-body-as :byte-array
     :client (ok/create)
     :handler (fn [response]
                (f (:body response)))}))
