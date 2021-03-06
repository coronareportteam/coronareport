package quarano.sormas_integration.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;
import quarano.tracking.TrackedPerson;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Federico Grasso
 *
 * Update listener
 */

@Slf4j
@RequiredArgsConstructor
@Component
class UpdateListener implements PostUpdateEventListener {

    @Override
    public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
        if(postUpdateEvent.getEntity() instanceof TrackedPerson){

            log.debug("Trigger invoked...");

            postUpdateEvent.getSession().createNativeQuery(
                    "INSERT INTO index_synch_backlog (id, sync_date) " +
                            "VALUES (:id, :sync_date)")
                    .setParameter("id", ((TrackedPerson) postUpdateEvent.getEntity())
                            .getId()
                            .toString())
                    .setParameter("sync_date", LocalDateTime.now())
                    .setFlushMode(FlushMode.MANUAL)
                    .executeUpdate();

            postUpdateEvent.getSession().createNativeQuery(
                    "INSERT INTO contacts_synch_backlog (id, sync_date) " +
                            "VALUES (:id, :sync_date)")
                    .setParameter("id", ((TrackedPerson) postUpdateEvent.getEntity())
                            .getId()
                            .toString())
                    .setParameter("sync_date", LocalDateTime.now())
                    .setFlushMode(FlushMode.MANUAL)
                    .executeUpdate();
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }
}