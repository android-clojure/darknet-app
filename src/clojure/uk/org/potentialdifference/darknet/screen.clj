(ns uk.org.potentialdifference.darknet.screen
  (:import [android.app Activity]
           [android.util DisplayMetrics]))

(defn dimensions [^Activity activity]
  (let [^DisplayMetrics screen (.getDisplayMetrics (.getResources activity))
        w (.-widthPixels screen)
        h (.-heightPixels screen)]
    {:width w
     :height h
     :ratio (/ w h)
     :decimal (str (float (/ w h)))}))
