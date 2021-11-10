(ns user
  (:require [sc.api]))

(comment
  (do
    (require '[shadow.cljs.devtools.server :as server])
    (server/start!)
    (require '[shadow.cljs.devtools.api :as shadow])
    (shadow/watch :doxa-demo)
    )
  )
