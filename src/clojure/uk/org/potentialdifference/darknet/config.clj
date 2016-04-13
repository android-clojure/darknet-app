(ns uk.org.potentialdifference.darknet.config)

(def host "192.168.1.25")
;; (def host "192.168.2.24") ;; DELETEME

(def config
  {:api-host host
   :ws-url  (str "wss://" host ":8081")
   :api-url (str "http://" host ":8080")
   :api-key "x9RHJ2I6nWi376Wa"})
