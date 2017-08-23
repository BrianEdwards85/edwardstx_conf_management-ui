-- src/us/edwardstx/conf/management_ui/data/sql/conf.sql

-- :name get-keys-sql :? :*
SELECT key_path, secret
  FROM conf.keys

-- :name get-key-values-sql :? :*
SELECT conf_values.id, conf_values.key_path,
CASE WHEN keys.secret THEN '*********'
     ELSE conf_values.conf_value
     END AS conf_value, keys.secret
FROM conf.conf_values
JOIN conf.keys ON keys.key_path = conf_values.key_path
WHERE conf_values.key_path = :key

