(ns uk.org.potentialdifference.darknet.publish-camera-stream
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.debug :refer [*a]]
            [neko.find-view :refer [find-view]]
            [neko.log :as log]
            [neko.notify :refer [toast]]
            [neko.resource :as res]
            [neko.threading :refer [on-ui]])
  (:import [android.view SurfaceHolder]))

(defactivity uk.org.potentialdifference.darknet.PublishCameraStreamActivity
  :key :publish-camera-stream
  
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (on-ui
        (set-content-view! (*a)
          [:surface-view {:orientation :vertical
                          :id ::camera
                          :layout-width :fill
                          :layout-height :fill}]))
    (doto (.getHolder (find-view this ::camera))
      (.setType SurfaceHolder/SURFACE_TYPE_PUSH_BUFFERS)
      (.addCallback this))))
