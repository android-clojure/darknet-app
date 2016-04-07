(ns uk.org.potentialdifference.darknet.camera
  (:import [android.hardware Camera]
           [android.hardware Camera$Parameters]))

(defn preview-sizes [index]
  (try
    (let [camera (Camera/open index)
          sizes (doall
                 (for [size (.. camera (getParameters) (getSupportedPreviewSizes))]
                   {:width (.-width size)
                    :height (.-height size)}))]
      (.release camera)
      sizes)
    (catch Throwable e [])))
