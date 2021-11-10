(ns demo-tests
  (:require [clojure.test :refer :all]
            [re-frame.core :as rf]
            [day8.re-frame.test :as rft]
            [demo-app :as demo]))

; TODO recursive pull with union, cached

(comment
  ; suppress re-frame warnings in REPL
  (rf/set-loggers! {:warn (fn [& _])}))

(deftest uncached-results
  (let [uncached-sub (rf/subscribe [::demo/people-uncached])]
    (rft/run-test-sync

      (testing "initial render"
        (rf/dispatch [::demo/init-db])
        (is (= #{} @uncached-sub) "no results from initial empty db"))

      (demo/add-person)
      ;(demo/add-animal)                                          ; <<< why does adding this data break the results

      (is (= #{demo/graphql-person} @uncached-sub)
          "results changed after db change"))))

(deftest cached-query-results
  (let [cached-sub (rf/subscribe [::demo/people-cached])]
    (rft/run-test-sync

      (rf/dispatch [::demo/init-db])
      (is (= #{} @cached-sub) "no results from initial empty db")

      (demo/add-person)
      (demo/add-animal)

      (is (= #{demo/graphql-person} @cached-sub)
          "results changed after db change"))))

(deftest cached-query-results-nested
  "load person with nested animal"
  (let [cached-sub (rf/subscribe [::demo/people-cached])]
    (rft/run-test-sync

      (rf/dispatch [::demo/init-db])
      (is (= #{} @cached-sub) "no results from initial empty db")

      (demo/add-person-with-animal)

      (is (= #{(merge demo/graphql-person
                      {:pet [:animal/id demo/id2]})} @cached-sub)
          "results changed after db change"))))
