(ns uk.org.potentialdifference.darknet.config)

(def ip "192.168.1.24")

(def config
  {:ws-url  (str "wss://" ip ":8081")
   :api-url (str "http://") ip ":8080"
   :api-key "x9RHJ2I6nWi376Wa"})
