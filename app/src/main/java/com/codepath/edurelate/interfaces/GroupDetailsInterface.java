package com.codepath.edurelate.interfaces;

import com.codepath.edurelate.models.Member;
import com.parse.ParseUser;

public interface GroupDetailsInterface {
    void leave();
    void inviteUser();
    void chatWithUser(ParseUser user);
    void goProfile(ParseUser user);
}
