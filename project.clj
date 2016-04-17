(defproject darknet/darknet "0.1.0-SNAPSHOT"
  :description "FIXME: Android project description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :global-vars {*warn-on-reflection* true}

  :source-paths ["src/clojure" "src"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :plugins [[lein-droid "0.4.3" :exclusions [org.clojure/clojure]]]

  :dependencies [[org.clojure-android/clojure "1.7.0-r4"]
                 [neko/neko "4.0.0-alpha5"]
                 [org.java-websocket/Java-WebSocket "1.3.0"]
                 [cheshire "5.5.0" :exclusions [org.clojure/clojure]]

                 [happy "0.5.2"]
                 [com.squareup.okhttp/okhttp "2.5.0"]
                 [com.android.support/support-v4 "13.0.0"]]
  :profiles {:default [:dev]

             :dev
             [:android-common :android-user
              {:dependencies [[org.clojure/tools.nrepl "0.2.10"]]
               :target-path "target/debug"
               :android {:aot :all-with-unused
                         :aot-exclude-ns ["cider.nrepl.middleware.util.java.parser"
                                          "cider.nrepl"
                                          "cider-nrepl.plugin"
                                          
                                          "clojure.core.async.lab"
                                          "cljs.core.async.macros"
                                          "cljs.core.async.impl.ioc-macros"
                                          "taoensso.sente.packers.transit"
                                          "taoensso.timbre.tools.logging"
                                          "happy.representor.transit"
                                          #"taoensso\.timbre\.appenders\..*"
                                          #"taoensso\.sente\.server-adapters\..*"]
                         :rename-manifest-package "uk.org.potentialdifference.darknet.debug"
                         :manifest-options {:app-name "Darknet (debug)"}}}]
             :release
             [:android-common
              {:target-path "target/release"
               :android
               { ;; :keystore-path "/home/user/.android/private.keystore"
                ;; :key-alias "mykeyalias"
                ;; :sigalg "MD5withRSA"

                :ignore-log-priority [:debug :verbose]
                :aot :all
                :build-type :release}}]}

  :android { ;; Specify the path to the Android SDK directory.
            ;; :sdk-path "/home/user/path/to/android-sdk/"

            ;; Try increasing this value if dexer fails with
            ;; OutOfMemoryException. Set the value according to your
            ;; available RAM.
            :dex-opts ["-JXmx4096M"]
            :multi-dex true
            :multi-dex-proguard-conf-path "build/proguard-multi-dex.cfg"
            :native-libraries-paths ["libs"]
            :target-version "15"
            :aot-exclude-ns ["clojure.parallel" "clojure.core.reducers"
                             "cider.nrepl" "cider-nrepl.plugin"
                             "cider.nrepl.middleware.util.java.parser"
                             #"cljs-tooling\..+"]})
