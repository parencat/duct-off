(ns duct-off.api
  (:require
   [org.httpkit.server :as server]
   [ring.util.response :as r]
   [ring.middleware.params :refer [wrap-params]]
   [hiccup.core :as h]
   [compojure.core :refer [defroutes GET routes wrap-routes]]
   [compojure.route :as route]
   [duct-off.ui :as ui]))


(defn wrap-hx-page [handler]
  (fn [request]
    (let [not-hx?  (not (some-> request (get-in [:headers "hx-request"]) parse-boolean))
          response (cond-> (handler request)
                     not-hx? (update :body ui/base-page))]
      (-> (update response :body #(h/html %))
          (r/content-type "text/html")
          (r/status 200)))))


(def ui-routes
  (routes
   (GET "/" []
     (-> (ui/home-page)
         (r/response)))

   (GET "/preview" [file]
     (-> (ui/preview-page file)
         (r/response)))

   (GET "/file-preview" [file]
     (-> (ui/file-viewer file)
         (r/response)))))


(defroutes app-routes
  (-> ui-routes
      (wrap-params)
      (wrap-routes wrap-hx-page))
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