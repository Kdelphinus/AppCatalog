package com.appcatalog.orchestrator.target.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetEnvironmentRepository extends JpaRepository<TargetEnvironment, Long> {
}
