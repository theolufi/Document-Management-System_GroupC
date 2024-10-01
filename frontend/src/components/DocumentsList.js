import React, { useState, useEffect } from 'react';
import axios from 'axios';

const DocumentsList = () => {
    const [documents, setDocuments] = useState([]);

    useEffect(() => {
        console.log('Fetching documents...');
        axios.get('http://localhost:8081/api/documents/1')
            .then(response => {
                console.log('Documents fetched:', response.data); // Log the data for debugging
                setDocuments(response.data);
            })
            .catch(error => {
                console.error('Error fetching documents:', error); // Log any errors
            });
    }, []); 

    return (
        <div>
            <h1>Documents</h1>
            {documents.length === 0 ? (
                <p>No documents available</p>
            ) : (
                <ul>
                    {documents.map(document => (
                        <li key={document.id}>
                            {document.title}: {document.content}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default DocumentsList;
