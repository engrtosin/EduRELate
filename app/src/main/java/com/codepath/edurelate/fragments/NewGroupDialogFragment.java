package com.codepath.edurelate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.FragmentNewGroupBinding;
import com.codepath.edurelate.models.Category;
import com.codepath.edurelate.models.Group;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.hootsuite.nachos.validator.ChipifyingNachoValidator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NewGroupDialogFragment extends DialogFragment  implements TextView.OnEditorActionListener {

    public static final String TAG = "NewGroupDialogFragment";

    FragmentNewGroupBinding binding;
    String[] suggestions = new String[]{};
    HashMap<String, Category> categoryMap;
    ArrayAdapter<String> adapter;

    /* ---------------------- interface ---------------------- */

    public NewGroupDialogFragment() {
    }

    public static NewGroupDialogFragment newInstance(String title) {
        NewGroupDialogFragment frag = new NewGroupDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewGroupBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        categoryMap = new HashMap<>();
        queryAllCategories();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title", "Enter Name");
        // Show soft keyboard automatically and request focus to field
        binding.etGroupName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setOnClickListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void setOnClickListeners() {
        binding.swGroupAccess.setChecked(false);
        binding.ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createNewGroup();
                } catch (ParseException e) {
                    Log.e(TAG,"Error while creating a new group: " + e.getMessage(),e);
                }
            }
        });
    }

    private void queryAllCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while querying for categories: " + e.getMessage(),e);
                    return;
                }
                suggestions = new String[objects.size()];
                Log.i(TAG,"Categories queried successfully. Size: " + objects.size());
                for (int i = 0; i < objects.size(); i++) {
                    suggestions[i] = objects.get(i).getTitle();
                    categoryMap.put(suggestions[i],objects.get(i));
                    Log.i(TAG,"Suggestion at " + i + ": " + suggestions[i]);
                }
                setupAdapter();
            }
        });
    }

    private void setupAdapter() {
        adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item,suggestions);
        binding.ntvCategories.setAdapter(adapter);
        binding.ntvCategories.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
        binding.ntvCategories.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
        binding.ntvCategories.setNachoValidator(new ChipifyingNachoValidator());
    }

    protected void createNewGroup() throws ParseException {
        String groupName = binding.etGroupName.getText().toString();
        if (groupName == null) {
            Toast.makeText(getContext(),"Group name cannot be empty.",Toast.LENGTH_SHORT).show();
            return;
        }
        List<Category> groupCategories = new ArrayList<>();
        List<String> suggestionList = Arrays.asList(suggestions);
        List<Chip> chips = binding.ntvCategories.getAllChips();
        for (int i = 0; i < chips.size(); i++) {
            String chipText = chips.get(i).getText().toString();
            if (categoryMap.containsKey(chipText)) {
                groupCategories.add(categoryMap.get(chipText));
            }
        }
        int groupAccess = Group.OPEN_GROUP_CODE;
        if (binding.swGroupAccess.isChecked()) {
            groupAccess = Group.CLOSED_GROUP_CODE;
        }
        Group.newNonFriendGroup(groupName,groupAccess,groupCategories);
        dismiss();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }
}