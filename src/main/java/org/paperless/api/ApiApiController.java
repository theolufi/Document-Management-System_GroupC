package org.paperless.api;


import org.openapitools.jackson.nullable.JsonNullable;
import org.paperless.bl.services.DocumentService;
import org.paperless.model.DocumentDTO;
import org.paperless.model.GetDocument200Response;
import org.paperless.model.GetDocuments200Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.paperless.codegen.languages.SpringCodegen", date = "2024-02-17T12:03:05.936104740Z[Etc/UTC]")
@Controller
@RequestMapping("${openapi.paperlessRestServer.base-path:}")
public class ApiApiController implements ApiApi {
    private final DocumentService documentService;
    private final NativeWebRequest request;

    @Autowired
    public ApiApiController(NativeWebRequest request, DocumentService documentService) {
        this.request = request;
        this.documentService = documentService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }


    @Override
    public ResponseEntity<Void> uploadDocument(String title, OffsetDateTime created, Integer documentType, List<Integer> tags, Integer correspondent, List<MultipartFile> documentArg) {
        try {

            String name = documentArg.get(0).getOriginalFilename();
            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setTitle(JsonNullable.of(title == null ? name : title));
            documentDTO.setOriginalFileName(JsonNullable.of(name));
            documentDTO.setCreated(created);
            documentDTO.setDocumentType(JsonNullable.of(documentType));
            documentDTO.setTags(JsonNullable.of(tags));
            documentDTO.setCorrespondent(JsonNullable.of(correspondent));

            MultipartFile file = documentArg.get(0);

            if(file == null || file.isEmpty()){
                return ResponseEntity.badRequest().build();
            }

            documentService.uploadDocument(documentDTO, file);
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<GetDocument200Response> getDocument(Integer id, Integer page, Boolean fullPerms) {
        return ResponseEntity.ok(documentService.getDocument(id, page, fullPerms));
    }

    @Override
    public ResponseEntity<GetDocuments200Response> getDocuments(Integer page, Integer pageSize, String query, String ordering, List<Integer> tagsIdAll, Integer documentTypeId, Integer storagePathIdIn, Integer correspondentId, Boolean truncateContent) throws IOException {
        return documentService.getDocuments(page, pageSize, query, ordering, tagsIdAll, documentTypeId, storagePathIdIn, correspondentId, truncateContent);
    }
}
