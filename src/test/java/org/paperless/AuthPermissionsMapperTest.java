package org.paperless;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paperless.bl.mapper.AuthPermissionsMapperImpl;
import org.paperless.model.GetDocument200ResponsePermissions;
import org.paperless.persistence.entities.AuthUser;
import org.paperless.persistence.repositories.AuthUserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class AuthPermissionsMapperTest {

    @Mock
    private AuthUserRepository userRepository;

    @InjectMocks
    private AuthPermissionsMapperImpl mapper;

    @Test
    public void testEntityToDto() {
        // Mock entity
        AuthUser entity = new AuthUser();
        entity.setId(1);

        // Mock behavior of repositories

        // Perform mapping
        GetDocument200ResponsePermissions permissions = mapper.entityToDto(entity);

        // Verify mapping
        assertEquals(1, permissions.getView().getUsers().size());
        assertEquals(1, permissions.getChange().getUsers().size());
    }
}
