(ns uk.org.potentialdifference.darknet.subscribe-camera-stream
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.debug :refer [*a]]
            [neko.find-view :refer [find-view]]
            [neko.log :as log]
            [neko.notify :refer [toast]]
            [neko.resource :as res]
            [neko.threading :refer [on-ui]]))

(defactivity uk.org.potentialdifference.darknet.SubscribeCameraStreamActivity
  :key :subscribe-camera-stream
  
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (on-ui
        (set-content-view! (*a)
          [:linear-layout {:orientation :vertical
                           :layout-width :fill
                           :layout-height :wrap}
           [:text-view {:text "Subscribe Camera Stream Activity"}]]))))
