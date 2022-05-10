(ns bedrock.utils.jdbc
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.data.json :as json]))

(defn create-entity-table! [db table]
  (let [cmd (jdbc/create-table-ddl table
                                   [[:id "varchar(36)" "not null"]
                                    [:payload "varchar" "not null"]
                                    #_[:payload "json" "not null"]]
                                   {:conditional? true})]
    (jdbc/db-do-commands db [cmd])))

(defn get-entity-list [db table]
 (jdbc/query db [(str "select id, payload from " (name table))]
              {:row-fn (juxt :id (comp json/read-str :payload))}))

(defn get-entity-id-list [db table]
  (jdbc/query db [(str "select id from " (name table))]
              {:row-fn :id}))

(defn get-entity [db table id not-found]
  (if-let [r (not-empty (jdbc/query db [(str "select payload from " (name table) " where id=?") id]
                                    {:row-fn (comp json/read-str :payload)}))]
    (first r)
    not-found))

(defn- update-entity! [db table id value not-found]
  (when (= 0 (jdbc/update! db table {:payload (json/write-str value)} ["id = ?" id]))
    not-found)
  
  ;; probably quite H2 specific :-(
  #_(let [r (jdbc/execute! db [(str "update " (name table) " set payload = ? FORMAT JSON where id = ?") (json/write-str value)  id])]
      (when (= (first r) 0)
        not-found)))

(defn- insert-entity! [db table id value]
  (jdbc/insert! db table {:id id :payload (json/write-str value)})

  ;; TODO: quote table?!
  ;; probably quite H2 specific :-(
  #_(jdbc/execute! db [(str "insert into " (name table) " (id, payload) values (?, ? FORMAT JSON)") id (json/write-str value)]))

(defn put-entity! [db table id value]
  (jdbc/with-db-transaction [tx db]
    (when (= ::not-found (update-entity! tx table id value ::not-found))
      (insert-entity! tx table id value))))

(defn create-entity! [db table value]
  (jdbc/with-db-transaction [tx db]
    (loop []
      (let [new-id (.toString (java.util.UUID/randomUUID))]
        (if (= ::not-found (get-entity tx table new-id ::not-found))
          (do (insert-entity! tx table new-id value)
              new-id)
          (recur))))))

(defn delete-entity! [db table id]
  (jdbc/delete! db table ["id = ?" id]))
