package beans;

public class Employee {
	private String seCode;
	private String seName;
	private String emCode;
	private String emName;
	private String date;
	private String emPassword;
	private int type;
	
	//Employee 테이블의 이름을 가져오기 
	
	public String getSeName() {
		return seName;
	}
	public void setSeName(String seName) {
		this.seName = seName;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSeCode() {
		return seCode;
	}
	public void setSeCode(String seCode) {
		this.seCode = seCode;
	}
	public String getEmCode() {
		return emCode;
	}
	public void setEmCode(String emCode) {
		this.emCode = emCode;
	}
	public String getEmPassword() {
		return emPassword;
	}
	public void setEmPassword(String emPassword) {
		this.emPassword = emPassword;
	}
	public String getEmName() {
		return emName;
	}
	public void setEmName(String emName) {
		this.emName = emName;
	}

}