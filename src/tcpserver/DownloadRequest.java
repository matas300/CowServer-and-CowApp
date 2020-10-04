package tcpserver;

import java.time.LocalDateTime;

public class DownloadRequest extends Request{

	private static final long serialVersionUID = 1879800592232755774L;
	private LocalDateTime start;
	private LocalDateTime end;
	private int delay;
	private String pathDir;
	

	public DownloadRequest(String pathDir, String kind, String value, LocalDateTime start) {
		super(kind, value);
		this.pathDir=pathDir;
		this.start= start;
		this.end=null;
		this.delay=0;
	}
	public DownloadRequest(String pathDir, String kind, String value, LocalDateTime start, int delay) {
		super(kind, value);
		this.pathDir=pathDir;
		this.start= start;
		this.end=null;
		this.delay=delay;
	}
	public DownloadRequest(String pathDir, String kind, String value, LocalDateTime start, LocalDateTime end) {
		super(kind, value);
		this.pathDir=pathDir;
		this.start= start;
		this.end=end;
		this.delay=0;
	}
	public DownloadRequest(String pathDir, String kind, String value, LocalDateTime start, LocalDateTime end, int delay) {
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
	public LocalDateTime getStart() {
		return start;
	}
	public void setStart(LocalDateTime start) {
		this.start = start;
	}
	public LocalDateTime getEnd() {
		return end;
	}
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}

}
