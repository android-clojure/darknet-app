(ns uk.org.potentialdifference.darknet.config)

(def host "192.168.1.25")

(def config
  {:api-host host
   :ws-url  (str "wss://" host ":8080")
   :api-url (str "https://" host ":8443")
   :api-key "x9RHJ2I6nWi376Wa"})
