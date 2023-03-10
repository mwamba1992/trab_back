package tz.go.mof.trab.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import tz.go.mof.trab.models.Permission;

import java.util.Optional;


@Transactional
@RepositoryRestResource(collectionResourceRel = "permissions", path = "permissions")
public interface PermissionRepository  extends JpaRepository<Permission, String> {


    Optional<Permission> findByName(String name);

    Permission findByDisplayName(String displayName);

    Optional<Permission> findById(String id);
}
