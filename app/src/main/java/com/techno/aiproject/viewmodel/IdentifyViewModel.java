package com.techno.aiproject.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.techno.aiproject.database.PlantHistory;
import com.techno.aiproject.repository.PlantRepository;

public class IdentifyViewModel extends AndroidViewModel {
    private final PlantRepository repository;
    
    private final MutableLiveData<String> progressState = new MutableLiveData<>();
    private final MutableLiveData<PlantHistory> identifySuccess = new MutableLiveData<>();
    private final MutableLiveData<String> identifyError = new MutableLiveData<>();

    public IdentifyViewModel(@NonNull Application application) {
        super(application);
        repository = new PlantRepository(application);
    }

    public LiveData<String> getProgressState() {
        return progressState;
    }

    public LiveData<PlantHistory> getIdentifySuccess() {
        return identifySuccess;
    }

    public LiveData<String> getIdentifyError() {
        return identifyError;
    }

    public void startIdentification(String imagePath, String language) {
        progressState.setValue("Initializing analysis...");
        identifySuccess.setValue(null);
        identifyError.setValue(null);

        repository.identifyAndAnalyze(imagePath, language, new PlantRepository.PipelineCallback() {
            @Override
            public void onSuccess(PlantHistory plantHistory) {
                identifySuccess.setValue(plantHistory);
                progressState.setValue(null);
            }

            @Override
            public void onError(String message) {
                identifyError.setValue(message);
                progressState.setValue(null);
            }

            @Override
            public void onProgress(String status) {
                progressState.setValue(status);
            }
        });
    }
}
