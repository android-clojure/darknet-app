(ns uk.org.potentialdifference.darknet.main
  (:require [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [neko.activity :refer [defactivity set-content-view!]]
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

(defactivity uk.org.potentialdifference.darknet.MainActivity
  :key :main
  :features [:no-title]
  
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (helper/fullscreen! this)
    (helper/keep-screen-on! this)
    (helper/landscape! this)
    #_(let [chan (websocket/client (:ws-url config))]
      (go-loop []
        (when-let [instruction (<! (chan 1 (map ->instruction)))]
          (case (:message instruction)
            "startCameraStream" (publish-camera-stream! this instruction)
            "streamVideo" (subscribe-camera-stream! this instruction)
            :default)
          (recur))))
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
                       (publish-camera-stream! this {:width 800 :height 480}))}]]))))

