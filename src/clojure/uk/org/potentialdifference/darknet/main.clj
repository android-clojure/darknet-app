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
            [uk.org.potentialdifference.darknet.websocket :as websocket])
  (:import android.widget.EditText
           android.view.WindowManager$LayoutParams))

(res/import-all)

(defn fullscreen! [^android.app.Activity activity]
  (.addFlags (.getWindow activity)
             WindowManager$LayoutParams/FLAG_FULLSCREEN))

(defn keep-screen-on! [^android.app.Activity activity]
  (.addFlags (.getWindow activity)
             WindowManager$LayoutParams/FLAG_KEEP_SCREEN_ON))

(defactivity uk.org.potentialdifference.darknet.MainActivity
  :key :main
  :features [:no-title]
  
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (fullscreen! this)
    (keep-screen-on! this)
    (let [chan (websocket/client (:ws-url config))]
      (go-loop []
        (when-let [message (<! chan)]
          (on-ui (toast message))
          (recur))))
    (on-ui
        (set-content-view! (*a)
          [:linear-layout {:orientation :vertical
                           :layout-width :fill
                           :layout-height :wrap}
           [:button {:text "Publish Camera Stream",
                     :on-click
                     (fn [^android.widget.Button b]
                       (.startActivity this
                         (intent/intent this '.PublishCameraStreamActivity {})))}]
           [:button {:text "Publish Camera Stream",
                     :on-click
                     (fn [^android.widget.Button b]
                       (.startActivity this
                         (intent/intent this com.foxdogstudios.peepers.StreamCameraActivity
                                        {:width 800 :height 480})))}]]))))

