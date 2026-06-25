package com.msa.userservice.repositories;

import com.msa.userservice.entities.Role;
import com.msa.userservice.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    List<Role> findByName(RoleName name);
}