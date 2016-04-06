(ns uk.org.potentialdifference.darknet.subscribe-camera-stream
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.find-view :refer [find-view]]
            [uk.org.potentialdifference.darknet.activity-helpers :as helper]
            [neko.data :refer [like-map]]
            [neko.debug :refer [*a]]
            [neko.find-view :refer [find-view]]
            [neko.log :as log]
            [neko.notify :refer [toast]]
            [neko.resource :as res]
            [neko.threading :refer [on-ui]]
            [neko.ui.mapping :refer [defelement]]
            [clojure.java.io :as io])
  (:import [android.graphics Color]
           [android.view Gravity]
           [com.camera.simplemjpeg MjpegView]
           [com.camera.simplemjpeg MjpegInputStream]))

(defelement :mjpeg-view
  :classname com.camera.simplemjpeg.MjpegView
  :inherits android.view.SurfaceView
  :traits [:id])

(defactivity uk.org.potentialdifference.darknet.SubscribeCameraStreamActivity
  :key :subscribe-camera-stream
  :features [:no-title]
  
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (let [options (like-map (.getIntent this))]
      (helper/fullscreen! this)
      (helper/keep-screen-on! this)
      (helper/landscape! this)
      (on-ui
          (set-content-view! (*a)
            [:frame-layout {:background-color Color/BLACK}
             [:mjpeg-view {:id ::mjpeg-view}]
             [:text-view {:layout-width :wrap
                          :background-color Color/WHITE
                          :text "Test text view"
                          :gravity :top}]]))
      (future
        (let [stream (new MjpegInputStream (io/input-stream (get options :url)))
              ^MjpegView mjpeg-view (find-view this ::mjpeg-view)]
          (on-ui
              (doto mjpeg-view
                (.setSource stream)
                (.setDisplayMode MjpegView/SIZE_BEST_FIT))))))))
