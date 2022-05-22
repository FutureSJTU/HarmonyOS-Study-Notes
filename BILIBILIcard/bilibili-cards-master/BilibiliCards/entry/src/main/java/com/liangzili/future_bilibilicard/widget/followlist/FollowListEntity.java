package com.liangzili.future_bilibilicard.widget.followlist;

import java.util.Date;
import java.util.List;

public class FollowListEntity {
    private int code;
    public int getCode() {return code;}
    public void setCode(int code) {this.code = code;}

    private String message;
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}

    public static class Data{

        public static class list{
            //番剧类型
            private String season_type_name;
            public String getSeason_type_name() {
                return season_type_name;
            }
            public void setSeason_type_name(String season_type_name) { this.season_type_name = season_type_name; }
            //标题
            private String title;
            public String getTitle() {
                return title;
            }
            public void setTitle(String title) {
                this.title = title;
            }
            //封面(附加时间戳)
            private String cover;
            public String getCover() {return cover; }
            public void setCover(String cover) {
                //获取到的为http请求，转换为加密请求方式，解决图片无法访问的问题 2022.01.19
                this.cover = cover.replaceFirst("http:","https:") +"?timestamp="+new Date().getTime();
            }
            //标记
            private String badge;
            public String getBadge() { return badge; }
            public void setBadge(String badge) { this.badge = badge; }
            //标记样式信息
            public static class Badge_info{
                // 标记背景色
                private String bg_color;
                public String getBg_color() { return bg_color; }
                public void setBg_color(String bg_color) { this.bg_color = bg_color; }
            };
            private Badge_info badge_info;
            public Badge_info getBadge_info() { return badge_info; }
            public void setBadge_info(Badge_info badge_info) { this.badge_info = badge_info; }
            //更新进度
            public static class New_ep{
                //更新状态
                private String index_show;
                public String getIndex_show() {
                    return index_show;
                }
                public void setIndex_show(String index_show) {
                    this.index_show = index_show;
                }
            }
            private New_ep new_ep;
            public New_ep getNew_ep() {
                return new_ep;
            }
            public void setNew_ep(New_ep new_ep) {
                this.new_ep = new_ep;
            }
            //播放地址
            private String url;
            public String getUrl() {
                return url;
            }
            public void setUrl(String url) {
                this.url = url;
            }
            //更新时间
            private String renewal_time;
            public String getRenewal_time() {
                return renewal_time;
            }
            public void setRenewal_time(String renewal_time) {
                this.renewal_time = renewal_time;
            }
            //播放短链
            private String short_url;
            public String getShort_url() { return short_url; }
            public void setShort_url(String short_url) { this.short_url = short_url; }
            //观看进度
            private String progress;
            public String getProgress() {return progress; }
            public void setProgress(String progress) { this.progress = progress; }
            //追番状态(根据追番状态，设置progress的color)
            private String follow_status;
            public String getFollow_status() {
                String color;
                if(follow_status.equals("2")){
                    color = "#FB7299";
                }else {
                    color = "#99000000";
                }
                System.out.println(color);
                return color;
            }
            public void setFollow_status(String follow_status) {
                this.follow_status = follow_status;
            }
        }
        private List<list> list;
        public List<list> getList() {
            return list;
        }
        public void setList(List<list> list) {
            this.list = list;
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
