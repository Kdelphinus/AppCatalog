package com.appcatalog.orchestrator.target.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TARGET_ENVIRONMENTS")
@Getter
@Setter
public class TargetEnvironment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TargetType type;

    private String host;
    private int port;
    private String credentialId; // For Vault or other secret management

    public enum TargetType {
        KUBERNETES,
        VM
    }
}
