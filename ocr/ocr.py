import os
import time

import pika
import psycopg2
import pytesseract
from dotenv import load_dotenv
from minio import Minio
from pdf2image import convert_from_bytes
from elasticsearch import Elasticsearch


class RabbitMQConnection:
    def __init__(self, host, username, password):
        self.host = host
        self.username = username
        self.password = password

    def connect(self):
        try:
            connection_params = pika.ConnectionParameters(
                host=self.host,
                credentials=pika.PlainCredentials(self.username, self.password)
            )
            connection = pika.BlockingConnection(connection_params)
            return connection
        except Exception as e:
            print("Error: Could not connect to RabbitMQ")
            raise e


class MinioConnection:
    def __init__(self, endpoint, access_key, secret_key):
        self.endpoint = endpoint
        self.access_key = access_key
        self.secret_key = secret_key

    def connect(self):
        try:
            minio_client = Minio(self.endpoint, access_key=self.access_key, secret_key=self.secret_key, secure=False)
            return minio_client
        except Exception as e:
            print("Error: Could not connect to MinIO")
            raise e


class PostgresConnection:
    def __init__(self, dbname, user, password, host, port):
        self.dbname = dbname
        self.user = user
        self.password = password
        self.host = host
        self.port = port

    def connect(self):
        try:
            conn = psycopg2.connect(
                dbname=self.dbname,
                user=self.user,
                password=self.password,
                host=self.host,
                port=self.port
            )
            return conn
        except Exception as e:
            print("Error: Could not connect to PostgreSQL")
            raise e


class ElasticSearchService:
    def __init__(self, endpoint, username, password):
        self.endpoint = endpoint
        self.username = username
        self.password = password

        self.es = Elasticsearch([self.endpoint], basic_auth=(self.username, self.password))

        if not self.es.indices.exists(index="paperless"):
            self.es.indices.create(index="paperless")

    def index(self, document):
        try:
            print(f"Indexing with: {document}")
            response = self.es.index(
                index="paperless",
                id=document["document_name"],
                body=document
            )

            return response['result']

        except Exception as e:
            print(f"Error on ID {document['document_name']}: {e}")
            return None


def update_document_content(conn, text, title):
    try:
        cur = conn.cursor()
        sql_find_id = "SELECT id FROM documents_document WHERE title = %s ORDER BY id DESC LIMIT 1;"
        cur.execute(sql_find_id, (title,))
        result = cur.fetchone()
        if result:
            doc_id = result[0]
        else:
            print("Error: No document found with the given title")
            return

        sql_update = "UPDATE documents_document SET content = %s WHERE id = %s;"
        cur.execute(sql_update, (text, doc_id))

        conn.commit()
        cur.close()
    except Exception as e:
        print("Error: Could not add to database")
        raise e


def on_document_received(body, minio_client, conn, es_service):
    try:
        file_url = body.decode('utf-8')
        bucket_name = file_url.split('/')[0]
        file_path = file_url.split('/')[1]

        file_data = minio_client.get_object(bucket_name, file_path)
        # Parse the PDF into images that can be processed
        doc = convert_from_bytes(file_data.data)
        content = ""
        for page, image_data in enumerate(doc):
            content += pytesseract.image_to_string(image_data, lang="deu") + "\n"

        update_document_content(conn, content, file_path)
        es_service.index({
            "document_name": file_path,
            "body": content
        })
        print("Document processed successfully" + file_path)
    except Exception as e:
        print("Error: Could not process file")
        raise e


def main():
    load_dotenv("config.env")
    print("Starting...")
    time.sleep(90)

    rabbitmq_connection = RabbitMQConnection(
        os.getenv("RABBITMQ_HOST"),
        os.getenv("RABBITMQ_USERNAME"),
        os.getenv("RABBITMQ_PASSWORD")
    )
    minio_connection = MinioConnection(
        os.getenv("MINIO_ENDPOINT"),
        os.getenv("MINIO_ACCESS_KEY"),
        os.getenv("MINIO_SECRET_KEY")
    )
    postgres_connection = PostgresConnection(
        os.getenv("POSTGRES_DB"),
        os.getenv("POSTGRES_USER"),
        os.getenv("POSTGRES_PASSWORD"),
        os.getenv("POSTGRES_HOST"),
        os.getenv("POSTGRES_PORT")
    )

    es_service = ElasticSearchService(
        os.getenv("ES_HOST"),
        os.getenv("ES_USERNAME"),
        os.getenv("ES_PASSWORD")
    )

    rabbitmq_conn = rabbitmq_connection.connect()
    minio_client = minio_connection.connect()
    postgres_conn = postgres_connection.connect()

    channel = rabbitmq_conn.channel()
    channel.queue_declare(queue='ORC_DOCUMENT_IN')

    channel.basic_consume(queue='ORC_DOCUMENT_IN',
                          on_message_callback=lambda ch, method, properties, body: on_document_received(body,
                                                                                                        minio_client,
                                                                                                        postgres_conn,
                                                                                                        es_service),
                          auto_ack=True)

    print("OCR started!")
    channel.start_consuming()


if __name__ == "__main__":
    main()
