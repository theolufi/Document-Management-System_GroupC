package org.paperless.bl.mapper;

import org.mapstruct.*;
import org.paperless.model.DocumentDTO;
import org.openapitools.jackson.nullable.JsonNullable;
import org.paperless.persistence.entities.*;
import org.paperless.persistence.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Service
public abstract class DocumentMapper implements AbstractMapper<DocumentsDocument, DocumentDTO> {

    @Autowired
    private DocumentsCorrespondentRepository correspondentRepository;
    @Autowired
    private DocumentsDocumenttypeRepository documentTypeRepository;
    @Autowired
    private DocumentsStoragepathRepository storagePathRepository;
    @Autowired
    private AuthUserRepository userRepository;
    @Autowired
    private DocumentsDocumentTagsRepository documentTagsRepository;

    @Mapping(target = "documentDocumentsDocumentTagses", source = "tags", qualifiedByName = "hiddenTagsDto")
    @Mapping(target = "archiveSerialNumber", source = "archiveSerialNumber", qualifiedByName = "hiddenArchiveSerialNumberDto")
    @Mapping(target = "correspondent", source = "correspondent", qualifiedByName = "hiddenCorrespondentDto")
    @Mapping(target = "documentType", source = "documentType", qualifiedByName = "hiddenDocumentTypeDto")
    @Mapping(target = "storagePath", source = "storagePath", qualifiedByName = "hiddenStoragePathDto")
    abstract public DocumentsDocument dtoToEntity(DocumentDTO dto);


    @Mapping(target = "tags", source = "documentDocumentsDocumentTagses", qualifiedByName = "hiddenTagsEntity")
    @Mapping(target = "createdDate", source = "created", qualifiedByName = "hiddenCreatedToCreatedDate")
    @Mapping(target = "correspondent", source = "correspondent", qualifiedByName = "hiddenCorrespondentEntity")
    @Mapping(target = "documentType", source = "documentType", qualifiedByName = "hiddenDocumentTypeEntity")
    @Mapping(target = "storagePath", source = "storagePath", qualifiedByName = "hiddenStoragePathEntity")
    abstract public DocumentDTO entityToDto(DocumentsDocument entity);

    @Named("hiddenCorrespondentEntity")
    JsonNullable<Integer> mapHiddenCorrespondent(DocumentsCorrespondent correspondent) {
        return correspondent!=null ? JsonNullable.of(correspondent.getId()) : JsonNullable.undefined();
    }

    @Named("hiddenDocumentTypeEntity")
    JsonNullable<Integer> mapHiddenDocumentType(DocumentsDocumenttype documentType) {
        return documentType!=null ? JsonNullable.of(documentType.getId()) : JsonNullable.undefined();
    }

    @Named("hiddenStoragePathEntity")
    JsonNullable<Integer> mapHiddenStoragePath(DocumentsStoragepath storagePath) {
        return storagePath!=null ? JsonNullable.of(storagePath.getId()) : JsonNullable.undefined();
    }

    @Named("hiddenOwnerEntity")
    JsonNullable<Integer> mapHiddenOwner(AuthUser owner) {
        return owner!=null ? JsonNullable.of(owner.getId()) : JsonNullable.undefined();
    }

    @Named("hiddenTagsEntity")
    JsonNullable<List<Integer>> mapHiddenTags(Set<DocumentsDocumentTags> tags) {
        return tags!=null ? JsonNullable.of( tags.stream().map( tag->(int)tag.getId() ).toList() ) : JsonNullable.undefined();
    }

    @Named("hiddenCreatedToCreatedDate")
    OffsetDateTime mapHiddenCreatedDate(OffsetDateTime value) {
        return value!=null ? value.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC) : null;
    }

    @Named("hiddenCorrespondentDto")
    DocumentsCorrespondent mapHiddenCorrespondentDto(JsonNullable<Integer> value) {
        if(value==null || !value.isPresent() || value.get()==null) return null;

        return correspondentRepository.findById(value.get()).orElse(null);
    }

    @Named("hiddenDocumentTypeDto")
    DocumentsDocumenttype mapHiddenDocumentTypeDto(JsonNullable<Integer> value) {
        if(value==null || !value.isPresent() || value.get()==null) return null;

        return documentTypeRepository.findById(value.get()).orElse(null);
    }

    @Named("hiddenStoragePathDto")
    DocumentsStoragepath mapHiddenStoragePathDto(JsonNullable<Integer> value) {
        if(value==null || !value.isPresent() || value.get()==null) return null;

        return storagePathRepository.findById(value.get()).orElse(null);
    }

    @Named("hiddenOwnerDto")
    AuthUser mapHiddenOwnerDto(JsonNullable<Integer> value) {
        if(value==null || !value.isPresent() || value.get()==null) return null;

        return userRepository.findById(value.get()).orElse(null);
    }

    @Named("hiddenTagsDto")
    Set<DocumentsDocumentTags> mapHiddenTagsDto(JsonNullable<List<Integer>> value) {
        if(value==null || !value.isPresent() || value.get()==null) return null;

        return new HashSet<>(documentTagsRepository.findAllById(value.get()));
    }

    @Named("hiddenArchiveSerialNumberDto")
    Integer mapHiddenArchiveSerialNumberDto(JsonNullable<String> value) {
        if(value==null || !value.isPresent() || value.get()==null) return null;
        return Integer.parseInt(value.get());
    }
}
