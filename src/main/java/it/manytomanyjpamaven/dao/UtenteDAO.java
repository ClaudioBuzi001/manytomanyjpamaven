package it.manytomanyjpamaven.dao;

import java.util.List;

import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public interface UtenteDAO extends IBaseDAO<Utente> {

	public List<Utente> findAllByRuolo(Ruolo ruoloInput);

	public Utente findByIdFetchingRuoli(Long id);

	// -voglio tutti gli utenti creati a giugno 2021
	public List<Utente> findAllByDateCreatedUgualeAGiugno();

	// -voglio il numero di utenti admin.
	public long countUtentiAdmin();

	// -voglòio la lista di utenti che abbiao password con meno di 8 cartatteri
	public List<Utente> findAllByPasswordShorterThanEightChar();
	
	//-voglio sapere se tra gli uenti disabilitati c è almeno un admin.
	public boolean checkUtentiDisabilitatiAlmenoUnAdmin();
}
