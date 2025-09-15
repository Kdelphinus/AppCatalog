package com.appcatalog.target.domain;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetEnvironmentRepository extends JpaRepository<TargetEnvironment, Long> {

  boolean existsByName(String name);
}
