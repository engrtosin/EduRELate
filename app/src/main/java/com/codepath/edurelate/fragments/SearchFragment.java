package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.SearchAdapter;
import com.codepath.edurelate.databinding.FragmentSearchBinding;
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
import java.util.HashMap;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    FragmentSearchBinding binding;
    View rootView;
    SearchFragInterface mListener;
    String queryTxt;
    List<Member> members;
    List<Group> groups;
    List<SearchResult> results;
    List<SearchResult> oldResults;
    SearchResult chatsHeader;
    SearchResult messagesHeader;
    SearchAdapter adapter;
    SearchAdapter oldAdapter;
    LinearLayoutManager llManager;

    /* ------------------- INTERFACE -------------------------- */
    public interface SearchFragInterface {
        void fragmentClosed();
    }

    public void setFragListener(SearchFragInterface listener) {
        mListener = listener;
    }

    /* ---------------- CONSTRUCTOR -------------------------- */
    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(List<Member> members) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putParcelable(Member.KEY_MEMBER, Parcels.wrap(members));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryTxt = "";
        if (getArguments() != null) {
            members = Parcels.unwrap(getArguments().getParcelable(Member.KEY_MEMBER));
            groups = Member.getGroups(members);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatsHeader = new SearchResult("Chats",0);
        messagesHeader = new SearchResult("Messages",0);
        results = new ArrayList<>();
        adapter = new SearchAdapter(getContext(),results,"");
        binding.rvSearchItems.setAdapter(adapter);
        llManager = new LinearLayoutManager(getContext());
        binding.rvSearchItems.setLayoutManager(llManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rvSearchItems.addItemDecoration(itemDecoration);
        setListeners();
    }

    private void setListeners() {
        binding.etSearchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (results.size() != 0) {
                    Log.i(TAG,"text has changed to " + s);
//                    textChanged(s.toString());
                }
            }
        });
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
            oldAdapter = adapter;
        }
        List<SearchResult> newResults = new ArrayList<>();
        SearchAdapter newAdapter = new SearchAdapter(getContext(),newResults,newQueryTxt);
        getNewResults(newResults,newAdapter,newQueryTxt);
        binding.rvSearchItems.setAdapter(newAdapter);
        oldAdapter = newAdapter;
    }

    private void getNewResults(List<SearchResult> newResults, SearchAdapter newAdapter, String newQuery) {
        newResults.add(chatsHeader);
        int oldMsgPos = oldAdapter.getMsgHeaderPos();
        for (int i = 1; i < oldMsgPos; i++) {
            if (oldResults.get(i).getTitle().toLowerCase().contains(newQuery.toLowerCase())) {
                newResults.add(oldResults.get(i));
            }
        }
        newAdapter.setMsgHeaderPos(newResults.size());
        for (int i = oldMsgPos+1; i < oldResults.size(); i++) {
            if (oldResults.get(i).getTitle().toLowerCase().contains(newQuery.toLowerCase())) {
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
            queryChats();
            queryMessages();
        }
    }

    private void queryChats() {
        results.add(chatsHeader);
        for (int i = 0; i < members.size(); i++) {
            Group group = members.get(i).getGroup();
            if (group.getGroupName().toLowerCase().contains(queryTxt.toLowerCase())) {
                Log.i(TAG,"Chat: " + group.getGroupName());
                SearchResult result = new SearchResult(group,group.getGroupName(),null,group.getGroupPic());
                Message latestMsg = group.getLatestMsg();
                if (latestMsg != null) {
                    result.setLatestMsg(latestMsg.getBody(true));
                }
                results.add(result);
                continue;
            }
            if (group.getIsFriendGroup()) {
                ParseUser friend =  members.get(i).getFriend();
                if (User.getFullName(friend).toLowerCase().contains(queryTxt.toLowerCase())) {
                    Log.i(TAG,"Chat: " + User.getFullName(friend));
                    SearchResult result = new SearchResult(group,User.getFullName(friend),null,User.getUserPic(friend));
                    Message latestMsg = group.getLatestMsg();
                    if (latestMsg != null) {
                        result.setLatestMsg(latestMsg.getBody(true));
                    }
                    results.add(result);
                }
            }
        }
        chatsHeader.setResultNumTxt(results.size()-1);
        adapter.setMsgHeaderPos(results.size());
        adapter.notifyDataSetChanged();
    }

    private void queryMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_GROUP);
        query.orderByDescending(Message.KEY_CREATED_AT);
        query.whereContainedIn(Message.KEY_GROUP, groups);
        query.whereMatches(Message.KEY_BODY,"("+queryTxt+")", "i");
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while querying members: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Message query successful");
                createResults(objects);
            }
        });
    }

    private void createResults(List<Message> messages) {
        results.add(messagesHeader);
        for (int i = 0; i < messages.size(); i++) {
            Log.i(TAG,i + ": Message: " + messages.get(i).getBody(true));
            Message message = messages.get(i);
            Group group = message.getGroup();
            SearchResult result = new SearchResult(group,group.getGroupName(),message.getBody(true),group.getGroupPic());
            if (group.getIsFriendGroup()) {
                updateResultPic(group,result);
            }
            results.add(result);
        }
        messagesHeader.setResultNumTxt(messages.size());
        adapter.notifyDataSetChanged();
    }

    private void updateResultPic(Group group, SearchResult result) {
        ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
        query.include(Member.KEY_FRIEND);
        query.whereEqualTo(Member.KEY_USER,ParseUser.getCurrentUser());
        query.whereContainedIn(Member.KEY_GROUP, Arrays.asList(group));
        query.findInBackground(new FindCallback<Member>() {
            @Override
            public void done(List<Member> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                if (objects.size() > 0) {
                    result.setPic(User.getUserPic(objects.get(0).getFriend()));
                }
            }
        });
    }

    private void clearAllData() {
    }
}