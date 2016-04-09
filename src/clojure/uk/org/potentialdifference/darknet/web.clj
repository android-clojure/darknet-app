(ns uk.org.potentialdifference.darknet.web
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.debug :refer [*a]]
            [neko.data :refer [like-map]]
            [neko.find-view :refer [find-view]]
            [neko.log :as log]
            #_[neko.notify :refer [toast]]
            [neko.resource :as res]
            [neko.threading :refer [on-ui]]
            [neko.intent :as intent]
            [uk.org.potentialdifference.darknet.activity-helpers :as helper])
  (:import [android.graphics Color]))

(defactivity uk.org.potentialdifference.darknet.WebActivity
  :key :web
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
             [:web-view {:id ::web
                         :layout-width :fill
                         :layout-height :fill}]]))
      (when-let [web-view (find-view this ::web)]
        (cond
          (:url options) (.loadUrl web-view (:url options))
          (:html options) (.loadData web-view (:html options) "text/html","utf-8"))))))


