package tz.go.mof.trab.models;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import tz.go.mof.trab.utils.CustomGeneratedData;



@Audited
@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
public class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @Audited
    private String id = CustomGeneratedData.GenerateUniqueID();

    @Basic(optional = false)
    @Column(name = "created_at", updatable = false)
    @Audited
    private LocalDateTime createdAt = LocalDateTime.now();

    @Basic(optional = false)
    @Column(name = "updated_at")
    @Audited
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at", nullable = true)
    @Audited
    private LocalDateTime deletedAt;

    @Column(name = "created_by", nullable = true)
    @Audited
    private String createdBy;

    @Column(name = "updated_by",nullable = true)
    @Audited
    private String updatedBy;

    @Basic(optional = false)
    @Column(name = "deleted")
    @Audited
    private Boolean deleted = false;

    @Column(name = "deleted_by",nullable = true)
    @Audited
    private String deletedBy;

    @Basic(optional = false)
    @Column(name = "active")
    @Audited
    private Boolean active = true;

    @Basic(optional = true)
    @Column(name = "action")
    @Audited
    private String action;


}
