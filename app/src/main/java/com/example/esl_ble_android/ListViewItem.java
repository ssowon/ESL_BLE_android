package com.example.esl_ble_android;

public class ListViewItem {
    private String numberStr ;
    private String nameStr ;
    private String posStr ;
    private String priceStr ;
    private String saleStr ;

    public void setName(String name) {
        this.nameStr = name ;
    }
    public void setPrice(String price) {
        this.priceStr = price ;
    }
    public void setPos(String pos) {
        this.posStr = pos ;
    }
    public void setNumber(String number) {
        this.numberStr = number ;
    }
    public void setSale(String sale) {
        this.saleStr = sale ;
    }

    public String getName() {
        return this.nameStr ;
    }
    public String getPrice() {
        return this.priceStr ;
    }
    public String getPos() {
        return this.posStr ;
    }
    public String getNumber() {
        return this.numberStr ;
    }
    public String getSale() {
        return this.saleStr ;
    }

    public ListViewItem(String name, String price, String pos, String number, String sale){
        this.nameStr = name;
        this.priceStr = price;
        this.posStr = pos;
        this.numberStr = number;
        this.saleStr = sale;
    }
}