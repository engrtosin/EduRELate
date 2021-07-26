package com.codepath.edurelate.models;

import android.util.Log;

import com.codepath.edurelate.R;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Parcel(analyze = Request.class)
@ParseClassName("Request")
public class Request extends ParseObject {

    public static final String TAG = "RequestModel";
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_TO_GROUP = "toGroup";

    /* ------------------------ NEW REQUEST METHODS ------------------------ */
    public static Request newRequest(ParseUser creator, Group toGroup) {
        Request request = new Request();
        request.put(KEY_CREATOR,creator);
        request.put(KEY_TO_GROUP,toGroup);
        request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while saving request: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Request successfully saved");
            }
        });
        return request;
    }

    /* ------------------------ GET METHODS ------------------------ */
    public Group getToGroup() {
        return (Group) getParseObject(KEY_TO_GROUP);
    }

    public static List<String> getGroupIds(List<Request> requests) {
        List<String> groupIds = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            String groupId = requests.get(i).getToGroup().getObjectId();
            groupIds.add(groupId);
        }
        return groupIds;
    }

    public ParseUser getCreator() {
        return getParseUser(KEY_CREATOR);
    }
}
