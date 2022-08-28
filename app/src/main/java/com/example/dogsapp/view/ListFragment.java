package com.example.dogsapp.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dogsapp.R;

import com.example.dogsapp.viewmodel.ListViewModel;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListFragment extends Fragment {

    private ListViewModel viewModel;
    private DogsListAdapter dogsListAdapter=new DogsListAdapter(new ArrayList<>());


    @BindView(R.id.listError)
    TextView listError;

    @BindView(R.id.loadingView)
    ProgressBar loadingView;

    @BindView(R.id.refereshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.dogsList)
    RecyclerView dogsList;

    public ListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewModel= new ViewModelProvider(this).get(ListViewModel.class);
        viewModel.refresh();

        dogsList.setLayoutManager(new LinearLayoutManager(getContext()));
        dogsList.setAdapter(dogsListAdapter);

        refreshLayout.setOnRefreshListener(()->{
            dogsList.setVisibility(View.GONE);
            listError.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
            viewModel.refreshByPassCache();
        });
        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.dogs.observe(getViewLifecycleOwner(), dogs -> {
            if(dogs!=null&&dogs instanceof List){
                dogsList.setVisibility(View.VISIBLE);
                dogsListAdapter.updateDogsList(dogs);
            }
        });
        viewModel.dogLoadError.observe(getViewLifecycleOwner(), isError -> {
            if(isError!=null&&isError instanceof Boolean){
                listError.setVisibility(isError? View.VISIBLE:View.GONE);
            }
        });
        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if(isLoading!=null&&isLoading instanceof Boolean){
                listError.setVisibility(isLoading? View.VISIBLE:View.GONE);
                if (isLoading){
                    listError.setVisibility(View.GONE);
                    dogsList.setVisibility(View.GONE);
                }
            }
        });
    }
}