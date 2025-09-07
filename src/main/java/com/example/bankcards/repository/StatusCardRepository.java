package com.example.bankcards.repository;

import com.example.bankcards.entity.StatusCard;
import com.example.bankcards.entity.StatusCardEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusCardRepository extends JpaRepository<StatusCard, Long> {
    StatusCard findByName(StatusCardEnum name);
}
