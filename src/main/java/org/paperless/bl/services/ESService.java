package org.paperless.bl.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.paperless.configuration.ElasticSearchConfig;
import org.paperless.persistence.entities.DocumentsDocument;
import org.paperless.persistence.repositories.DocumentsDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ESService implements IESService {
    private final ElasticsearchClient esClient;
    private final DocumentsDocumentRepository documentRepository;

    @Autowired
    public ESService(ElasticsearchClient esClient, DocumentsDocumentRepository documentRepository) throws IOException {
        this.esClient = esClient;
        this.documentRepository = documentRepository;
    }

    @Override
    public List<DocumentsDocument> searchEsForDocument(String searchTerm) throws IOException {
        SearchResponse<ObjectNode> response = esClient.search(s -> s
                        .index(ElasticSearchConfig.DOCUMENTS_INDEX_NAME)
                        .size(1000)
                        .query(q -> q.match(m -> m.field("body").query(searchTerm).fuzziness("AUTO"))),
                ObjectNode.class
        );

        if (response.hits().total() != null) {
            logSearchResult(response.hits().total().value());
        }

        return getDocuments(response.hits());
    }

    private void logSearchResult(long totalHits) {
        if (totalHits != 0) {
            log.info("Found {} documents", totalHits);
        } else {
            log.info("No documents found");
        }
    }

    private List<DocumentsDocument> getDocuments(HitsMetadata<ObjectNode> hitsMetadata) {
        List<DocumentsDocument> documentEntities = new ArrayList<>();

        for (Hit<ObjectNode> hit : hitsMetadata.hits()) {
            String document_name = "";
            if (hit.source() != null) {
                document_name = hit.source().get("document_name").asText();
            }

            List<DocumentsDocument> byTitleOrderById = documentRepository.findByTitleOrderById(document_name);
            if (!byTitleOrderById.isEmpty()) {
                documentEntities.add(byTitleOrderById.get(byTitleOrderById.size() - 1));
            }
        }

        return documentEntities;
    }
}
