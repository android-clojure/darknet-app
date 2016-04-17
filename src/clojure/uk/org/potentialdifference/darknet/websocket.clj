(ns uk.org.potentialdifference.darknet.websocket
  (:import [org.java_websocket.client WebSocketClient]
           [org.java_websocket.drafts Draft_10]
           [java.net URI]))

(defn connect! [url options]
  (doto (proxy [WebSocketClient]
            [(new URI url) (new Draft_10)]
            (onOpen [handshake]
              ((:on-open options) this handshake))
            (onClose [code reason remote]
              ((:on-close options) code reason remote))
            (onMessage [message]
              ((:on-message options) message))
            (onError [^java.lang.Exception e]
              ((:on-error options) e)))
    (.connectBlocking)))
