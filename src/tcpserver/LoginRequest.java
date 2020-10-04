package tcpserver;

public class LoginRequest extends Request {
	
	private static final long serialVersionUID = -2019661214153557891L;
	private String username;
	private String passw;

	public LoginRequest(String kind, String value, String username, String passw) {
		super(kind, value);
		this.username=username;
		this.passw=passw;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassw() {
		return passw;
	}

	public void setPassw(String passw) {
		this.passw = passw;
	}

}
