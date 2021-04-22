package lv.dev.mintos.weatherdemo.infra;

public class RestException extends RuntimeException {

    private String messageId;
    private Object[] params;

    public RestException(String messageId, Object... params) {
        this.messageId = messageId;
        this.params = params;
    }

    public String getMessageId() {
        return messageId;
    }

    public Object[] getParams() {
        return params;
    }
}
