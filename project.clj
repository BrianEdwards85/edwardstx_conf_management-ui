(defproject us.edwardstx.conf/management-ui "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0-beta2"]
                 [org.clojure/spec.alpha "0.1.134"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/clojurescript "1.9.946"
                  :scope "provided"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [org.clojure/core.async "0.3.443"]

                 [us.edwardstx/edwardstx_common "1.0.2-SNAPSHOT"]

                 [ring/ring-core "1.6.2"]
                 [ring/ring-defaults "0.3.1"]

                 [compojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [yogthos/config "0.8"]

                 [reagent "0.7.0"]
                 [reagent-utils "0.2.1"]
                 [re-frame "0.9.2"]
                 [re-com "0.9.0"]
                 [day8.re-frame/http-fx "0.1.3"]
                 [cljs-ajax "0.5.8"]
                 [com.cemerick/url "0.1.1"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.2.0"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.0.2"]
            [lein-cljsbuild "1.1.7"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

  :min-lein-version "2.5.0"

  :uberjar-name "management-ui.jar"

  :main us.edwardstx.conf.management-ui

  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
             {:output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/uberjar"
              :optimizations :advanced
              :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel {:on-jsload "us.edwardstx.conf.management-ui.core/mount-root"}
             :compiler
             {
              :main "us.edwardstx.conf.management-ui.dev"
              :asset-path "/management-ui/assets/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}



            }
   }


  :figwheel
  {:http-server-root "public"
   :server-port 3449
   :nrepl-port 7002
   :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"
                      ]
   :css-dirs ["resources/public/css"]
   :ring-handler us.edwardstx.conf.management-ui.handler/app}

  :sass {:src "src/sass"
         :dst "resources/public/css"}

  :profiles {:dev {:repl-options {:init-ns us.edwardstx.conf.management-ui.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[binaryage/devtools "0.9.4"]
                                  [ring/ring-mock "0.3.1"]
                                  [ring/ring-devel "1.6.1"]
                                  [re-frisk "0.3.0"]
                                  [prone "1.1.4"]
                                  [figwheel-sidecar "0.5.11"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [pjstadig/humane-test-output "0.8.2"]
                                  ]

                   :resource-paths ["env/dev/resources" "env/test/resources" "resources"]
                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.11"]
                             [lein-less "1.7.5"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
