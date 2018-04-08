package com.casper.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * 配置文件实体映射
 */
@ConfigurationProperties(prefix = "casper.hello")
public class HelloProperties
{
    //消息内容
    private String msg = "Casper";
    //是否显示消息内容
    private boolean show = true;

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public boolean isShow() {
        return show;
    }
    public void setShow(boolean show) {
        this.show = show;
    }
}