(ns app
  (:require [re-frame.core :as rf]
            [reagent.dom :as reagent]
            [demo-app :as demo]))

(enable-console-print!)

(defn app-root []
  (let [p1 (rf/subscribe [::demo/people-uncached])
        p2 (rf/subscribe [::demo/people-cached])]
    [:div
     [:div
      [:h2 "uncached"]
      [:pre @p1]]
     [:hr]
     [:div
      [:h2 "cached"]
      [:pre @p2]]]))

(defn start-app []
  (rf/dispatch [::demo/init-db])
  (js/setTimeout (fn []
                   (println "loading from remote")

                   (do
                     (demo/add-person)
                     ;(demo/add-animal)                      ; <<< uncomment for stale results bug
                     )

                   ;(demo/add-person-with-animal)            ; <<< uncomment for entity type bug
                   )
                 2000)
  (reagent/render [app-root] (.getElementById js/document "root")))
