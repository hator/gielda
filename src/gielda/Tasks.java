package gielda;

public class Tasks {
	public static class Disconnect extends Exception {private static final long serialVersionUID = -5766835565280848553L;}
	public static class Error extends Exception {
		private static final long serialVersionUID = 625712011430764183L;
		
		private String msg; 
		public Error(String msg) {
			this.msg = msg;
		}
		public String getMsg(){
			return msg;
		}
	}
}
