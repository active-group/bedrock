(defproject de.active-group/bedrock "0.1.0-SHAPSHOT"
  :description "A builder for web applications."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1" :scope "provided"]
                 [org.clojure/clojurescript "1.10.773" :scope "provided"]
                 [de.active-group/active-clojure "0.38.0"]
                 [de.active-group/reacl-c "0.10.13"]
                 [de.active-group/reacl-c-basics "0.10.1"]

                 [ring "1.9.5"]
                 [ring-middleware-format "0.7.5"]
                 [compojure "1.6.2"]
                 [hiccup "1.0.5"]]

  :source-paths ["src"]

  :aliases {"watch" ["run" "-m" "shadow.cljs.devtools.cli" "watch" "test" "example"]}

  :profiles {:dev {:source-paths ["src" "dev"]
                   :dependencies [[thheller/shadow-cljs "2.11.23"]
                                  [binaryage/devtools "1.0.2"]]}}
  )
