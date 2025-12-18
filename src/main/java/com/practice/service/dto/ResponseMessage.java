package com.practice.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseMessage {
    private String message;
    private String errorCode;
    private String errorMsg;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public ResponseMessage(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public ResponseMessage(String message, String errorCode, String errorMsg) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

}
