(ns demo-app
  (:require #?(:cljs [cljs.pprint :refer [pprint]]
               :clj  [clojure.pprint :refer [pprint]])
            [re-frame.core :as rf]
            [ribelo.doxa :as dx]))

(rf/reg-event-fx ::init-db
                 (fn [{:keys [:db]} _]
                   {:db {:doxa (dx/create-dx [] {::dx/with-diff? true})}}))

(defn upsert
  [doxa-db response]
  (dx/commit doxa-db [:dx/put response]))

(rf/reg-event-fx ::graphql-response
                 (fn [{:keys [:db]} [_ response]]
                   {:db (update db :doxa upsert response)}))

(defn people
  [doxa-db]
  (dx/q [:find (pull [:*] [?t1 ?person])
         :where
         [?t1 ?person :person/id _]]
        doxa-db))

(rf/reg-sub ::people-uncached
            (fn [{:keys [doxa]} _]
              (people doxa)))

(defn people-cached
  [doxa-db]
  ^{::dx/cache ::all-people}
  (dx/q [:find (pull [:*] [?t1 ?person])
         :where
         [?t1 ?person :person/id _]]
        doxa-db))

(rf/reg-sub ::people-cached
            (fn [{:keys [doxa]} _]
              (people-cached doxa)))


(def id1 "2379a6d2-ef62-49dc-831d-348b672a30d8")
(def id2 "34fdd51c-3919-41f6-a94d-c8d899511c2f")

(def graphql-person {:person/id id1
                     :name      "Steve"})
(def graphql-animal {:animal/id id2
                     :name      "Fido"})

(defn add-person
  []
  (rf/dispatch [::graphql-response [^{::dx/entity-key :person/id}
                                    graphql-person]]))
(defn add-animal
  []
  (rf/dispatch [::graphql-response [^{::dx/entity-key :animal/id}
                                    graphql-animal]]))
(defn add-person-with-animal
  []
  (rf/dispatch [::graphql-response [^{::dx/entity-key :person/id}
                                    (assoc graphql-person
                                      :pet ^{::dx/entity-key :animal/id} graphql-animal)]]))








