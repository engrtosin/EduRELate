package com.codepath.edurelate.models;

import com.parse.ParseFile;

public class SearchResult {

    public static final String TAG = "SearchResult";
    public static final int HEADING = 200;
    public static final int RESULT_ITEM = 100;

    private Group group;
    private String title;
    private String latestMsg;
    private ParseFile pic;
    private String resultNumTxt;

    public SearchResult(Group group, String title, String latestMsg, ParseFile pic) {
        this.group = group;
        this.title = title;
        this.latestMsg = latestMsg;
        this.pic = pic;
    }

    public SearchResult(String title,int resultNum) {
        this.title = title;
        if (resultNum < 1) {
            this.resultNumTxt = "(No result)";
            return;
        }
        if (resultNum > 1) {
            this.resultNumTxt = "1 result)";
            return;
        }
        this.resultNumTxt = "(" + resultNum + " results)";
    }

    public Group getGroup() {
        return group;
    }

    public String getTitle() {
        return title;
    }

    public String getLatestMsg() {
        return latestMsg;
    }

    public ParseFile getPic() {
        return pic;
    }

    public String getResultNumTxt() {
        return resultNumTxt;
    }

    public void setPic(ParseFile pic) {
        this.pic = pic;
    }

    public void setLatestMsg(String latestMsg) {
        this.latestMsg = latestMsg;
    }

    public void setResultNumTxt(int resultNum) {
        if (resultNum < 1) {
            this.resultNumTxt = "(No result)";
            return;
        }
        if (resultNum > 1) {
            this.resultNumTxt = "1 result)";
            return;
        }
        this.resultNumTxt = "(" + resultNum + " results)";
    }
}
