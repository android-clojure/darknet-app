(ns uk.org.potentialdifference.darknet.main
  (:require [clojure.core.async :refer [<! >! put! close! go]]
            [neko.activity :refer [defactivity set-content-view!]]
            [neko.debug :refer [*a]]
            [neko.find-view :refer [find-view]]
            [neko.log :as log]
            [neko.notify :refer [toast]]
            [neko.resource :as res]
            [neko.threading :refer [on-ui]]
            [neko.intent :as intent]
            [taoensso.sente  :as sente])
    (:import android.widget.EditText))

;; We execute this function to import all subclasses of R class. This gives us
;; access to all application resources.
(res/import-all)

;; This is how an Activity is defined. We create one and specify its onCreate
;; method. Inside we create a user interface that consists of an edit and a
;; button. We also give set callback to the button.
(defactivity uk.org.potentialdifference.darknet.MainActivity
  :key :main

  (onCreate [this bundle]
    (.superOnCreate this bundle)
    ;; (neko.debug/keep-screen-on this)
    #_(let [{:keys [chsk ch-recv send-fn state]}
            (sente/make-channel-socket! "wss://192.161.1.21:8081"
                                        {:type :ws})]
        (go (log/d (<! ch-recv))))
    (on-ui
        (set-content-view! (*a)
          [:linear-layout {:orientation :vertical
                           :layout-width :fill
                           :layout-height :wrap}
           [:button {:text "Publish Camera Stream",
                     :on-click
                     (fn [^android.widget.Button b]
                       (.startActivity this (intent/intent this '.PublishCameraStreamActivity {})))}]
           [:button {:text "Publish Camera Stream",
                     :on-click
                     (fn [^android.widget.Button b]
                       (.startActivity this (intent/intent this com.foxdogstudios.peepers.StreamCameraActivity {})))}]]))))

