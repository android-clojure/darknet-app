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

(defn parse-long [x]
  (Long/parseLong x))

(defn parse-vals [coll kvs]
  (reduce (fn [m [k v]]
            (cond-> m
              (contains? m k)
              (update-in [k] v))) coll kvs))

(defn ->instruction [str]
  (-> (parse-string str true)
      (parse-vals {:width parse-long
                   :height parse-long})))

(defn publish-camera-stream! [^Activity activity options]
  (.startActivity activity
    (intent/intent activity '.PublishCameraStreamActivity
                   options)))

(defn view-camera-stream! [^Activity activity options]
  (.startActivity activity
    (intent/intent activity '.ViewCameraStreamActivity
                   options)))

(defn back-to-home! [^Activity activity options]
  (.startActivity activity
    (intent/intent activity '.MainActivity options)))

(defn web-page! [^Activity activity options]
  (.startActivity activity
    (intent/intent activity '.WebActivity options)))

(defactivity uk.org.potentialdifference.darknet.MainActivity
  :key :main
  :features [:no-title]
  
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (let [on-message (fn [str]
                       (let [instruction (->instruction str)]
                         #_(on-ui (toast (pr-str instruction)))
                         (case (:message instruction)
                           "startCameraStream" (publish-camera-stream! this instruction)
                           "streamVideo" (view-camera-stream! this instruction)
                           "stop" (back-to-home! this instruction)
                           "openWebPage" (web-page! this instruction)
                           :default)))]
      (helper/fullscreen! this)
      (helper/keep-screen-on! this)
      (helper/landscape! this)
      (websocket/connect! (:ws-url config)
                          {:on-open (fn [_])
                           :on-close (fn [code reason remote])
                           :on-message on-message
                           :on-error (fn [e] #_(on-ui (toast (.getMessage e))))})
      (on-ui
          (set-content-view! (*a)
            [:linear-layout {:orientation :vertical
                             :layout-width :fill
                             :layout-height :wrap}
             [:button {:text "View Camera Stream",
                       :on-click
                       (fn [^android.widget.Button b]
                         (view-camera-stream! this {:url "http://webcam1.lpl.org/axis-cgi/mjpg/video.cgi"}))}]
             [:button {:text "Publish Camera Stream",
                       :on-click
                       (fn [^android.widget.Button b]
                         (publish-camera-stream! this {:width 800 :height 480}))}]])))))

