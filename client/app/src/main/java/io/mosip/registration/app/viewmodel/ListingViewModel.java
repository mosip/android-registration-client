package io.mosip.registration.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.spi.PacketService;

import java.util.List;

public class ListingViewModel extends ViewModel {

    private static final String TAG = ListingViewModel.class.getSimpleName();

    private PacketService packetService;

    public ListingViewModel(PacketService packetService) {
        this.packetService = packetService;
    }

    private MutableLiveData<List<Registration>> registrationList;

    public LiveData<List<Registration>> getRegistrationList() {
        if (registrationList == null) {
            registrationList = new MutableLiveData<>();
            loadRegistrations();
        }
        return registrationList;
    }

    private void loadRegistrations() {
        // do async operation to fetch users
        /*Handler myHandler = new Handler();
        myHandler.postDelayed(() -> {
            registrationList.setValue(this.registrationRepository.getAllRegistrations());
        }, 5000);*/
        registrationList.setValue(this.packetService.getAllRegistrations(0, 0));
    }
}
