package com.codepath.edurelate.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.edurelate.adapters.SearchChatsAdapter;
import com.codepath.edurelate.adapters.SearchGroupsAdapter;
import com.codepath.edurelate.databinding.FragmentSearchGroupsBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.Message;
import com.codepath.edurelate.models.SearchResult;
import com.codepath.edurelate.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchGroupsFragment extends Fragment {

    public static final String TAG = "SearchGroupsFragment";

    FragmentSearchGroupsBinding binding;
    View rootView;
    SearchFragInterface mListener;
    String queryTxt;
    List<Member> members;
    List<Group> groups;
    List<SearchResult> results;
    List<SearchResult> oldResults;
    SearchGroupsAdapter adapter;
    LinearLayoutManager llManager;

    /* ------------------- INTERFACE -------------------------- */
    public interface SearchFragInterface {
        void fragmentClosed();
    }

    public void setFragListener(SearchFragInterface listener) {
        mListener = listener;
    }

    /* ---------------- CONSTRUCTOR -------------------------- */
    public SearchGroupsFragment() {
        // Required empty public constructor
    }

    public static SearchGroupsFragment newInstance(List<Group> groups) {
        SearchGroupsFragment fragment = new SearchGroupsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Group.KEY_GROUP,Parcels.wrap(groups));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryTxt = "";
        if (getArguments() != null) {
            groups = Parcels.unwrap(getArguments().getParcelable(Group.KEY_GROUP));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchGroupsBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        results = new ArrayList<>();
        adapter = new SearchGroupsAdapter(getContext(),results,"");
        binding.rvSearchItems.setAdapter(adapter);
        llManager = new LinearLayoutManager(getContext());
        binding.rvSearchItems.setLayoutManager(llManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rvSearchItems.addItemDecoration(itemDecoration);
        setListeners();
    }

    private void setListeners() {
        binding.etSearchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    startSearch();
                    return true;
                }
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    startSearch();
                    return true;
                }
                return false;
            }
        });
        binding.ivCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.fragmentClosed();
                clearAllData();
            }
        });
        binding.ivSearchGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
    }

    private void textChanged(String newQueryTxt) {
        if (oldResults == null) {
            oldResults = new ArrayList<>(results);
        }
        List<SearchResult> newResults = new ArrayList<>();
        SearchChatsAdapter newAdapter = new SearchChatsAdapter(getContext(),newResults,newQueryTxt);
        getNewResults(newResults,newAdapter,newQueryTxt);
        binding.rvSearchItems.setAdapter(newAdapter);
    }

    private void getNewResults(List<SearchResult> newResults, SearchChatsAdapter newAdapter, String newQuery) {
        int oldMsgPos = 0;
        for (int i = 1; i < oldMsgPos; i++) {
            if (oldResults.get(i).getTitle().toLowerCase().contains(newQuery.toLowerCase())) {
                newResults.add(oldResults.get(i));
            }
        }
        for (int i = oldMsgPos+1; i < oldResults.size(); i++) {
            if (oldResults.get(i).getLatestMsg().toLowerCase().contains(newQuery.toLowerCase())) {
                newResults.add(oldResults.get(i));
            }
        }
        newAdapter.notifyDataSetChanged();
    }

    private void startSearch() {
        results.clear();
        queryTxt = binding.etSearchTxt.getText().toString();
        queryTxt = queryTxt.trim();
        binding.etSearchTxt.setText(queryTxt);
        if (!queryTxt.isEmpty()) {
            adapter.setQueryTxt(queryTxt);
            createResults();
        }
    }

    private void queryGroups() {

    }

    private void createResults() {
        for (int i = 0; i < groups.size(); i++) {
            Log.i(TAG,i + ": Group: " + groups.get(i).getGroupName());
            Group group = groups.get(i);
            String groupName = group.getGroupName();
            if (groupName.toLowerCase().contains(queryTxt.toLowerCase())) {
                SearchResult result = new SearchResult(group);
                results.add(result);
                adapter.notifyItemChanged(results.size()-1);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void clearAllData() {
    }
}