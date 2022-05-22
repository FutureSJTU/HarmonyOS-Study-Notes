package com.liangzili.future_bilibilicard.widget.dynamic;

public class CardsEntity {
    //标题
    private String title;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String dynamic;
    public String getDynamic() {
        return dynamic;
    }
    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    private String pic;
    public String getPic() {
        return pic;
    }
    public void setPic(String pic) {
        this.pic = pic;
    }

    private String short_link;
    public String getShort_link() {
        return short_link;
    }
    public void setShort_link(String short_link) {
        this.short_link = short_link;
    }

    public static class Owner{

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
