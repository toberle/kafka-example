package com.github.toberle.kafka.example.dto;

public class TestDto {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TestDto{" +
                "data='" + data + '\'' +
                '}';
    }
}
