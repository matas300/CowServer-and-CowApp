package tcpclient;

import java.time.LocalDate;

public class DownloadRequest extends Request{

    private static final long serialVersionUID = 1879800592232755774L;
    private LocalDate start;
    private LocalDate end;
    private int delay;
    private String pathDir;


    public DownloadRequest(String pathDir, String kind, String value, LocalDate start) {
        super(kind, value);
        this.pathDir=pathDir;
        this.start= start;
        this.end=null;
        this.delay=0;
    }
    public DownloadRequest(String pathDir, String kind, String value, LocalDate start, int delay) {
        super(kind, value);
        this.pathDir=pathDir;
        this.start= start;
        this.end=null;
        this.delay=delay;
    }
    public DownloadRequest(String pathDir, String kind, String value, LocalDate start, LocalDate end) {
        super(kind, value);
        this.pathDir=pathDir;
        this.start= start;
        this.end=end;
        this.delay=0;
    }
    public DownloadRequest(String pathDir, String kind, String value, LocalDate start, LocalDate end, int delay) {
        super(kind, value);
        this.pathDir=pathDir;
        this.start= start;
        this.end=end;
        this.delay=delay;
    }
    public String getPathDir() {
        return pathDir;
    }
    public void setPathDir(String pathDir) {
        this.pathDir = pathDir;
    }
    public LocalDate getStart() {
        return start;
    }
    public void setStart(LocalDate start) {
        this.start = start;
    }
    public LocalDate getEnd() {
        return end;
    }
    public void setEnd(LocalDate end) {
        this.end = end;
    }
    public int getDelay() {
        return delay;
    }
    public void setDelay(int delay) {
        this.delay = delay;
    }

}

