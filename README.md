Release manager
-------

To start run `docker-compose up -d`

To test versions endpoint use:

```shell
curl --location --request GET 'http://localhost:8080/services?systemVersion=2' \
--header 'Content-Type: application/json' \
--data '{
    "serviceName": "sender",
    "serviceVersionNumber": 2
}'
```

To test deploy endpoint use:

```shell
curl --location 'http://localhost:8080/deploy' \
--header 'Content-Type: application/json' \
--data '{
    "serviceName": "sender",
    "serviceVersionNumber": 5
}'
```