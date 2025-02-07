package com.shopme.common.entity;

import java.util.Objects;

import com.shopme.common.enums.ERole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "erole", length = 40, nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private ERole eRole;

    @Column(length = 150)
    private String description;

    public Role(ERole eRole) {
        super();
        this.eRole = eRole;
    }

    public Role(Long id) {
        this.id = id;
    }

    public Role(ERole eRole, String description) {
        super();
        this.eRole = eRole;
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Role other = (Role) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return this.eRole.name();
    }
}
