package com.liangzili.future_bilibilicard.widget.pandect;

import java.util.List;

public class PandectEntity {

    private int code;
    private String message;
    private int ttl;
    private List<Data> data;
    public class Data {

        private long date_key;
        private int total_inc;
        public void setDate_key(long date_key) {
            this.date_key = date_key;
        }
        public long getDate_key() {
            return date_key;
        }

        public void setTotal_inc(int total_inc) {
            this.total_inc = total_inc;
        }
        public int getTotal_inc() {
            return total_inc;
        }

    }
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
    public int getTtl() {
        return ttl;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }

}
