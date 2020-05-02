package com.sample.MovEmt.emtApi;

public class EndPoint {
    public static final String LOGIN = "https://openapi.emtmadrid.es/v1/mobilitylabs/user/login/";
    public static final String ARRIVE_LINES = "https://openapi.emtmadrid.es/v2/transport" +
            "/busemtmad/stops/%s/arrives/";
    public static final String INFO_STOP = "https://openapi.emtmadrid.es/v1/transport/busemtmad/stops/%s/detail/";
    public static final String GET_STOP = "https://openapi.emtmadrid.es/v1/transport/busemtmad/stops/list/";
}
