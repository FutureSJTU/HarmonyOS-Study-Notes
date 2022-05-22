package com.liangzili.future_bilibilicard.widget.slideshow;

import java.util.List;

public class SlideShowEntity {
    public static class Result{

        //标题
        private String title;
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        private String img;
        public String getImg() {
            return img;
        }
        public void setImg(String img) {
            //获取到的为http请求，转换为加密请求方式，解决图片无法访问的问题 2022.01.19
            this.img = img.replaceFirst("http:","https:");
        }


    }
    private List<Result> result;
    public List<Result> getResult() {
        return result;
    }
    public void setResult(List<Result> result) {
        this.result = result;
    }
}
