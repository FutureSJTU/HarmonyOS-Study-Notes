package com.liangzili.future_bilibilicard.widget.fans;

public class Myinfo {
    public class Data{
        private String face;
        public String getFace() {
            return face;
        }
        public void setFace(String face) {
            //获取到的为http请求，转换为加密请求方式，解决图片无法访问的问题 2022.01.19
            this.face = face.replaceFirst("http:","https:");
        }

        public class Vip{
            private String avatar_subscript_url;
            public String getAvatar_subscript_url() {
                return avatar_subscript_url;
            }
            public void setAvatar_subscript_url(String avatar_subscript_url) {
                //获取到的为http请求，转换为加密请求方式，解决图片无法访问的问题 2022.01.19
                this.avatar_subscript_url = avatar_subscript_url.replaceFirst("http:","https:");
            }
        }
        private Vip vip;
        public Vip getVip() {
            return vip;
        }
        public void setVip(Vip vip) {
            this.vip = vip;
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
