package io.mosip.registration.app.viewmodel;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface IListingViewModel<T> {
    LiveData<List<T>> getList();
}
