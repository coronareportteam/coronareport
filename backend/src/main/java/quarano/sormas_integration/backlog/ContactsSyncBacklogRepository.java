package quarano.sormas_integration.backlog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * @author Federico Grasso
 *
 * Contacts Backlog Table repository
 */
public interface ContactsSyncBacklogRepository extends JpaRepository<ContactsSynchBacklog, Long> {
    // Returns all modified or inserted records after specified date
    @Query("select distinct b.id from ContactsSynchBacklog b where b.syncDate <= :syncDate")
    ArrayList<UUID> findBySyncDate(Date syncDate);

    // Delete all entries related to specified UUID after specified date
    @Transactional
    @Modifying
    @Query("delete from ContactsSynchBacklog b where b.syncDate <= :syncDate and b.id = :uuid")
    void deleteAfterSynchronization(UUID uuid, Date syncDate);
}