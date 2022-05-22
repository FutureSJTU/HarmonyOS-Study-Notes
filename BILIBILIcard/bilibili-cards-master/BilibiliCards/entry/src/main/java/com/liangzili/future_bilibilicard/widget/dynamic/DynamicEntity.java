package com.liangzili.future_bilibilicard.widget.dynamic;

import java.util.List;

public class DynamicEntity {
    private int code;
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    private Data data;
    public static class Data{

        public static class Cards{
            //动态信息
            private String card;
            public String getCard() {
                return card;
            }
            public void setCard(String card) {
                this.card = card;
            }
        }
        private List<Cards> cards;
        public List<Cards> getCards() {
            return cards;
        }
        public void setCards(List<Cards> cards) {
            this.cards = cards;
        }
    }
    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }
}


