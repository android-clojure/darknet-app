(ns uk.org.potentialdifference.darknet.main
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.debug :refer [*a]]
            [neko.find-view :refer [find-view]]
            [neko.log :as log]
            [neko.notify :refer [toast]]
            [neko.resource :as res]
            [neko.threading :refer [on-ui]]
            [neko.intent :as intent]
            [uk.org.potentialdifference.darknet.config :refer [config]]
            [uk.org.potentialdifference.darknet.websocket :as websocket]
            [uk.org.potentialdifference.darknet.activity-helpers :as helper]
            [cheshire.core :refer [parse-string]])
  (:import [android.app Activity]
           android.widget.EditText))

(res/import-all)

(defn ->instruction [str]
  (parse-string str true))

(defn publish-camera-stream! [^Activity activity options]
  (.startActivity activity
    (intent/intent activity com.foxdogstudios.peepers.StreamCameraActivity
                   options)))

(defn subscribe-camera-stream! [^Activity activity options]
  (.startActivity activity
    (intent/intent activity '.SubscribeCameraStreamActivity
                   options)))

(defn back-to-home! [^Activity activity options]
  (.startActivity activity
    (intent/intent activity '.MainActivity options)))

(defactivity uk.org.potentialdifference.darknet.MainActivity
  :key :main
  :features [:no-title]
  
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (let [on-message (fn [str]
                       (on-ui (toast str))
                       (let [instruction (->instruction str)]
                         (case (:message instruction)
                           "startCameraStream" (publish-camera-stream! this instruction)
                           "streamVideo" (subscribe-camera-stream! this instruction)
                           "stop" (back-to-home! this instruction)
                           :default)))]
      (helper/fullscreen! this)
      (helper/keep-screen-on! this)
      (helper/landscape! this)
      (websocket/connect! (:ws-url config)
                          {:on-open (fn [_])
                           :on-close (fn [code reason remote])
                           :on-message on-message
                           :on-error (fn [e] (on-ui (toast (.getMessage e))))})
      (on-ui
          (set-content-view! (*a)
            [:linear-layout {:orientation :vertical
                             :layout-width :fill
                             :layout-height :wrap}
             [:button {:text "View Camera Stream",
                       :on-click
                       (fn [^android.widget.Button b]
                         (subscribe-camera-stream! this {:url "http://webcam1.lpl.org/axis-cgi/mjpg/video.cgi"}))}]
             [:button {:text "Publish Camera Stream",
                       :on-click
                       (fn [^android.widget.Button b]
                         (publish-camera-stream! this {:width 800 :height 480}))}]])))))

