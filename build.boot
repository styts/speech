(set-env!
 :source-paths    #{"src/cljs" "src/clj"}
 :resource-paths  #{"resources"}
 :dependencies '[;; system
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.reader "0.9.2"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [environ "1.1.0"]
                 [boot-environ "1.1.0"]
                 [org.danielsz/system "0.4.0"]

                 ;; reply things
                 [cider/cider-nrepl "0.15.0-snapshot"]
                 [refactor-nrepl "2.2.0"]

                 ;; backend
                 [compojure "1.3.4"] ;; defroutes
                 [jarohen/chord "0.6.0"] ;; websockets

                 ;; utilities
                 [cheshire "5.7.1"]

                 ;; frontend
                 [adzerk/boot-cljs          "1.7.228-2"  :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.3"      :scope "test"]
                 [adzerk/boot-reload        "0.4.13"     :scope "test"]
                 [binaryage/devtools "0.9.0" :scope "test"]
                 [cljsjs/chartist "0.10.1-0"]
                 [com.cemerick/piggieback   "0.2.1"      :scope "test"]
                 [crisptrutski/boot-cljs-test "0.3.0" :scope "test"]
                 [garden "1.3.2"]
                 [org.clojure/clojurescript "1.9.293"]
                 [org.clojure/tools.nrepl   "0.2.12"     :scope "test"]
                 [org.martinklepsch/boot-garden "1.3.2-0" :scope "test"]
                 [pandeiro/boot-http        "0.7.6"      :scope "test"]
                 [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]
                 [reagent "0.6.1"]
                 [weasel                    "0.7.0"      :scope "test"]

                 [cfft "0.1.0"]
                 [thi.ng/color "1.2.0"]
                 [net.mikera/vectorz-clj "0.46.0"]])

(require
 '[environ.boot :refer [environ]]
 '[system.boot :refer [system]]
 '[speech.systems :refer [dev-system]]
 '[speech.microphone]
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[crisptrutski.boot-cljs-test :refer [test-cljs]]
 '[org.martinklepsch.boot-garden :refer [garden]]
 '[powerlaces.boot-cljs-devtools :refer [cljs-devtools dirac]])

(deftask build-frontend []
  (comp
   (cljs)
   (garden :styles-var 'speech.styles/screen
           :output-to "css/garden.css")))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                 garden {:pretty-print false})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none :source-map true}
                 reload {:on-jsload 'speech.app/init})
  identity)

(deftask backend []
  (comp
   (environ :env {:http-port "4000"})
   (system :sys #'dev-system :auto true :files ["microphone.clj" "web.clj"])
   identity))

(deftask frontend
  "Simple alias to run application in development mode"
  []
  (comp
   (watch :verbose true)
   (development)
   (serve :port 3000)
   (cljs-repl)
   (cljs-devtools)
   (reload)
   (build-frontend)))

(deftask build-jar
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (production)
   (build-frontend)
   (aot :namespace #{'speech.main})
   (uber)
   (jar :file "speech.jar" :main 'speech.main)
   (sift :include #{#"speech.jar"})
   (target)))

(deftask deploy []
  (sh "scp" "target/speech.jar" "orangepi:speech")
  identity)
