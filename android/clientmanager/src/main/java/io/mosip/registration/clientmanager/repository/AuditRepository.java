package io.mosip.registration.clientmanager.repository;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.AuditDao;
import io.mosip.registration.clientmanager.entity.Audit;

/**
 *
 * @author Anshul Vanawat
 * @since 1.0.0
 */

public class AuditRepository {

    private AuditDao auditDao;

    @Inject
    public AuditRepository(AuditDao auditDao) {
        this.auditDao = auditDao;
    }

    public List<Audit> getAuditsFromDate(long fromDateTime) {
        return this.auditDao.getAll(fromDateTime);
    }

    public void insertAudit(Audit audit) {
        this.auditDao.insert(audit);
    }

    public void deleteAllAuditsTillDate(long tillDateTime) {
        auditDao.deleteAll(tillDateTime);
    }
}
