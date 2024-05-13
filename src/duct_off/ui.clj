(ns duct-off.ui
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string])
  (:import
   [java.io File]))


(defn base-page [content]
  [:html {:lang "en"}
   [:head
    [:title "DuctOff"]

    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:meta {:name "color-scheme" :content "light dark"}]

    [:link {:rel  "stylesheet"
            :href "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css"}]
    [:link {:rel  "stylesheet"
            :href "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.colors.min.css"}]
    [:link {:href "public/prism.css" :rel "stylesheet"}]
    [:style
     ".deps-tag {padding: 2px 4px;}
       .deps-tag + .deps-tag {margin-left: 10px;}"]

    [:script {:src         "https://unpkg.com/htmx.org@1.9.10"
              :defer       true
              :integrity   "sha384-D1Kt99CQMDuVetoL1lrYwg5t+9QdHe7NLX/SoJYkXDFfX37iInKRy5xLSi8nO7UC"
              :crossorigin "anonymous"}]]
   [:body
    [:main {:id    "do-page"
            :class "container"}
     content]
    [:script {:src "public/prism.js"}]
    [:script
     "document.body.addEventListener(
       \"highlightCode\",
       function (evt) {
          Prism.highlightAll();
       })"]]])


(defn icon [{:keys [path round]}]
  [:svg {:xmlns        "http://www.w3.org/2000/svg"
         :fill         "none"
         :width        "1.3rem"
         :height       "1.3rem"
         :viewBox      "0 0 24 24"
         :stroke-width "1.5"
         :stroke       "currentColor"}
   [:path (cond-> {:d path}
                  (some? round)
                  (assoc :stroke-linecap "round"
                         :stroke-linejoin "round"))]])


(def plus-icon
  (icon {:path  "M12 9v6m3-3H9m12 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"
         :round true}))

(def remove-icon
  (icon {:path  "m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
         :round true}))


(defn deps-card [{:keys [group name version type]}]
  [:article
   [:div {:style "display: flex; align-items: center;"}
    [:a {:href  "#"
         :class "contrast"}
     (str group "/" name)]
    [:b {:style "flex: 1; margin: 0 10px;"}
     version]
    (if (= type :candidate)
      [:a {:href "#"}
       plus-icon]
      [:a {:href "#"}
       remove-icon])]
   [:small {:style "color: var(--pico-muted-color);"}
    "Clojure core library"]
   [:footer
    [:small {:class "deps-tag pico-background-violet-400"}
     "http router"]
    [:small {:class "deps-tag pico-background-indigo-400"}
     "db driver"]
    [:small {:class "deps-tag pico-background-azure-300"}
     "router"]]])


(defn home-page []
  [:section
   [:h3
    "Let's setup your new Clojure project!"]

   [:form
    [:label "Project info"]

    [:fieldset
     [:div {:class "grid"}
      [:input {:name        "group"
               :type        "text"
               :placeholder "Group"}]
      [:input {:name        "name"
               :type        "text"
               :placeholder "Name"}]]
     [:textarea {:name        "description"
                 :rows        1
                 :placeholder "Describe your idea"
                 :aria-label  "Description"}]]

    [:label "Dependencies"]

    [:fieldset
     [:div {:class "grid"
            :style "align-items: center;"}
      [:input {:type          "search"
               :name          "search"
               :auto-complete "off"
               :placeholder   "Find libraries and modules"
               :aria-label    "Search"}]
      [:label {:style "text-align: center;"}
       "Selected items"]]

     [:section {:class "grid"
                :style "height: 50vh; grid-template-rows: 100%;"}
      [:div {:style "display: flex; flex-direction: column;"}
       (into
        [:div {:style "overflow-y: scroll;"}]
        (repeatedly 20 #(deps-card {:group "org.clojure" :name "clojure" :version "1.11.0" :type :candidate})))]

      [:div {:style "overflow-y: scroll;"}
       (into
        [:div {:style "overflow: auto;"}]
        (repeatedly 20 #(deps-card {:group "org.clojure" :name "clojure" :version "1.11.0" :type :selected})))]]]

    [:fieldset {:class "grid"}
     [:input {:type        "button"
              :class       "outline secondary"
              :value       "Preview files"
              :hx-get      "/preview"
              :hx-target   "#do-page"
              :hx-push-url "true"}]
     [:input {:type  "submit"
              :class "outline"
              :value "Download project"}]]]])


(def files-black-list
  ["target"
   "logs"
   "classes"
   "checkouts"
   "pom\\.xml"
   "pom\\.xml\\.asc"
   "\\.nrepl-port"
   "\\.dir-locals\\.el"
   "profiles\\.clj"
   "dev/resources/local\\.edn"
   "dev/src/local\\.clj"
   "\\.lein-.*"
   "\\.git"
   "\\.idea"
   ".*\\.iml"])


(def black-list-matcher
  (re-pattern (string/join "|" files-black-list)))


(defn not-in-black-list? [^File f]
  (not (re-matches black-list-matcher (.getName f))))


(defn project-tree
  "Takes a folder and returns a tree of files and folders."
  [^File f]
  (when (not-in-black-list? f)
    (if (.isDirectory f)
      (into [:ul]
            (->> (.listFiles f)
                 (filter not-in-black-list?)
                 (map (fn [^File file]
                        [:li
                         (if (.isDirectory file)
                           [:details
                            [:summary
                             (str "📁 " (.getName file))]
                            (project-tree file)]
                           [:a {:hx-get    (str "/preview?file=" (.getAbsolutePath file))
                                :hx-target "#do-page"}
                            [:span (str "📄 " (.getName file))]])]))))
      [:a {:href "#"}
       [:span "📄 " (.getName f)]])))


(defn file-viewer [file-name]
  (let [file-name (or file-name "src/duct_off/main.clj")
        file      (io/file file-name)
        content   (slurp file)
        file-lang (case (last (re-find #"\.(.+)$" file-name))
                    ("clj" "cljs" "edn") "language-clojure"
                    ("html" "xml") "language-html"
                    ("css" "scss" "sass") "language-css"
                    ("js" "ts") "language-js"
                    ("json") "language-json"
                    ("md") "language-markdown"
                    ("yaml" "yml") "language-yaml"
                    "language-plain")]
    [:pre
     [:code {:class file-lang}
      content]]))


(defn preview-page [file]
  [:section {:class "grid"}
   [:div
    (project-tree (io/file "."))]
   [:div
    (file-viewer file)]])
