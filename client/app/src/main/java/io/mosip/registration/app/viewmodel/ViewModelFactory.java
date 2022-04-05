package io.mosip.registration.app.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private ListingViewModel listingViewModel;

    public ViewModelFactory(ListingViewModel listingViewModel) {
        this.listingViewModel = listingViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass == ListingViewModel.class){
            return (T) this.listingViewModel;
        }else{
            throw new IllegalStateException("Unknown entity");
        }
    }
}
