package com.eodi.yak.eodi_yak.domain.reservation.entity;

public enum StatusType {
    WAIT("wait"), //대기
    OK("ok"), // 승인
    Rejection("rejection"); // 반려

    private String statusType;

    StatusType(String statusType)
    {
        this.statusType = statusType;
    }

    public String getStatusType() {
        return statusType;
    }
}