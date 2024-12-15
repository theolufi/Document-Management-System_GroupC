package org.paperless.bl.mapper;

import org.mapstruct.*;
import org.paperless.model.GetDocument200ResponsePermissions;
import org.paperless.model.GetDocument200ResponsePermissionsView;
import org.paperless.persistence.entities.AuthUser;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class AuthPermissionsMapper implements AbstractMapper<AuthUser, GetDocument200ResponsePermissions> {


    @Mapping(target = "view", source = "id", qualifiedByName = "viewEntity")
    @Mapping(target = "change", source = "id", qualifiedByName = "changeEntity")
    abstract public GetDocument200ResponsePermissions entityToDto(AuthUser entity);

    @Named("viewEntity")
    GetDocument200ResponsePermissionsView map1(Integer id) {
        if (id == null)
            return new GetDocument200ResponsePermissionsView();
        return new GetDocument200ResponsePermissionsView().addUsersItem(id);
    }

    @Named("changeEntity")
    GetDocument200ResponsePermissionsView map2(Integer id) {
        if (id == null)
            return new GetDocument200ResponsePermissionsView();
        return new GetDocument200ResponsePermissionsView().addUsersItem(id);
    }

}
