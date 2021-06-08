(require '[cljs.reader :refer [read-string]]
         '[qlkit.core :as ql :refer [defcomponent*]]
         '[ajax.core :refer [POST]]
         '[daiquiri.interpreter :refer [interpret] :rename {interpret html}])

(defmulti read (fn [qterm & _] (first qterm)))

(defmethod read :qlkit-todo/todos
  [[dispatch-key params :as query-term] env {:keys [:todo/by-id] :as state}]
  (let [{:keys [todo-id]} params]
    (if todo-id
      [(ql/parse-children query-term (assoc env :todo-id todo-id))]
      (for [id (keys by-id)]
        (ql/parse-children query-term (assoc env :todo-id id))))))

(defmethod read :db/id
  [query-term {:keys [todo-id] :as env} state]
  (when (get-in state [:todo/by-id todo-id])
    todo-id))

(defmethod read :todo/text
  [query-term {:keys [todo-id] :as env} state]
  (get-in state [:todo/by-id todo-id :todo/text]))

(defmulti mutate first)

(defmethod mutate :todo/new!
  [[dispatch-key params :as query-term] env state-atom]
  (let [{:keys [:db/id]} params]
    (swap! state-atom assoc-in [:todo/by-id id] params)))

(defmethod mutate :todo/delete!
  [query-term {:keys [todo-id] :as env} state-atom]
  (swap! state-atom update :todo/by-id dissoc todo-id))

(defmulti remote (fn [qterm & _] (first qterm)))

(defmethod remote :todo/new!
  [query-term env state]
  query-term)

(defmethod remote :todo/delete!
  [query-term env state]
  query-term)

(defmethod remote :todo/text
  [query-term env state]
  query-term)

(defmethod remote :db/id
  [query-term env state]
  query-term)

(defmethod remote :qlkit-todo/todos
  [query-term env state]
  (ql/parse-children-remote query-term env))

(defmulti sync (fn [qterm & _] (first qterm)))

(defmethod sync :qlkit-todo/todos
  [[_ params :as query-term] result env state-atom]
  (for [{:keys [db/id] :as todo} result]
    (ql/parse-children-sync query-term todo (assoc env :db/id id))))

(defmethod sync :todo/text
  [query-term result {:keys [:db/id] :as env} state-atom]
  (when id
    (swap! state-atom assoc-in [:todo/by-id id :todo/text] result)))

(defmethod sync :db/id
  [query-term result {:keys [:db/id] :as env} state-atom]
  (when id
    (swap! state-atom assoc-in [:todo/by-id id :db/id] result)))

(defmethod sync :todo/new!
  [query-term result env state-atom]
  (let [[temp-id permanent-id] result]
    (swap! state-atom
           update
           :todo/by-id
           (fn [by-id]
             (-> by-id
                 (dissoc temp-id)
                 (assoc permanent-id (assoc (by-id temp-id) :db/id permanent-id)))))))

(defonce app-state (atom {}))

(defcomponent* TodoItem
  (query [[:todo/text] [:db/id]])
  (render [this {:keys [:todo/text] :as atts} state]
          (html
            [:li.box
             [:span text]
             [:button.delete.is-pulled-right
              {:on-click (fn [] (ql/transact!* this [:todo/delete!]))}]])))

(defcomponent* TodoList
  (query [[:qlkit-todo/todos (ql/get-query TodoItem)]])
  (render [this {:keys [:qlkit-todo/todos] :as atts} {:keys [new-todo] :as state}]
          (html
            [:div.content>div.columns.is-centered>div.column.is-two-thirds
             [:div.columns>div.column
              [:div.field
               [:input.input {:id          :new-todo
                              :value       (or new-todo "")
                              :placeholder "What needs to be done?"
                              :on-key-down (fn [e]
                                             (when (= (.-keyCode e) 13)
                                               (ql/transact!* this [:todo/new! {:db/id (random-uuid)
                                                                                :todo/text new-todo}])
                                               (ql/update-state!* this dissoc :new-todo)))
                              :on-change   (fn [e]
                                             (ql/update-state!* this assoc :new-todo (.-value (.-target e))))}]]]
             (when (seq todos)
               [:div.columns>div.column
                [:ol
                 (for [todo todos]
                   (ql/create-instance TodoItem todo))]])])))

(defn remote-handler [query callback]
  (POST "?endpoint"
        {:format :text
         :params query
         :handler (fn [result]
                    (callback (read-string result)))
         :error-handler (fn [e] (print "server error: " (str e)))}))

(ql/mount {:component      TodoList
           :dom-element    (.getElementById js/document "app")
           :state          app-state
           :remote-handler remote-handler
           :parsers        {:read   read
                            :mutate mutate
                            :remote remote
                            :sync   sync}})
