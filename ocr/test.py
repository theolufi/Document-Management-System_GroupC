import unittest
from unittest.mock import patch, MagicMock, ANY

from ocr import (
    on_document_received, RabbitMQConnection, MinioConnection, PostgresConnection, ElasticSearchService,
)


class Test(unittest.TestCase):
    def test_connection(self):
        # Mock RabbitMQ connection
        connection = RabbitMQConnection("host", "username", "password")
        connection.connect = MagicMock(return_value="mock_connection")

        # Test connection
        result = connection.connect()
        self.assertEqual(result, "mock_connection")

    @patch('ocr.Minio')
    def test_connect(self, mock_minio):
        # Mock Minio
        mock_minio_client = MagicMock()
        mock_minio.return_value = mock_minio_client

        # Test Minio connection
        connection = MinioConnection("endpoint", "access_key", "secret_key")
        result = connection.connect()

        # Assertions
        self.assertEqual(result, mock_minio_client)
        mock_minio.assert_called_once_with("endpoint", access_key="access_key", secret_key="secret_key", secure=False)

    @patch('ocr.psycopg2.connect')
    def test_connect(self, mock_connect):
        # Mock psycopg2.connect
        mock_connection = MagicMock()
        mock_connect.return_value = mock_connection

        # Test PostgreSQL connection
        connection = PostgresConnection("dbname", "user", "password", "host", "port")
        result = connection.connect()

        # Assertions
        self.assertEqual(result, mock_connection)
        mock_connect.assert_called_once_with(dbname="dbname", user="user", password="password", host="host",
                                             port="port")

    @patch('ocr.Elasticsearch')
    def test_init(self, mock_elasticsearch):
        # Mock Elasticsearch
        mock_es_client = MagicMock()
        mock_elasticsearch.return_value = mock_es_client

        # Test Elasticsearch initialization
        es_service = ElasticSearchService("endpoint", "username", "password")

        # Assertions
        self.assertEqual(es_service.es, mock_es_client)
        mock_elasticsearch.assert_called_once_with(["endpoint"], basic_auth=("username", "password"))

    @patch('ocr.Minio')
    @patch('psycopg2.connect')
    @patch('ocr.convert_from_bytes', return_value=[MagicMock()])
    @patch('pytesseract.image_to_string', return_value='mocked text')
    def test_on_document_received(self, mock_image_to_string, convert_from_bytes_mock, mock_connect, mock_minio):
        # Mocking Minio client
        mock_minio_instance = MagicMock()
        mock_minio.return_value = mock_minio_instance
        mock_minio_instance.get_object.return_value.data = b"Mock PDF data"

        # Mocking PostgreSQL connection
        mock_conn = MagicMock()
        mock_es = MagicMock()

        on_document_received(b"bucket_name/document_path", mock_minio_instance, mock_conn, mock_es)

        # Assertions
        mock_minio_instance.get_object.assert_called_once_with("bucket_name", "document_path")
        self.assertEqual(mock_conn.cursor().execute.call_count, 2)
        expected_update_sql = "UPDATE documents_document SET content = %s WHERE id = %s;"
        mock_conn.cursor().execute.assert_called_with(expected_update_sql, (
            "mocked text\n", ANY))
        mock_conn.commit.assert_called_once()
        mock_conn.cursor().close.assert_called_once()

        mock_es.index.assert_called_once_with({
            "document_name": "document_path",
            "body": "mocked text\n"
        })


if __name__ == "__main__":
    unittest.main()
