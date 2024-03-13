(ns duct-off.api
  (:require
   [org.httpkit.server :as server]))


(defn app-handler [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello, World!"})


(defn ^:fx/autowire off-server [{:keys [port]}]
  (server/run-server
   app-handler
   {:port port}))


(defn stop-server
  {:fx/autowire true
   :fx/halt     ::off-server}
  [stop-server-fn]
  (when (some? stop-server-fn)
    (stop-server-fn :timeout 100)))