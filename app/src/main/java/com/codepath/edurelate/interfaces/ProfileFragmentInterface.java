package com.codepath.edurelate.interfaces;

import com.codepath.edurelate.models.Member;

public interface ProfileFragmentInterface {
    void logout();
    void goChatActivity(Member member);
    void joinNewGroup();
}
