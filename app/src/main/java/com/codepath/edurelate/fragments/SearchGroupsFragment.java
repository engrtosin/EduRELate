package com.codepath.edurelate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.edurelate.R;
import com.codepath.edurelate.activities.AllGroupsActivity;
import com.codepath.edurelate.adapters.SearchGroupsAdapter;
import com.codepath.edurelate.databinding.FragmentSearchGroupsBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.SearchResult;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchGroupsFragment extends Fragment {

    public static final String TAG = "SearchGroupsFragment";

    FragmentSearchGroupsBinding binding;
    View rootView;
    SearchFragInterface mListener;
    String queryTxt;
    List<Member> members;
    List<Group> groupsByName;
    List<Group> groupsByRank;
    List<SearchResult> resultsByName;
    List<SearchResult> resultsByRank;
    SearchGroupsAdapter groupsAdapter;
    SearchGroupsAdapter byRankAdapter;
    LinearLayoutManager llManager;
    ArrayAdapter<String> sortAdapter;
    String[] sortOptions = new String[]{"Name (A-Z)","Name (Z-A)", "Recommended"};
    boolean groupsIsReversed = false;

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

    public static SearchGroupsFragment newInstance(List<Group> groupsByName,List<Group> groupsByRank) {
        SearchGroupsFragment fragment = new SearchGroupsFragment();
        Bundle args = new Bundle();
        args.putParcelable(AllGroupsActivity.GROUPS_BY_NAME,Parcels.wrap(groupsByName));
        args.putParcelable(AllGroupsActivity.GROUPS_BY_RANK,Parcels.wrap(groupsByRank));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryTxt = "";
        if (getArguments() != null) {
            groupsByName = Parcels.unwrap(getArguments().getParcelable(AllGroupsActivity.GROUPS_BY_NAME));
            groupsByRank = Parcels.unwrap(getArguments().getParcelable(AllGroupsActivity.GROUPS_BY_RANK));
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
        resultsByName = new ArrayList<>();
        resultsByRank = new ArrayList<>();
        groupsAdapter = new SearchGroupsAdapter(getContext(), resultsByName,"");
        byRankAdapter = new SearchGroupsAdapter(getContext(), resultsByRank,"");
        binding.rvSearchItems.setAdapter(groupsAdapter);
        llManager = new LinearLayoutManager(getContext());
        binding.rvSearchItems.setLayoutManager(llManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rvSearchItems.addItemDecoration(itemDecoration);
        setupSortSpinner();
        setListeners();
    }

    /* ------------------- SPINNERS ------------------ */
    private void setupSortSpinner() {
        sortAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item,sortOptions);
        binding.spSortBy.setAdapter(sortAdapter);
        binding.spSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sortItemSelected(int position) {
        Log.i(TAG,"Sort item selected at " + position);
        Log.i(TAG,"Position is new");
        if (position == 0) {
            binding.rvSearchItems.setAdapter(groupsAdapter);
            if (groupsIsReversed) {
                Log.i(TAG,"Groups is reversed");
                Collections.reverse(groupsByName);
                groupsIsReversed = false;
                groupsAdapter.notifyDataSetChanged();
            }
            return;
        }
        if (position == 1) {
            binding.rvSearchItems.setAdapter(groupsAdapter);
            if (!groupsIsReversed) {
                Log.i(TAG,"Groups is not reversed");
                Collections.reverse(groupsByName);
                groupsIsReversed = true;
                groupsAdapter.notifyDataSetChanged();
            }
            return;
        }
        if (position == 2) {
            binding.rvSearchItems.setAdapter(byRankAdapter);
        }
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

    private void startSearch() {
        resultsByName.clear();
        queryTxt = binding.etSearchTxt.getText().toString();
        queryTxt = queryTxt.trim();
        binding.etSearchTxt.setText(queryTxt);
        if (!queryTxt.isEmpty()) {
            groupsAdapter.setQueryTxt(queryTxt);
            createResults();
        }
    }

    private void createResults() {
        for (int i = 0; i < groupsByName.size(); i++) {
            Log.i(TAG,i + ": Group: " + groupsByName.get(i).getGroupName());
            Group group = groupsByName.get(i);
            String groupName = group.getGroupName();
            if (groupName.toLowerCase().contains(queryTxt.toLowerCase())) {
                SearchResult result = new SearchResult(group);
                resultsByName.add(result);
                groupsAdapter.notifyItemChanged(resultsByName.size()-1);
            }
            group = groupsByRank.get(i);
            groupName = group.getGroupName();
            if (groupName.toLowerCase().contains(queryTxt.toLowerCase())) {
                SearchResult result = new SearchResult(group);
                resultsByRank.add(result);
                byRankAdapter.notifyItemChanged(resultsByRank.size()-1);
            }
        }
        groupsAdapter.notifyDataSetChanged();
    }

    private void clearAllData() {
    }
}