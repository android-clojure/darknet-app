(ns uk.org.potentialdifference.darknet.publish-camera-stream
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.data :refer [like-map]]
            [neko.debug :refer [*a]]
            [neko.find-view :refer [find-view]]
            [neko.log :as log]
            [neko.notify :refer [toast]]
            [neko.resource :as res]
            [neko.threading :refer [on-ui]]
            [uk.org.potentialdifference.darknet.activity-helpers :as helper]
            [neko.ui.mapping :refer [defelement]])
  (:import [android.graphics Color]
           [android.view SurfaceHolder]
           [uk.org.potentialdifference.darknet StreamCameraDelegate]
           [com.foxdogstudios.peepers CameraStreamer]))

(defelement :surface-view
  :classname android.view.SurfaceView)

(def camera-streamer
  (atom nil))

(defn new-camera-streamer [^StreamCameraDelegate delegate
                           index port
                           ^SurfaceHolder holder]
  (let [preview-size-index (int 0)
        jpeg-quality (int 40)]
    (CameraStreamer.
     delegate
     (int index)
     false
     (int port)
     (int preview-size-index)
     (int jpeg-quality)
     holder
     (int 800)
     (int 480))))

(defactivity uk.org.potentialdifference.darknet.PublishCameraStreamActivity
  :key :publish-camera-stream
  :features [:no-title]
  :implements [android.view.SurfaceHolder$Callback
               uk.org.potentialdifference.darknet.StreamCameraDelegate]
  
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (let [options (like-map (.getIntent this))]
      (helper/fullscreen! this)
      (helper/keep-screen-on! this)
      (helper/landscape! this)
      (on-ui
          (set-content-view! (*a)
            [:frame-layout {:background-color Color/BLACK}
             [:surface-view {:id ::camera
                             :layout-width :fill
                             :layout-height :fill}]]))
      (doto (.getHolder (find-view this ::camera))
        (.setType SurfaceHolder/SURFACE_TYPE_PUSH_BUFFERS)
        (.addCallback this))))

  (surfaceChanged [this holder fmt width height]
                  (on-ui (toast (format "surfaceChanged %d x %d" width height))))
  (surfaceCreated [this holder]
                  (reset! camera-streamer (new-camera-streamer this 0 8080 holder))
                  (.start @camera-streamer))
  (surfaceDestroyed [this holder]
                    (on-ui (toast "surfaceDestroyed"))
                    (.stop @camera-streamer))
  (cameraStreamDidStart [this width height]
                        (on-ui (toast "cameraStreamDidStart"))))
