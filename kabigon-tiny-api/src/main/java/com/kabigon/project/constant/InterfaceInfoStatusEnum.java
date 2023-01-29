package com.kabigon.project.constant;

/**
 * @author kabigon
 * @version 2023/1/24/13:49
 */
public enum InterfaceInfoStatusEnum {

    ONLINE("上线", 1),
    offline("下线", 0);


    private String message;
    private int code;

    InterfaceInfoStatusEnum(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
