package io.mosip.registration.clientmanager.repository;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.clientmanager.dao.IdentitySchemaDao;
import io.mosip.registration.clientmanager.dao.ProcessSpecDao;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.entity.ProcessSpec;

public class ProcessSpecRepository {
    private static final String TAG = ProcessSpecRepository.class.getSimpleName();
    private ProcessSpecDao processSpecDao;

    List<ProcessSpec> findAllByIdVersionAndIsActiveTrueOrderByOrderNumAsc(double idVersion) {
        List<ProcessSpec> processSpecList = processSpecDao.getAllProcessSpec(idVersion);
        return processSpecList;
    }

    ProcessSpec findByIdAndIdVersionAndIsActiveTrue(String processId, double idVersion) {
        ProcessSpec processSpec = processSpecDao.findByIdAndIdVersion(processId, idVersion);

        return processSpec;
    }
}
