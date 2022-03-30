package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Base class for {@link Audit} with {@link #uuid} and {@link #createdAt}
 *
 * @author Anshul vanawat
 */
//@MappedSuperclass
@Data
@AllArgsConstructor
public class BaseAudit {

    /**
     * Field for immutable universally unique identifier (UUID)
     */
    @PrimaryKey
    @ColumnInfo(name = "log_id")
    @NotNull
    //@Updateable = false
    private String uuid;

    @ColumnInfo(name = "log_dtimes")
    @NotNull
    //@Updateable = false
    private LocalDateTime createdAt;

    /**
     * Constructor to initialize {@link BaseAudit} with uuid and timestamp
     */
    public BaseAudit() {
        uuid = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }
}