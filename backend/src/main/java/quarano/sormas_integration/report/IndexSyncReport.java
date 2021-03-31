package quarano.sormas_integration.report;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Federico Grasso
 *
 * Index Synchronization Report Table
 */

@Entity
@Table(name = "index_sync_report")
@Setter(AccessLevel.PUBLIC)
@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class IndexSyncReport {
    @Id
    @NonNull
    private UUID uuid;

    @NonNull
    private int personsNumber;

    @NonNull
    private int casesNumber;

    @NonNull
    private LocalDateTime syncDate;

    @NonNull
    private int syncTime;

    @NonNull
    private ReportStatus status;

    public enum ReportStatus {
        STARTED,
        FAILED,
        SUCCESS
    }
}
