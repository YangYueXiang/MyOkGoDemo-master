package com.yl.myokgodemo.bean;

/**
 * <p>Description:
 *
 * @author xzhang
 */

public class Data {
    private String downurl = "http://update.qbchoice.com/api/apkdown/7";
    private int serviceCode;
    private String msg;

    public String getDownurl() {
        return downurl;
    }

    public void setDownurl(String downurl) {
        this.downurl = downurl;
    }

    public int getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(int serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "downurl:" + downurl + ",code:" + serviceCode + ",msg:" + msg;
    }
}
