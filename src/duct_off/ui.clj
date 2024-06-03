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
    [:link {:href "public/prism-twilight.css" :rel "stylesheet"}]
    [:style
     ".deps-tag {padding: 2px 4px;}
      .deps-tag + .deps-tag {margin-left: 10px;}
      .tree-view ul li {
          list-style: none;
          margin-bottom: var(--pico-nav-element-spacing-horizontal);
      }
      .tree-view details {
          margin-bottom: 0;
      }
      .tree-view details summary {
          height: 1.5rem;
          line-height: 1.5rem;
          display: flex;
          align-items: center;
      }
      details[open]>summary:not([role]):not(:focus) {
          color: var(--pico-accordion-close-summary-color);
      }
      .tree-view details[open]>summary, .tree-view details ul {
          margin-bottom: var(--pico-nav-element-spacing-horizontal);
      }
      .tree-view details summary::after {
          display: none;
      }
      .tree-view details summary::before {
          display: block;
          width: 1rem;
          height: 1rem;
          float: left;
          transform: rotate(-90deg);
          background-image: var(--pico-icon-chevron);
          background-position: left center;
          background-size: 1rem auto;
          background-repeat: no-repeat;
          content: \"\";
          transition: transform var(--pico-transition);
     }
     .tree-view details[open]>summary::before {
         transform: rotate(0);
     }
     .tree-view .tree-item {
         white-space: nowrap;
         margin-left: 1rem;
         cursor: pointer;
     }
     .tree-item-name {
         margin-left: 0.25rem;
     }
     .tree-view-list ul {
         padding-left: 0.75rem;
     }
     .tree-view-list {
         padding-left: 1rem;
     }
     .tree-view-nav {
         margin-left: 1.5rem;
     }
     .preview-page {
         height: calc(100vh - var(--pico-block-spacing-vertical)*2);
         overflow: hidden;
         margin: 0;
         grid-template-columns: 1fr 3fr; grid-gap: 1rem;
     }
     .tree-view-back {
         margin-left: 0.5rem;
     }
     .tree-view-wrapper {
         height: 100%;
         overflow: hidden;
         display: flex;
         flex-direction: column;
     }"]

    [:script {:src         "https://unpkg.com/htmx.org@1.9.10"
              :defer       true
              :integrity   "sha384-D1Kt99CQMDuVetoL1lrYwg5t+9QdHe7NLX/SoJYkXDFfX37iInKRy5xLSi8nO7UC"
              :crossorigin "anonymous"}]]
   [:body
    [:main {:id "do-page"}
     content]
    [:script {:src "public/prism.js"}]]])


(defn icon [{:keys [path box round evenodd]
             :or   {box 24}}]
  [:svg {:xmlns        "http://www.w3.org/2000/svg"
         :fill         "none"
         :width        "1.2rem"
         :height       "1.2rem"
         :viewBox      (format "0 0 %d %d" box box)
         :stroke-width "1"
         :stroke       "currentColor"}
   [:path (cond-> {:d path}
            round (assoc :stroke-linecap "round" :stroke-linejoin "round")
            evenodd (assoc :fill-rule "evenodd" :clip-rule "evenodd"))]])


(def plus-icon
  (icon {:path  "M12 9v6m3-3H9m12 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"
         :round true}))

(def remove-icon
  (icon {:path  "m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
         :round true}))

(def folder-icon
  (icon {:path "M3.75 3A1.75 1.75 0 0 0 2 4.75v3.26a3.235 3.235 0 0 1 1.75-.51h12.5c.644 0 1.245.188 1.75.51V6.75A1.75 1.75 0 0 0 16.25 5h-4.836a.25.25 0 0 1-.177-.073L9.823 3.513A1.75 1.75 0 0 0 8.586 3H3.75ZM3.75 9A1.75 1.75 0 0 0 2 10.75v4.5c0 .966.784 1.75 1.75 1.75h12.5A1.75 1.75 0 0 0 18 15.25v-4.5A1.75 1.75 0 0 0 16.25 9H3.75Z"
         :box  20}))

(def doc-icon
  (icon {:path    "M4.5 2A1.5 1.5 0 0 0 3 3.5v13A1.5 1.5 0 0 0 4.5 18h11a1.5 1.5 0 0 0 1.5-1.5V7.621a1.5 1.5 0 0 0-.44-1.06l-4.12-4.122A1.5 1.5 0 0 0 11.378 2H4.5Zm2.25 8.5a.75.75 0 0 0 0 1.5h6.5a.75.75 0 0 0 0-1.5h-6.5Zm0 3a.75.75 0 0 0 0 1.5h6.5a.75.75 0 0 0 0-1.5h-6.5Z"
         :box     20
         :evenodd true}))

(def arrow-left-icon
  (icon {:path  "M10.5 19.5 3 12m0 0 7.5-7.5M3 12h18"
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
  [:section {:class "container"}
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
   ".*\\.iml"
   "\\.DS_Store"
   "\\.clj-kondo"
   "\\.lsp"])


(def black-list-matcher
  (re-pattern (string/join "|" files-black-list)))


(defn not-in-black-list? [^File f]
  (not (re-matches black-list-matcher (.getName f))))


(defn project-tree
  "Takes a folder and returns a tree of files and folders."
  [^File f]
  (when (not-in-black-list? f)
    (into
     [:ul {:class "tree-view-list"}]
     (->> (.listFiles f)
          (filter not-in-black-list?)
          (map (fn [^File file]
                 [:li
                  (if (.isDirectory file)
                    [:details
                     [:summary {:style "white-space: nowrap;"}
                      folder-icon
                      [:span {:class "tree-item-name"}
                       (.getName file)]]
                     (project-tree file)]

                    [:a {:hx-get    (str "/file-preview?file=" (.getPath file))
                         :hx-target "#file-viewer"
                         :hx-swap   "outerHTML"
                         :class     "tree-item"}
                     doc-icon
                     [:span {:class "tree-item-name"}
                      (.getName file)]])]))))))


(defn file-viewer [file-name]
  (let [file-name (or file-name "src/duct_off/main.clj")
        file      (io/file file-name)
        content   (slurp file)
        file-lang (case (last (re-find #"\w\.(.+)$" file-name))
                    ("clj" "cljs" "edn") "language-clojure"
                    ("html" "xml") "language-html"
                    ("css" "scss" "sass") "language-css"
                    ("js" "ts") "language-js"
                    ("json") "language-json"
                    ("md") "language-markdown"
                    ("yaml" "yml") "language-yaml"
                    "language-plain")]
    [:div {:id "file-viewer"}
     [:pre
      [:code {:class file-lang
              :style "font-size: 0.8rem"}
       content]]
     [:script
      "if (typeof Prism !== 'undefined') {
         Prism.highlightAll();
       };"]]))


(defn preview-page [file]
  [:section {:class "grid preview-page"}
   [:div {:class "tree-view-wrapper"}
    [:h5 {:class "tree-view-nav"}
     [:a {:href "/"}
      arrow-left-icon
      [:span {:class "tree-view-back"}
       "Back"]]]
    [:div {:class "overflow-auto tree-view"}
     (project-tree (io/file "."))]]
   [:div {:class "overflow-auto"}
    (file-viewer file)]])
