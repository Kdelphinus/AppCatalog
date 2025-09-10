package com.appcatalog.deployment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentJobRepository extends JpaRepository<DeploymentJob, Long> {}
