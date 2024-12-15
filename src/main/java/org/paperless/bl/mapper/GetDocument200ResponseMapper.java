package org.paperless.bl.mapper;

import org.mapstruct.*;
import org.paperless.model.GetDocument200Response;
import org.paperless.model.GetDocument200ResponsePermissions;
import org.paperless.model.GetDocuments200ResponseResultsInnerNotesInner;
import org.paperless.persistence.entities.DocumentsDocument;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.paperless.persistence.entities.*;
import org.paperless.persistence.repositories.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class GetDocument200ResponseMapper implements AbstractMapper<DocumentsDocument, GetDocument200Response> {
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
    @Autowired
    private AuthPermissionsMapper permissionsMapper;
    @Autowired
    private DocumentNotesMapper documentNotesMapper;

    @Mapping(target = "correspondent", source = "correspondent", qualifiedByName = "correspondentDto")
    @Mapping(target = "documentType", source = "documentType", qualifiedByName = "documentTypeDto")
    @Mapping(target = "storagePath", source = "storagePath", qualifiedByName = "storagePathDto")
    @Mapping(target = "documentDocumentsDocumentTagses", source = "tags", qualifiedByName = "tagsDto")
    @Mapping(target = "archiveSerialNumber", source = "archiveSerialNumber", qualifiedByName = "archiveSerialNumberDto")
    @Mapping(target = "owner", source = "owner", qualifiedByName = "ownerDto")
    @Mapping(target = "documentDocumentsNotes", source = "notes", qualifiedByName = "notesDto")
    abstract public DocumentsDocument dtoToEntity(GetDocument200Response dto);

    @Mapping(target = "correspondent", source = "correspondent", qualifiedByName = "correspondentEntity")
    @Mapping(target = "documentType", source = "documentType", qualifiedByName = "documentTypeEntity")
    @Mapping(target = "storagePath", source = "storagePath", qualifiedByName = "storagePathEntity")
    @Mapping(target = "tags", source = "documentDocumentsDocumentTagses", qualifiedByName = "tagsEntity")
    @Mapping(target = "createdDate", source = "created", qualifiedByName = "createdToCreatedDate")
    @Mapping(target = "owner", source = "owner", qualifiedByName = "ownerEntity")
    @Mapping(target = "permissions", source = "owner", qualifiedByName = "permissionsEntity")
    @Mapping(target = "notes", source = "documentDocumentsNotes", qualifiedByName = "notesEntity")
    abstract public GetDocument200Response entityToDto(DocumentsDocument entity);

    @Named("correspondentEntity")
    Integer map(DocumentsCorrespondent correspondent) {
        if (correspondent == null) return null;

        return correspondent.getId();
    }

    @Named("documentTypeEntity")
    Integer map(DocumentsDocumenttype documentType) {
        if (documentType == null) return null;
        return documentType.getId();
    }

    @Named("storagePathEntity")
    Integer map(DocumentsStoragepath storagePath) {
        if (storagePath == null) return null;
        return storagePath.getId();
    }

    @Named("ownerEntity")
    Integer map(AuthUser owner) {
        if (owner == null) return null;

        return owner.getId();
    }

    @Named("tagsEntity")
    List<Integer> map(Set<DocumentsDocumentTags> tags) {
        if (tags == null) return null;
        return tags.stream().map(tag -> (int) tag.getId()).toList();
    }

    @Named("permissionsEntity")
    GetDocument200ResponsePermissions mapPermissions(AuthUser owner) {
        if (owner == null) return null;
        return permissionsMapper.entityToDto(owner);
    }

    @Named("notesEntity")
    List<GetDocuments200ResponseResultsInnerNotesInner> mapNotes(Set<DocumentsNote> notes) {
        if (notes == null) return null;
        return notes.stream().map(note -> documentNotesMapper.entityToDto(note)).toList();
    }

    // map created to createdDate (Date without the time)
    @Named("createdToCreatedDate")
    OffsetDateTime mapCreatedDate(OffsetDateTime value) {
        return value != null ? value.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC) : null;
    }

    @Named("correspondentDto")
    DocumentsCorrespondent mapCorrespondent(Integer value) {
        if (value == null) return null;
        return correspondentRepository.findById(value).orElse(null);
    }

    @Named("documentTypeDto")
    DocumentsDocumenttype mapDocumentType(Integer value) {
        if (value == null) return null;

        return documentTypeRepository.findById(value).orElse(null);
    }

    @Named("storagePathDto")
    DocumentsStoragepath mapStoragePath(Integer value) {
        if (value == null) return null;
        return storagePathRepository.findById(value).orElse(null);
    }

    @Named("ownerDto")
    AuthUser mapOwner(Integer value) {
        if (value == null) return null;
        return userRepository.findById(value).orElse(null);
    }

    @Named("tagsDto")
    Set<DocumentsDocumentTags> mapDocTag(List<Integer> value) {
        if (value == null) return null;
        return new HashSet<>(documentTagsRepository.findAllById(value));
    }

    @Named("archiveSerialNumberDto")
    Integer mapArchiveSerialNumber(String value) {
        if (value == null || value.isEmpty()) return null;
        return Integer.parseInt(value);
    }

    @Named("notesDto")
    Set<DocumentsNote> mapNotes(List<GetDocuments200ResponseResultsInnerNotesInner> value) {
        if (value == null || value.isEmpty()) return null;

        HashSet<DocumentsNote> notes = new HashSet<DocumentsNote>();

        for (GetDocuments200ResponseResultsInnerNotesInner note : value) {
            notes.add(documentNotesMapper.dtoToEntity(note));
        }

        return notes;
    }

}
