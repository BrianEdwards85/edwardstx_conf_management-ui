(ns us.edwardstx.common.logging
  (:require [com.stuartsierra.component :as component])
  (:import java.nio.charset.Charset
           org.apache.logging.log4j.Level
           org.apache.logging.log4j.LogManager
           org.apache.logging.log4j.core.layout.JsonLayout
           org.springframework.amqp.rabbit.log4j2.AmqpAppender))

(def default-appender-settings {:appender-name "rabbitmq"
                                :declareExchange true
                                :exchangeType "topic"
                                :durable true
                                :autoDelete false
                                :deliveryMode "NON_PERSISTENT"
                                :routingKeyPattern "%X{applicationId}.%c.%p"
                                :contentType "text/plain"
                                :contentEncoding "UTF-8"
                                :generateId true
                                :charset "UTF-8"
                                :senderPoolSize 4
                                :maxSenderRetries 30})

(defn create-json-layout [configuration]
  (JsonLayout/createLayout configuration  ;;configuration
                           false          ;;locationInfo
                           false          ;;properties
                           false          ;;propertiesAsList
                           false          ;;complete
                           false          ;;compact
                           false          ;;eventEol
                           "["            ;;headerPattern
                           "]"            ;;footerPattern
                           (Charset/forName "UTF-8")        ;;charset
                           true))         ;;includeStacktrace


(defn create-appender [configuration layout
                       {:keys [host
                               port
                               ssl
                               username
                               password
                               vhost
                               exchange
                               service-name
                               ;;Defaults
                               appender-name
                               exchangeType
                               declareExchange
                               durable
                               autoDelete
                               routingKeyPattern
                               contentType
                               contentEncoding
                               generateId
                               deliveryMode
                               charset
                               senderPoolSize
                               maxSenderRetries]}]
    (AmqpAppender/createAppender configuration     ;;configuration
                                 appender-name     ;;name
                                 layout            ;;layout
                                 nil               ;;filter
                                 false             ;;ignoreExceptions
                                 nil               ;;uri
                                 host              ;;host
                                 port              ;;port
                                 nil               ;;addresses
                                 username          ;;user
                                 password          ;;password
                                 vhost             ;;virtualHost
                                 ssl               ;;useSsl
                                 nil               ;;sslAlgorithm
                                 nil               ;;sslPropertiesLocation
                                 nil               ;;keyStore
                                 nil               ;;keyStorePassphrase
                                 nil               ;;keyStoreType
                                 nil               ;;trustStore
                                 nil               ;;trustStorePassphrase
                                 nil               ;;trustStoreType
                                 senderPoolSize    ;;senderPoolSize
                                 maxSenderRetries  ;;maxSenderRetries
                                 service-name      ;;applicationId
                                 routingKeyPattern ;;routingKeyPattern
                                 generateId        ;;generateId
                                 deliveryMode      ;;deliveryMode
                                 exchange          ;;exchange
                                 exchangeType      ;;exchangeType
                                 declareExchange   ;;declareExchange
                                 durable           ;;durable
                                 autoDelete        ;;autoDelete
                                 contentType       ;;contentType
                                 contentEncoding   ;;contentEncoding
                                 nil               ;;clientConnectionProperties
                                 true              ;;async
                                 charset           ;;charset
                                 ))

(defn update-config [opts]
  (let [ctx       (LogManager/getContext false)
        config    (.getConfiguration ctx)
        layout    (create-json-layout config)
        appender  (create-appender config layout opts)
        root      (.getRootLogger config)
        cap-key   (-> root .getAppenders .keySet first)
        ]
    (.removeAppender root cap-key)
    (.setLevel root Level/DEBUG)
    (.start appender)
    (.addAppender config appender)
    (.addAppender root appender Level/DEBUG nil)
    (.updateLoggers ctx)
    ctx))

(defrecord Logging [conf ctx]
  component/Lifecycle

  (start [this]
    (assoc this :ctx
           (update-config
            (merge
             default-appender-settings
             (get-in conf [:conf :rabbit])
             (get-in conf [:conf :logging])
             (select-keys (:conf conf) [:service-name :service-id])))))

  (stop [this]
    (assoc this :ctx nil))

  )

(defn new-logging []
  (component/using
   (map->Logging {})
   [:conf]))

(comment
  (clojure.tools.logging/info "Hello info")

  (clojure.tools.logging/error "HELLO ERROR!")

  (update-config appender-settings)

  (use 'us.edwardstx.conf.logging :reload)

  (in-ns 'us.edwardstx.conf.logging)

  (def ctx (LogManager/getContext false))

  (def config (.getConfiguration ctx))

  (def root (.getRootLogger config))

  )
