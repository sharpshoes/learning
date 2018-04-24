package org.casper.learning.io.nettyrpc.client.demo;

import lombok.Data;

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
