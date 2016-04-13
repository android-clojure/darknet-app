(ns uk.org.potentialdifference.darknet.server
  (:require [happy.core :as h]
            [happy.client.okhttp :as ok]
            [uk.org.potentialdifference.darknet.config :refer [config]])
  (:import [java.security.cert CertificateFactory]
           [javax.net.ssl TrustManagerFactory]
           [java.security KeyStore]
           [javax.net.ssl SSLContext]
           [javax.net.ssl HostnameVerifier]))

(defn stream-video [from to width height]
  (let [api-key (get config :api-key)
        api-url (get config :api-url)
        url (str api-url "/broadcast/" to "/viewStream?from=" from "&width=" width "&height=" height)]
    (h/send! {:url url
              :method "PUT"
              :headers {"authorization" api-key
                        "content-type" "text/text"}
              :body "payload"}
             {:client (ok/create)})))

(defn get-bytes [url f]
  (h/GET url {}
    {:response-body-as :byte-array
     :client (ok/create)
     :handler (fn [response]
                (f (:body response)))}))

(defn get-stream [url f]
  (h/GET url {}
    {:response-body-as :stream
     :client (ok/create)
     :handler (fn [response]
                (f (:body response)))}))

(defn ssl-client! [client context]
  (let [cf (CertificateFactory/getInstance "X.509")
        stream (-> context (.getResources) (.openRawResource "server_cert.cert"))
        ca (try (.generateCertificate stream)
                (finally
                  (.close stream)))
        key-store (KeyStore/getInstance (KeyStore/getDefaultType))
        trust-factory (TrustManagerFactory/getInstance (TrustManagerFactory/getDefaultAlgorithm))
        ssl-context (SSLContext/getInstance "TLS")]
    (.load key-store nil nil)
    (.setCertificateEntry "ca" ca)
    (.init trust-factory key-store)
    (.init ssl-context nil (.getTrustManagers trust-factory) nil)
    (.setSslSocketFactory (.getSocketFactory ssl-context))
    (.setHostnameVerifier (reify HostnameVerifier
                            (verify [this host session]
                              (= host (:host config)))))))

(defn client [context]
  (doto (ok/create)
    (ssl-client! context)))
