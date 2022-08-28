package com.example.dogsapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.dogsapp.model.DogBreed;
import com.example.dogsapp.model.DogsApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListViewModel extends AndroidViewModel {

    public MutableLiveData<List<DogBreed>> dogs=new MutableLiveData<>();
    public MutableLiveData<Boolean> dogLoadError=new MutableLiveData<>();
    public MutableLiveData<Boolean> loading=new MutableLiveData<>();

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    private DogsApiService dogsApiService=new DogsApiService();
    private CompositeDisposable disposable=new CompositeDisposable();


    public void refresh(){
        fetchFromRemote();


    }
    private void fetchFromRemote(){
        loading.setValue(true);
        disposable.add(
        dogsApiService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DogBreed>>() {
                    @Override
                    public void onSuccess(List<DogBreed> dogBreeds) {
                        dogs.setValue(dogBreeds);
                        dogLoadError.setValue(false);
                        loading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        dogLoadError.setValue(true);
                        loading.setValue(false);
                        e.printStackTrace();

                    }
                })
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
