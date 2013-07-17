package CommonTools;

public class FireRehaError {

	public FireRehaError(Object source,String event,String[] details){
		try{
			RehaEvent revt = new RehaEvent(source);
			revt.setRehaEvent(event);
			revt.setDetails(details[0], details[1]);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
