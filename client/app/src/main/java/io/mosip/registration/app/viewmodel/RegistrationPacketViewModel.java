package io.mosip.registration.app.viewmodel;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.app.viewmodel.model.RegistrationPacketModel;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.util.DateUtil;

public class RegistrationPacketViewModel {

    private static final String TAG = RegistrationPacketViewModel.class.getSimpleName();

    private PacketService packetService;
    private DateUtil dateUtil;

    public RegistrationPacketViewModel(PacketService packetService, DateUtil dateUtil) {
        this.packetService = packetService;
        this.dateUtil = dateUtil;
    }

    private List<RegistrationPacketModel> registrationList;

    public List<RegistrationPacketModel> getList() {
        if (registrationList == null) {
            registrationList = new ArrayList<>();
            loadRegistrations();
        }
        return registrationList;
    }

    private void loadRegistrations() {
        List<Registration> registrations = this.packetService.getAllRegistrations(0, 0);

        List<RegistrationPacketModel> registrationPacketModels = new ArrayList<>();

        for (Registration registration : registrations) {
            String packetStatus = registration.getServerStatus() == null ? registration.getClientStatus() : registration.getServerStatus();
            String createdDate = dateUtil.getDateTime(registration.getCrDtime());
            registrationPacketModels.add(new RegistrationPacketModel(
                    registration.getPacketId(),
                    packetStatus,
                    createdDate
            ));
        }

        registrationList = registrationPacketModels;
    }

    public void refreshPacketStatus() {
        for (RegistrationPacketModel packet : registrationList) {
            packet.setPacketStatus(packetService.getPacketStatus(packet.getPacketId()));
        }
    }
}
