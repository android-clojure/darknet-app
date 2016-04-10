(ns uk.org.potentialdifference.darknet.storage
  (:require [clojure.java.io :refer [copy]])
  (:import [android.os Environment]
           [java.io File]))

(defn make-file [filename]
  (let [dir (File. (Environment/getExternalStoragePublicDirectory Environment/DIRECTORY_PICTURES) "darknet")
        file (File. dir filename)]
    (when-not (.exists dir)
      (.mkdirs dir))
    file))

(defn local-path [filename]
  (-> (make-file filename)
      (.getAbsolutePath)
      (str)))

(defn write-bytes! [bytes filename]
  (when-let [file (make-file filename)]
    (copy bytes file)))

(defn read-bytes [filename]
  (let [f (java.io.File. (make-file filename))
        ary (byte-array (.length f))
        is (java.io.FileInputStream. f)]
    (.read is ary)
    (.close is)
    ary))
