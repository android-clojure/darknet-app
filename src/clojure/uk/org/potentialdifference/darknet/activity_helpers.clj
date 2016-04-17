(ns uk.org.potentialdifference.darknet.activity-helpers
  (:import [android.app Activity]
           [android.content.pm ActivityInfo]
           [android.view WindowManager$LayoutParams]))

(defn fullscreen! [^Activity activity]
  (.addFlags (.getWindow activity)
             WindowManager$LayoutParams/FLAG_FULLSCREEN))

(defn keep-screen-on! [^Activity activity]
  (.addFlags (.getWindow activity)
             WindowManager$LayoutParams/FLAG_KEEP_SCREEN_ON))

(defn landscape! [^Activity activity]
  (.setRequestedOrientation activity ActivityInfo/SCREEN_ORIENTATION_LANDSCAPE))

(defn all! [activity]
  (doto activity
    (fullscreen!)
    (keep-screen-on!)
    (landscape!)))
