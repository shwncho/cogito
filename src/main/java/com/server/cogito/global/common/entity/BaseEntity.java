package com.server.cogito.global.common.entity;

import com.server.cogito.global.common.exception.ApplicationException;
import com.server.cogito.global.common.exception.StatusErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE, INACTIVE, INVALID;

        public static BaseEntity.Status getStatus(String status) throws ApplicationException {
            if (StringUtils.hasText(status)) {
                throw new ApplicationException(StatusErrorCode.EMPTY_STATUS);
            }
            try {
                return BaseEntity.Status.valueOf(status);
            } catch (ApplicationException ignored) {
                throw new ApplicationException(StatusErrorCode.INVALID_STATUS);
            }
        }
    }
}
