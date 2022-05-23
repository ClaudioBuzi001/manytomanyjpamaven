package it.manytomanyjpamaven.dao;

import java.util.List;

import it.manytomanyjpamaven.model.Ruolo;

public interface RuoloDAO extends IBaseDAO<Ruolo> {
	
	public Ruolo findByDescrizioneAndCodice(String descrizione, String codice) throws Exception;
	
	//-voglio la lista di descrizioni distinte dei ruoli con utenti associati
	//voglio una lista di descrizioni distnite dei ruoli che abbiano utenti associati, quindi ruoli
	public List<String> findAllDescrizioniWithUtentiAssociati();
}
