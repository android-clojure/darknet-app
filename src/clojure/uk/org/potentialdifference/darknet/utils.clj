(ns uk.org.potentialdifference.darknet.utils)

(defn set-state!
  "Sets the given key/value pair to the given activity's state."
  [context content-key content-val]
  (swap! (.state context) assoc content-key content-val))

(defn get-state
  "Gets the value for the given key in the given activity's state."
  [context content-key]
  (get @(.state context) content-key))
