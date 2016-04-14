(ns uk.org.potentialdifference.darknet.config)

(def host "192.168.1.25")
;; (def host "172.20.10.2")

(def config
  {:api-host host
   :ws-url  (str "wss://" host ":8080")
   :api-url (str "https://" host ":8443")
   :api-key "x9RHJ2I6nWi376Wa"})
