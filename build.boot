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

                 ;; frontend
                 [adzerk/boot-cljs          "1.7.228-2"  :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.3"      :scope "test"]
                 [adzerk/boot-reload        "0.4.13"     :scope "test"]
                 [pandeiro/boot-http        "0.7.6"      :scope "test"]
                 [com.cemerick/piggieback   "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.12"     :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [org.clojure/clojurescript "1.9.293"]
                 [crisptrutski/boot-cljs-test "0.3.0" :scope "test"]
                 [reagent "0.6.0"]
                 [garden "1.3.2"]
                 [org.martinklepsch/boot-garden "1.3.2-0" :scope "test"]
                 [binaryage/devtools "0.9.0" :scope "test"]
                 [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]
                 [cheshire "5.7.1"]])

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

(deftask start-capture []
  (comp
   (environ :env {:buffer-size "4000"} :verbose true)
   (speech.microphone/start-capture)
   identity))

(deftask build-jar
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (build-frontend)
   (aot :namespace #{'speech.main})
   (uber)
   (jar :file "speech.jar" :main 'speech.main)
   (sift :include #{#"speech.jar"})
   (target)))

(deftask deploy []
  (sh "scp" "target/speech.jar" "orangepi:speech")
  identity)

;; (deftask dev []
;;   (comp
;;    (environ :env {:http-port "4000"})
;;    (watch :verbose true)
;;    (system :sys #'dev-system :auto true :files [".*"] :regexes true)
;;    (cljs-repl)
;;    (cljs-devtools)
;;    (reload) ;; fighweel replacement
;;    (cljs :source-map true)
;;    (repl :server true)))

;;;;;;;;;;;;;;;;;;;;;; TESTING stuff from the template
;; (deftask testing []
;;   (set-env! :source-paths #(conj % "test/cljs"))
;;   identity)

;; ;;; This prevents a name collision WARNING between the test task and
;; ;;; clojure.core/test, a function that nobody really uses or cares
;; ;;; about.
;; (ns-unmap 'boot.user 'test)

;; (deftask test []
;;   (comp (testing)
;;         (test-cljs :js-env :phantom
;;                    :exit?  true)))

;; (deftask auto-test []
;;   (comp (testing)
;;         (watch)
;;         (test-cljs :js-env :phantom)))

(comment
  (boot (build))
  (boot (deploy))

  (boot (dev))

  ;; to ensure dev task is not blocking, it's executed as a future
  (def frontend-future (future (boot (frontend))))
  (future-cancel frontend-future)

  (boot (backend))

  (.start (:web (dev-system)))
  (system.repl/system)

  (+ 1 2)

  (load-file "build.boot")
  (boot (start-capture)))
