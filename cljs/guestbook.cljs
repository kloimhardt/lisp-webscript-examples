;---
; Excerpted from "Web Development with Clojure, Third Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj3 for more book information.
;---
; zip archive retrieved from the "Resources" section of
; https://pragprog.com/titles/dswdcloj3/web-development-with-clojure-third-edition/
; file: guestbook-reagent/src/cljs/guestbook/core.cljs

(require '[reagent.core :as r]
         '[reagent.dom :as dom]
         '[ajax.core :refer [GET POST]]
         '[clojure.string :as string])

;
(defn get-messages [messages]
  (GET "?messages"
       {:response-format :json
        :keywords? true
        :handler (fn [r] (reset! messages (:messages r)))}))
;

;
(defn message-list [messages]
  [:ul.messages
   (for [{:keys [timestamp message name]} @messages]
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p message]
      [:p " - " name]])])
;

;
(defn send-message! [fields errors messages]
  (POST "?message"

        {:format :json
         :headers
         {"Accept" "application/json"
          ;;"x-csrf-token" (.-value (.getElementById js/document "token"))}
          }
         :params @fields
         :handler (fn [_]
                    (swap! messages conj (assoc @fields :timestamp (.toLocaleString (js/Date.))))
                    (reset! fields nil)
                    (reset! errors nil))
         :error-handler (fn [e]
                          (.log js/console (str e))
                          (reset! errors (get-in e [:response :errors])))}))
;

;
(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))
;

;
(defn message-form [messages]
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn [messages]
      [:div
       [errors-component errors :server-error]
       [:div.field
        [:label.label {:for :name} "Name"]
        [errors-component errors :name]
        [:input.input
         {:type :text
          :name :name
          :on-change (fn [e] (swap! fields assoc :name
                                    (-> e .-target .-value)))
          :value (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [errors-component errors :message]
        [:textarea.textarea
         {:name :message
          :value (:message @fields)
          :on-change (fn [e] (swap! fields assoc :message
                                    (-> e .-target  .-value)))}]]
       [:input.button.is-primary
        {:type :submit
         :on-click (fn [_] (send-message! fields errors messages))
         :value "comment"}]])))

(defn home []
  (let [messages (r/atom nil)]
    (get-messages messages)
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       [:div.columns>div.column
        [:h3 "Messages"]
        [message-list messages]]
       [:div.columns>div.column
        [message-form messages]]])))
;
(dom/render [home] (.getElementById js/document "app"))
