To start the project run

```
docker-compose up --build
```

If the ocr worker is not starting properly this means the ES container was not ready yet, just restarted the ocr container in docker desktop and wait a little while.


To have a look at the postgres table run

```
docker exec -it postgres psql -U postgres -d paperless -c "SELECT * FROM documents_document;"
```

