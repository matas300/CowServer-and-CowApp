package tcpclient;

import java.io.Serializable;

public abstract class Request implements Serializable{
    private static final long serialVersionUID = 1L;

    private String kind;
    private String value;

    public Request(String kind, String value) {
        this.kind=kind;
        this.value=value;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
