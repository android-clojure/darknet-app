(ns uk.org.potentialdifference.darknet.camera
  (:require [neko.log :as log])
  (:import [android.hardware Camera]
           [android.hardware Camera$Parameters]
           [android.hardware Camera$Size]))

(defn preview-sizes [index]
  (try
    (let [camera (Camera/open index)
          sizes (doall
                 (for [^Camera$Size size (.. camera (getParameters) (getSupportedPreviewSizes))]
                   (let [w (.-width size)
                         h (.-height size)]
                     {:width w
                      :height h
                      :rato (/ w h)
                      :decimal (str (float (/ w h)))})))]
      (.release camera)
      sizes)
    (catch Throwable e (do
                         (log/i "darknet" "error: " (.getMessage e) )
                         []))))
