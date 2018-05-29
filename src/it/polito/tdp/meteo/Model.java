package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	
	private MeteoDAO mDAO ;
	private List <Citta> citta;
	private List<Citta> best ;

	public Model() {
	
		mDAO= new MeteoDAO();
		citta = mDAO.getAllCitta();
	}

	public String getUmiditaMedia(int mese) {
		
		String result="";
				
		for(Citta c : mDAO.getAllCitta()) {
							
			result+= ""+c.getNome()+" "+mDAO.getAvgRilevamentiLocalitaMese(mese, c.getNome()).toString()+"\n";
		}
		
		return result;
	}

	
	private void recursive(int step, List<Citta> parziale) {
		
		if(step == NUMERO_GIORNI_TOTALI) {
			
			Double costo = punteggioSoluzione(parziale) ;
			
			if(best == null || costo < punteggioSoluzione(best) )
			
				best = new ArrayList<>(parziale) ;
			
			System.out.println(parziale);
			
			//return;
		}
				
		for(Citta c : citta) {
			
			if( controlliSup(c,parziale) ) {
				
				parziale.add(c);
				
				recursive(step+1,parziale);
				
				parziale.remove(c);
			}
		}
	}
	
	
	private boolean controlliSup(Citta c, List<Citta> parziale) {
		
		int contatoreCitta=0;
		
		for(Citta citta : parziale) { //conto i giorni passati in una città
			
			if(c.equals(citta))
				
				contatoreCitta++;	
		}
		
		if(contatoreCitta >= NUMERO_GIORNI_CITTA_MAX) //limite massimo per città
			
			return false;
		
		if(parziale.size()==0)  
			
			return true ;
		
		if(parziale.size()==1 || parziale.size()==2) // secondo o terzo giorno: non posso cambiare
			
			return parziale.get(parziale.size()-1).equals(c) ;

		if(parziale.get(parziale.size()-1).equals(c)) // giorni successivi, posso SEMPRE rimanere
			return true ;
		
		if(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) &&
				parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)) )
			return true ;
		
		if(parziale.size() == NUMERO_GIORNI_TOTALI) {
		
			for (Citta city : mDAO.getAllCitta()) {
			
				if( parziale.contains(city) )
					
					return true;
			}
		}
		
		return false;
	}

	public String trovaSequenza(int mese) {
		
		List<Citta> parziale = new ArrayList<>() ;
		this.best = null ;
		
		for(Citta prova : citta) {
			
			prova.setRilevamenti(mDAO.getAllRilevamentiLocalitaMese(mese, prova.getNome()));
		}
	
		recursive(0,parziale) ;
		
		return best.toString() ;	
		
	}

	private Double punteggioSoluzione(List<Citta> soluzioneCandidata) {
			
		double score = 0.0;
		
			if( soluzioneCandidata.get(soluzioneCandidata.size()-1) != soluzioneCandidata.get(soluzioneCandidata.size()-2))
				
				score +=100;
			
			for ( Citta c : soluzioneCandidata) {
				
				for(Rilevamento r : c.getRilevamenti())
					
					score+=r.getUmidita();	
			}

		return score;
	}
}
