package com.liangzili.future_bilibilicard.widget.recommend;

import java.util.Date;
import java.util.List;

public class RecommendEntity {
    private int code;
    public int getCode() {return code;}
    public void setCode(int code) {this.code = code;}

    private String message;
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}

    public static class Data{
        public static class Item{
            private String uri;
            public String getUri() {return uri;}
            public void setUri(String uri) {this.uri = uri;}

            private String pic;
            public String getPic() {
                return pic+"?timestamp="+new Date().getTime();
            }
            public void setPic(String pic) {
                //获取到的为http请求，转换为加密请求方式，解决图片无法访问的问题  2022.01.19
                this.pic = pic.replaceFirst("http:","https:") +"?timestamp="+new Date().getTime();
            }

            //标题
            private String title;
            public String getTitle() {
                return title;
            }
            public void setTitle(String title) {
                this.title = title;
            }

            private static class Owner{
                private String name;
                public String getName() {
                    return name;
                }
                public void setName(String name) {
                    this.name = name;
                }

            }
            private Owner owner;
            public Owner getOwner() {
                return owner;
            }
            public void setOwner(Owner owner) {
                this.owner = owner;
            }
        }
        private List<Item> item;
        public List<Item> getItem() {
            return item;
        }
        public void setItem(List<Item> item) {
            this.item = item;
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
