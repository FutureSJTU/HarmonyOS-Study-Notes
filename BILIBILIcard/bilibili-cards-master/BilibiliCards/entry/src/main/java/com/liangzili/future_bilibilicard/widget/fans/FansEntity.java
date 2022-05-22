package com.liangzili.future_bilibilicard.widget.fans;

public class FansEntity {
    private int code;
    public int getCode() {return code;}
    public void setCode(int code) {this.code = code;}

    private String message;
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}


    public static class Data{
        private int follower;
        public int getFollower() {
            return follower;
        }
        public void setFollower(int follower) {
            this.follower = follower;
        }
    }
    private Data data;
    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }
}
