package com.example.myapplication;

public class order_data implements Comparable{
    private String ordertype;
    private String symbol;
    private Double buyprice;
    private Double buyquantity;
    private Double price;
    private Double percent;

    public order_data(String ordertype, String symbol, Double buyprice, Double buyquantity, Double price, Double percent) {
        this.ordertype = ordertype;
        this.symbol = symbol;
        this.buyprice = buyprice;
        this.buyquantity = buyquantity;
        this.price = price;
        this.percent = percent;
    }

    public Double getBuyquantity() {
        return buyquantity;
    }

    public void setBuyquantity(Double buyquantity) {
        this.buyquantity = buyquantity;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getBuyprice() {
        return buyprice;
    }

    public void setBuyprice(Double buyprice) {
        this.buyprice = buyprice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    @Override
    public int compareTo(Object o) {
        order_data da = (order_data) o;
        if(da.getPrice()==this.price)
            return 0;
        return 1;
    }
}
