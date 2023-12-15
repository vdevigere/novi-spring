package org.novi.core;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "flag")
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;

    @Transient
    private Boolean status;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<ActivationConfig> activationConfigs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ActivationConfig> getActivationConfigs() {
        return activationConfigs;
    }

    public void setActivationConfigs(Set<ActivationConfig> activationConfigs) {
        this.activationConfigs = activationConfigs;
    }

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}