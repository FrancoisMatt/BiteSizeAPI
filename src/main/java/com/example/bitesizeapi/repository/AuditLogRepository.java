package com.example.bitesizeapi.repository;

import com.example.bitesizeapi.model.AuditLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, Integer> {

}