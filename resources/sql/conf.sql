-- sql/conf.sql

-- :name get-keys-sql :? :*
SELECT key_path, secret
  FROM conf.keys

-- :name get-key-values-sql :? :*
SELECT conf_values.id, conf_values.key_path, keys.secret,
       CASE WHEN keys.secret THEN '*********'
            ELSE conf_values.conf_value
             END AS conf_value
FROM conf.conf_values
JOIN conf.keys ON keys.key_path = conf_values.key_path
WHERE conf_values.key_path = :key

-- :name get-services-sql :? :*
  SELECT service
    FROM core.services
ORDER BY service

-- :name get-service-key-values-sql :? :*
  SELECT conf_values.id, conf_values.key_path, keys.secret,
         CASE WHEN keys.secret THEN '*********'
              ELSE conf_values.conf_value
              END AS conf_value
    FROM conf.conf_service_values
    JOIN conf.conf_values ON conf_service_values.value_id = conf_values.id
    JOIN conf.keys ON keys.key_path = conf_service_values.key_path
    WHERE conf_service_values.service = :service
ORDER BY conf_values.key_path

-- :name insert-key-sql :! :n
INSERT INTO conf.keys (key_path, secret, parser)
     VALUES (:key_path, :secret, :parser)
ON CONFLICT (key_path) DO NOTHING

-- :name insert-key-value-sql :! :n
INSERT INTO conf.conf_values(id,key_path,conf_value)
VALUES (:id,:key_path,:conf_value)

-- :name set-service-key-value-sql :! :n
INSERT INTO conf.conf_service_values (service,key_path,value_id)
VALUES (:service , :key_path , :id)
ON CONFLICT ON CONSTRAINT conf_service_values_pk DO UPDATE SET value_id = EXCLUDED.value_id

-- :name get-value-by-id-sql :? :1
SELECT id, key_path
  FROM conf.conf_values
  WHERE conf_values.id = :id

-- :name remove-service-key-value-sql :! :n
DELETE FROM conf.conf_service_values
WHERE service = :service AND key_path = :key_path
