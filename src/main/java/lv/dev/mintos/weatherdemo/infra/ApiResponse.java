package lv.dev.mintos.weatherdemo.infra;

public record ApiResponse(Boolean success, Object body, String errorMessage) {

    public ApiResponse(Object body) {
        this(Boolean.TRUE, body, null);
    }

    public ApiResponse(String errorMessage) {
        this(Boolean.FALSE, null, errorMessage);
    }

}
