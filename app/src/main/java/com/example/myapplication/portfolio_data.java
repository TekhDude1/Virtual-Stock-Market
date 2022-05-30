package com.example.myapplication;

public class portfolio_data implements Comparable{
    private String name;
    private double price;
    private double profit;
    private double percent;

    public portfolio_data(String name, double price, double profit,double percent) {
        this.name = name;
        this.price = price;
        this.profit = profit;
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getPercent(){
        return percent;
    }
    public void setPercent(double percent){this.percent=percent;}

    @Override
    public int compareTo(Object o) {
        portfolio_data da = (portfolio_data) o;
        if(da.getPrice()==this.price)
            return 0;
        return 1;
    }
}
