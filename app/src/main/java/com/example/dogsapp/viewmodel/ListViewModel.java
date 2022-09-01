package com.example.dogsapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.dogsapp.model.DogBreed;
import com.example.dogsapp.model.DogDao;
import com.example.dogsapp.model.DogDatabase;
import com.example.dogsapp.model.DogsApiService;
import com.example.dogsapp.util.NotificationHelper;
import com.example.dogsapp.util.SharedPreferencesHelper;

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
    private AsyncTask<List<DogBreed>,Void,List<DogBreed>> insertTask;
    private AsyncTask<Void,Void,List<DogBreed>> retrieveTask;

    private SharedPreferencesHelper prefHelper=SharedPreferencesHelper.getInstance(getApplication());
    private long refreshTime=5*60*1000*1000*1000L;


    private void fetchFromDatabase(){
        loading.setValue(true);
        retrieveTask=new RetrieveDogsTask();
        retrieveTask.execute();
    }


    public void refresh(){
        long updateTime=prefHelper.getUpdateTime();
        long currentTime=System.nanoTime();
        if(updateTime!=0 && currentTime-updateTime<refreshTime) {
            fetchFromDatabase();
        }
        else {
            fetchFromRemote();
        }


    }
    public void refreshByPassCache(){
        fetchFromRemote();
    }

    private void checkCacheDuration(){
        String cachePreference= prefHelper.getCacheDuration();
        if(!cachePreference.equals("")){
            try{
                int cachePreferencesInt=Integer.parseInt(cachePreference);
                refreshTime=cachePreferencesInt*1000*1000*1000L;
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }

    }

    private void fetchFromRemote(){
        checkCacheDuration();
        loading.setValue(true);
        disposable.add(
        dogsApiService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DogBreed>>() {
                    @Override
                    public void onSuccess(List<DogBreed> dogBreeds) {
                        insertTask=new InsertDogsTask();
                        insertTask.execute(dogBreeds);

                        Toast.makeText(getApplication(),"Dogs Retrived from endpoint",Toast.LENGTH_SHORT).show();
                        NotificationHelper.getInstance(getApplication()).createNotification();
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
        if(insertTask!=null){
            insertTask.cancel(true);
            insertTask=null;
        }
        if(retrieveTask!=null){
            retrieveTask.cancel(true);
            retrieveTask=null;
        }
    }
    private void dogsRetrieved(List<DogBreed> dogList){
        dogs.setValue(dogList);
        dogLoadError.setValue(false);
        loading.setValue(false);
    }
    private class InsertDogsTask extends AsyncTask<List<DogBreed>,Void,List<DogBreed>>{

        @Override
        protected List<DogBreed> doInBackground(List<DogBreed>... lists) {
            List<DogBreed> list=lists[0];
            DogDao dao = DogDatabase.getInstance(getApplication()).dogDao();
            dao.deleteAllDogs();

            ArrayList<DogBreed> newList=new ArrayList<>(list);
            List<Long> result=dao.insertAll(newList.toArray(new DogBreed[0]));

            int i=0;
            while (i<list.size()){
                list.get(i).uuid=result.get(i).intValue();
                i++;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrieved(dogBreeds);
            prefHelper.saveUpdateTime(System.nanoTime());
        }
    }
    private class RetrieveDogsTask extends AsyncTask<Void,Void,List<DogBreed>>{

        @Override
        protected List<DogBreed> doInBackground(Void... voids) {
            return DogDatabase.getInstance(getApplication()).dogDao().getAllDogs();
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrieved(dogBreeds);
            Toast.makeText(getApplication(),"Dogs Retrived from datbase",Toast.LENGTH_SHORT).show();

        }
    }

}
