package org.casper.learning.io.nettyrpc.demo;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {

    private Integer id;
    private String name;
    private String age;
    private String title;

    private Object income;

    @Data
    public static class Salary {
        private Integer income;
        private Integer tax;
        private Integer taxRate;
    }
}
