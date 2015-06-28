(ns tst.tupelo.datomic
  (:use tupelo.core
        clojure.test )
  (:require [datomic.api      :as d]
            [tupelo.datomic   :as t]
            [tupelo.schema    :as ts]
            [schema.core      :as s]))

(set! *warn-on-reflection* false)
(set! *print-length* 5)
(set! *print-length* nil)

;---------------------------------------------------------------------------------------------------
; Prismatic Schema type definitions
(s/set-fn-validation! true)   ; #todo add to Schema docs

(def ^:dynamic *conn*)

(use-fixtures :each
  (fn [tst-fn]
    ; Create the database & a connection to it
    (let [uri           "datomic:mem://tupelo"
          _ (d/create-database uri)
          conn          (d/connect uri)
    ]
      (binding [*conn* conn]
        (tst-fn))
      (d/delete-database uri)
    )))


(deftest t-new-partition
  (let [result   (t/new-partition :people ) ]
    (is (matches? result
           {:db/id                    #db/id[:db.part/db _] 
            :db.install/_partition    :db.part/db 
            :db/ident                 :people} )))
  (let [result   (t/new-partition :part.with.ns ) ]
    (is (matches? result
           {:db/id                    #db/id[:db.part/db _] 
            :db.install/_partition    :db.part/db 
            :db/ident                 :part.with.ns} )))
)

(deftest t-new-attribute
  (testing "basic"
    (let [result  (t/new-attribute :weapon/type :db.type/keyword 
                      :db.unique/value       :db.unique/identity 
                      :db.cardinality/one    :db.cardinality/many 
                      :db/index :db/fulltext :db/isComponent :db/noHistory ) ]
      (is (s/validate datomic.db.DbId (:db/id result)))
      (is (matches? result
              {:db/id           _       :db/ident               :weapon/type
               :db/index        true    :db/unique              :db.unique/identity  
               :db/noHistory    true    :db/cardinality         :db.cardinality/many
               :db/isComponent  true    :db.install/_attribute  :db.part/db 
               :db/fulltext     true    :db/valueType           :db.type/keyword } )))
               
    (let [result  (t/new-attribute :weapon/type :db.type/keyword 
                      :db.unique/identity    :db.unique/value
                      :db.cardinality/many   :db.cardinality/one
                      :db/index :db/fulltext :db/isComponent :db/noHistory ) ]
      (is (matches? result
              {:db/id           _       :db/ident               :weapon/type
               :db/index        true    :db/unique              :db.unique/value  
               :db/noHistory    true    :db/cardinality         :db.cardinality/one  
               :db/isComponent  true    :db.install/_attribute  :db.part/db 
               :db/fulltext     true    :db/valueType           :db.type/keyword } ))))

  (testing "types"
    (let [result  (t/new-attribute :location :db.type/string) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/string       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/keyword) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/keyword       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/boolean) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/boolean       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/long) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/long       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/bigint) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/bigint       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/float) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/float       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/double) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/double       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/bigdec) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/bigdec       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/bytes) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/bytes       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/instant) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/instant       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/uuid) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/uuid       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/uri) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/uri       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/ref) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/ref       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } ))))

  (testing "cardinality & unique"
    (let [result  (t/new-attribute :location :db.type/string) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/string       :db/cardinality :db.cardinality/one 
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/string :db.cardinality/many) ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/string       :db/cardinality :db.cardinality/many
               :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/string :db.unique/value)  ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/string       :db/cardinality :db.cardinality/one 
               :db/unique       :db.unique/value      :db.install/_attribute :db.part/db } )))
    (let [result  (t/new-attribute :location :db.type/string :db.unique/identity)  ]
      (is (matches? result
              {:db/id           _                     :db/ident       :location 
               :db/valueType    :db.type/string       :db/cardinality :db.cardinality/one 
               :db/unique       :db.unique/identity   :db.install/_attribute :db.part/db } ))))
)


(deftest t-new-entity
  (testing "new-entity"
    (let [result  (t/new-entity     {:person/name "dilbert" :job/type :job.type/sucky} ) ]
      (is (matches? result {:db/id _ :person/name "dilbert" :job/type :job.type/sucky} ))
    )
  )
  (testing "new-entity with partition"
    (let [result  (t/new-entity  :dummy.part/name   {:person/name "dilbert" :job/type :job.type/sucky} ) 
          dbid    (grab :db/id result) 
          part1   (first dbid)
          part2   (second dbid) ]
      ; result: {:db/id #db/id[:dummy.part/name -1000003] :person/name "dilbert" :job/type :job.type/sucky}
      (is (matches? result   {:db/id _  :person/name "dilbert" :job/type :job.type/sucky} ))
      (is (matches? dbid #db/id[:dummy.part/name _] ))  ; #db/id[:dummy.part/name -1000003]
      (is (matches? part1 [:part :dummy.part/name]))
      (is (matches? part2 [:idx _]))
      (is (s/validate ts/Eid (second part2))))))

(deftest t-new-enum
  (is (matches? (t/new-enum :weapon.type/gun)
                {:db/id #db/id[:db.part/user _], :db/ident :weapon.type/gun} ))
  (is (matches? (t/new-enum :gun)
                {:db/id #db/id[:db.part/user _], :db/ident :gun} ))
  (is (thrown? Exception (t/new-enum "gun"))))

#_(deftest t-xx
  (testing "xx"
      (let [result  
      ]
        (spyxx result)
      )
  ))

#_(deftest t-xx
  (testing "xx"
      (let [result  
      ]
        (spyxx result)
      )
  ))

#_(deftest t-xx
  (testing "xx"
      (let [result  
      ]
        (spyxx result)
      )
  ))

#_(deftest t-xx
  (testing "xx"
      (let [result  
      ]
        (spyxx result)
      )
  ))

#_(deftest t-xx
  (testing "xx"
      (let [result  
      ]
        (spyxx result)
      )
  ))

