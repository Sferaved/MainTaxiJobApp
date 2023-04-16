package com.taxieasyua.job.driver_app;

public class Driver {
    private String city,firstName, secondName, email, phoneNumber, auto, modelAuto, typeAuto, colorAuto, yearsAuto, numberAuto;

    public Driver(String city, String firstName, String secondName, String email, String phoneNumber, String auto, String modelAuto, String typeAuto, String colorAuto, String yearsAuto, String numberAuto) {
        this.city = city;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.auto = auto;
        this.modelAuto = modelAuto;
        this.typeAuto = typeAuto;
        this.colorAuto = colorAuto;
        this.yearsAuto = yearsAuto;
        this.numberAuto = numberAuto;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

    public String getModelAuto() {
        return modelAuto;
    }

    public void setModelAuto(String modelAuto) {
        this.modelAuto = modelAuto;
    }

    public String getTypeAuto() {
        return typeAuto;
    }

    public void setTypeAuto(String typeAuto) {
        this.typeAuto = typeAuto;
    }
    public String getColorAuto() {
        return colorAuto;
    }

    public void setColorAuto(String colorAuto) {
        this.colorAuto = colorAuto;
    }

    public String getYearsAuto() {
        return yearsAuto;
    }

    public void setYearsAuto(String yearsAuto) {
        this.yearsAuto = yearsAuto;
    }

    public String getNumberAuto() {
        return numberAuto;
    }

    public void setNumberAuto(String numberAuto) {
        this.numberAuto = numberAuto;
    }

    @Override
    public String toString() {
        return
                "\nМісто:  " + city +
                "\nІм\'я:  " + firstName +
                "\nПрізвище: " + secondName +
                "\nEmail: " + email +
                "\nТелефон: " + phoneNumber +
                "\nМарка авто: " + auto +
                "\nМодель: " + modelAuto +
                "\nТип кузова: " + typeAuto +
                "\nКолор: " + colorAuto +
                "\nРік випуску: " + yearsAuto +
                "\nДержавий номер: " + numberAuto;
    }
}

