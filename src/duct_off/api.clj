(ns duct-off.api
  (:require
   [org.httpkit.server :as server]
   [ring.util.response :as r]
   [hiccup.core :as h]
   [compojure.core :refer [defroutes GET routes wrap-routes]]
   [compojure.route :as route]
   [duct-off.ui :as ui]))


(defn wrap-hx-page [handler]
  (fn [request]
    (let [not-hx? (not (some-> request (get-in [:headers "hx-request"]) parse-boolean))
          result  (cond-> (handler request)
                          not-hx? (update :body ui/base-page))]
      (-> (update result :body #(h/html %))
          (r/content-type "text/html")))))


(def ui-routes
  (routes
   (GET "/" []
     (r/response (ui/home-page)))

   (GET "/preview" [file]
     (-> (ui/preview-page file)
         (r/response)
         (r/header "HX-Trigger-After-Swap" "highlightCode")))))


(defroutes app-routes
  (wrap-routes ui-routes wrap-hx-page)
  (route/resources "/public")
  (route/not-found "Page not found"))


(defn ^:fx/autowire off-server [{:keys [port]}]
  (server/run-server
   app-routes
   {:port port}))


(defn stop-server
  {:fx/autowire true
   :fx/halt     ::off-server}
  [stop-server-fn]
  (when (some? stop-server-fn)
    (stop-server-fn :timeout 100)))