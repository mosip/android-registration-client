package io.mosip.registration.clientmanager.dto.http;

import lombok.Data;


@Data
public class RegProcRequestWrapper<T> {

    private String id;
    private String requesttime;
    private String version;
    private T request;
}
