(ns uk.org.potentialdifference.darknet.create-camera-stream
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.data :refer [like-map]]
            [neko.debug :refer [*a]]
            [neko.find-view :refer [find-view]]
            [neko.log :as log]
            [neko.resource :as res]
            [neko.threading :refer [on-ui]]
            [uk.org.potentialdifference.darknet.activity-helpers :as helper]
            [neko.ui.mapping :refer [defelement]]
            [uk.org.potentialdifference.darknet.server :as server])
  (:import [android.graphics Color]
           [android.view SurfaceHolder]
           [android.view SurfaceView]
           [android.util DisplayMetrics]
           [android.view ViewGroup$LayoutParams]
           [uk.org.potentialdifference.darknet StreamCameraDelegate]
           [com.foxdogstudios.peepers CameraStreamer]))

(defelement :surface-view
  :classname android.view.SurfaceView)

(def camera-streamer
  (atom nil))

(def intent-options
  (atom {}))

(defn new-camera-streamer [^StreamCameraDelegate delegate
                           index port
                           ^SurfaceHolder holder
                           desired-width
                           desired-height]
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
     desired-width
     desired-height)))

(defn fit-within [fit-w fit-h within-w within-h]
  (let [aspect-ratio (/ fit-w fit-h)
        within-ratio (/ within-w within-h)]
    (if (> aspect-ratio within-ratio)
      [within-w (int (/ within-w aspect-ratio))]
      [(int (* within-h aspect-ratio)) within-h])))

(defactivity uk.org.potentialdifference.darknet.CreateCameraStreamActivity
  :key :create-camera-stream
  :features [:no-title]
  :implements [android.view.SurfaceHolder$Callback
               uk.org.potentialdifference.darknet.StreamCameraDelegate]
  
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (let [options (like-map (.getIntent this))]
      (reset! intent-options options)
      (helper/fullscreen! this)
      (helper/keep-screen-on! this)
      (helper/landscape! this)
      (on-ui
          (set-content-view! (*a)
            [:frame-layout {:background-color Color/BLACK}
             [:surface-view {:id ::preview
                             :layout-width :fill
                             :layout-height :fill}]]))
      (doto ^SurfaceHolder (.getHolder ^SurfaceView (find-view this ::preview))
        (.setType SurfaceHolder/SURFACE_TYPE_PUSH_BUFFERS)
        (.addCallback this))))
  
  (surfaceChanged [this holder fmt width height])
  (surfaceCreated [this holder]
                  (reset! camera-streamer (new-camera-streamer this 0 8085 holder
                                                               (get @intent-options :width)
                                                               (get @intent-options :height)))
                  (.start ^CameraStreamer @camera-streamer))
  (surfaceDestroyed [this holder]
                    (.stop ^CameraStreamer @camera-streamer))
  (cameraStreamDidStart [^Activity this width height]
                        (let [^SurfaceView surface-view (find-view this ::preview)
                              ^DisplayMetrics screen (.getDisplayMetrics (.getResources this))
                              ^ViewGroup$LayoutParams params (.getLayoutParams surface-view)
                              [fit-w fit-h] (fit-within width height
                                                        (.-widthPixels screen)
                                                        (.-heightPixels screen))
                              to   (get @intent-options :to)
                              from (get @intent-options :from)]
                          (set! (.-width params) (int fit-w))
                          (set! (.-height params) (int fit-h))
                          (on-ui
                              (doto surface-view
                                (.setLayoutParams params)
                                (.requestLayout)))
                          (server/stream-video from to width height))))
