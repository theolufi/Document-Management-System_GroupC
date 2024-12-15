import time

import pytest
import requests


# Basis-URL für die API des Dokumenten-Management-Systems
BASE_URL = "http://localhost:8081"

# Eine pytest-Fixture, die eine Liste von Testdateien zurückgibt
@pytest.fixture
def test_files():
    # Gibt eine Liste von Dateinamen zurück, die hochgeladen werden sollen
    return ["testPositive.pdf", "testNegative.pdf"]


# Testfunktion zum Hochladen von Dokumenten
def test_upload_document(test_files):
    # Endpoint-URL für das Hochladen von Dokumenten
    url = f"{BASE_URL}/api/documents/post_document/"
    for file_name in test_files:
        # Öffnet die Datei zum Lesen im Binärmodus
        with open(file_name, "rb") as file:
            # Erstellt ein Dictionary für die zu sendenden Dateien
            files = {"document": file}
            response = requests.post(url, files=files)
            assert response.status_code == 201

# Hilfsfunktion zum Extrahieren von Dateinamen aus der API-Antwort
def get_list_of_retrieved_filenames(response):
    # Wandelt die Antwort in JSON um und extrahiert die 'title'-Werte aus jedem Eintrag in "results"
    return list(map(lambda entry: entry["title"], response.json()["results"]))

# Testfunktion zum Abrufen von Dokumenten
def test_get_documents():
    # Endpoint-URL für das Abrufen von Dokumenten
    url = f"{BASE_URL}/api/documents/"
    response = requests.get(url)
    assert response.status_code == 200

    # Ruft die Liste der abgerufenen Dateinamen ab
    list_of_retrieved_filenames = get_list_of_retrieved_filenames(response)
    # Überprüft, ob die Testdateien in der Antwort enthalten sind
    assert "testPositive.pdf" in list_of_retrieved_filenames
    assert "testNegative.pdf" in list_of_retrieved_filenames

# Testfunktion zum Durchsuchen von Dokumenten
def test_search_documents():
    # Endpoint-URL für das Durchsuchen von Dokumenten
    url = f"{BASE_URL}/api/documents/"
    # Parameter für die Suche
    params = {"query": "Integrationtests"}
    response = requests.get(url, params=params)

    # Ruft die Liste der abgerufenen Dateinamen ab
    list_of_retrieved_filenames = get_list_of_retrieved_filenames(response)

    # Überprüft den Statuscode und das Vorhandensein/Absenz spezifischer Dateien
    assert response.status_code == 200
    assert "testPositive.pdf" in list_of_retrieved_filenames
    assert "testNegative.pdf" not in list_of_retrieved_filenames

# Testfunktion zum Abrufen eines Dokuments anhand seiner ID
def test_get_document_by_id():
    # Endpoint-URL für das Abrufen von Dokumenten
    url = f"{BASE_URL}/api/documents/"
    response = requests.get(url)
    assert response.status_code == 200

    document_id = None
    # Durchsucht die Antwort nach einem Dokument mit dem Titel 'testPositive.pdf'
    for document in response.json()["results"]:
        if document["title"] == "testPositive.pdf":
            document_id = document["id"]
            break
    # Stellt sicher, dass eine Dokumenten-ID gefunden wurde
    assert document_id is not None

    # Ruft das Dokument anhand seiner ID ab
    url = f"{BASE_URL}/api/documents/{document_id}/"
    response = requests.get(url)
    # Wartet, um der OCR genug Zeit zu geben, den Inhalt zu verarbeiten
    time.sleep(3)
    # Überprüft den Statuscode und vergleicht die Inhalte des Dokuments
    assert response.status_code == 200
    assert response.json()["title"] == "testPositive.pdf"
    assert response.json()["content"].strip() == "Integrationtests in Python!"
