package com.example.bankcards.repository;

import com.example.bankcards.entity.RoleEnum;
import com.example.bankcards.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleUserRepository extends JpaRepository<RoleUser, Long> {
    RoleUser findByName(RoleEnum name);

}
