package org.paperless.bl.services;

import org.paperless.model.DocumentDTO;
import org.paperless.model.GetDocument200Response;
import org.paperless.model.GetDocuments200Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IDocumentService {

    void uploadDocument(DocumentDTO documentDTO, MultipartFile file);

    GetDocument200Response getDocument(Integer id, Integer page, Boolean fullPerms);

    ResponseEntity<GetDocuments200Response> getDocuments(Integer page, Integer pageSize, String query, String ordering, List<Integer> tagsIdAll, Integer documentTypeId, Integer storagePathIdIn, Integer correspondentId, Boolean truncateContent) throws IOException;

}
