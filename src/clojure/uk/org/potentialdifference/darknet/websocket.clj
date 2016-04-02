(ns uk.org.potentialdifference.darknet.websocket
  (:require [clojure.core.async :refer [chan close! put! go-loop >! <! timeout go]])
  (:import [org.java_websocket.client WebSocketClient]
           [org.java_websocket.drafts Draft_10]
           [java.net URI]))

(defn client
  [uri]
  (let [chan (chan)]
    (.connect
     (proxy [WebSocketClient]
         [(new URI uri) (new Draft_10)]
         (onOpen [handshake])
         (onClose [i s b]
           (close! chan))
         (onMessage [message]
           (go (>! chan message)))
         (onError [^java.lang.Exception e]
           (close! chan))))
    chan))
