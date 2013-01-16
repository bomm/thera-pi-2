package Tools;

public class IndiKey{
	public String indischl ;
	public String grunddaten;
	public String vorrangig;
	public String ergaenzend;
	public boolean ungerade; 
	//SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	public IndiKey(String indi,String grunddaten,String vorrangig,String ergaenzend,int position){
		
		this.indischl = indi;
		this.grunddaten = grunddaten;
		this.vorrangig = vorrangig;
		this.ergaenzend = ergaenzend;
		this.ungerade = (position % 2 == 0 ? false : true);
	}
}