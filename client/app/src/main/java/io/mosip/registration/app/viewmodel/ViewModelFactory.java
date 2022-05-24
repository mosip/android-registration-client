package io.mosip.registration.app.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory<T> implements ViewModelProvider.Factory {

    private T viewModel;

    public ViewModelFactory(T viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(IListingViewModel.class.isAssignableFrom(modelClass)){
            return (T) this.viewModel;
        }else{
            throw new IllegalStateException("Unknown entity");
        }
    }
}
