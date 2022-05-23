package it.manytomanyjpamaven.test;

import java.util.Date;
import java.util.List;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
			initRuoli(ruoloServiceInstance);

			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

//			testInserisciNuovoUtente(utenteServiceInstance);
//			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
//
//			testCollegaUtenteARuoloEsistente(ruoloServiceInstance, utenteServiceInstance);
//			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
//
//			testModificaStatoUtente(utenteServiceInstance);
//			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
//
//			testRimuoviRuoloDaUtente(ruoloServiceInstance, utenteServiceInstance);
//			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
//
//			testRimuoviUtente(utenteServiceInstance);
//			
			testTrovaTuttiConDataCreazineAGiugno(utenteServiceInstance)	;
			
			testContaUtentiAdmin(utenteServiceInstance);
			
			testTrovaUtentiConPasswordMinoreDiOttoCaratteri(utenteServiceInstance);
			
			testControllaSeUtentiBloccatiAlmenoUnAdmin(utenteServiceInstance);
			
			testTrovaDescrizioniRuoliConUtentiAssociati(ruoloServiceInstance);
			
			
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}
	}

	private static void testInserisciNuovoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoUtente inizio.............");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		System.out.println(".......testInserisciNuovoUtente fine: PASSED.............");
	}

	private static void testCollegaUtenteARuoloEsistente(RuoloService ruoloServiceInstance,
			UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testCollegaUtenteARuoloEsistente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCollegaUtenteARuoloEsistente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario.bianchi", "JJJ", "mario", "bianchi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testInserisciNuovoUtente fallito: ruoli non aggiunti ");

		System.out.println(".......testCollegaUtenteARuoloEsistente fine: PASSED.............");
	}

	private static void testModificaStatoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoUtente inizio.............");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testModificaStatoUtente fallito: utente non inserito ");

		// proviamo a passarlo nello stato ATTIVO ma salviamoci il vecchio stato
		StatoUtente vecchioStato = utenteNuovo.getStato();
		utenteNuovo.setStato(StatoUtente.ATTIVO);
		utenteServiceInstance.aggiorna(utenteNuovo);

		if (utenteNuovo.getStato().equals(vecchioStato))
			throw new RuntimeException("testModificaStatoUtente fallito: modifica non avvenuta correttamente ");

		System.out.println(".......testModificaStatoUtente fine: PASSED.............");
	}

	private static void testRimuoviRuoloDaUtente(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testRimuoviRuoloDaUtente inizio.............");

		// carico un ruolo e lo associo ad un nuovo utente
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("aldo.manuzzi", "pwd@2", "aldo", "manuzzi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente non inserito ");
		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);

		// ora ricarico il record e provo a disassociare il ruolo
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		boolean confermoRuoloPresente = false;
		for (Ruolo ruoloItem : utenteReloaded.getRuoli()) {
			if (ruoloItem.getCodice().equals(ruoloEsistenteSuDb.getCodice())) {
				confermoRuoloPresente = true;
				break;
			}
		}

		if (!confermoRuoloPresente)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente e ruolo non associati ");

		// ora provo la rimozione vera e propria ma poi forzo il caricamento per fare un confronto 'pulito'
		utenteServiceInstance.rimuoviRuoloDaUtente(utenteReloaded.getId(), ruoloEsistenteSuDb.getId());
		utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (!utenteReloaded.getRuoli().isEmpty())
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo ancora associato ");

		System.out.println(".......testRimuoviRuoloDaUtente fine: PASSED.............");
	}
	
	
	
	private static void testRimuoviUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testRimuoviUtente inizio.............");

		//Mi inserisco in tabella un nuovo utente e me lo configuro
		Utente daRimuovere = new Utente("PierParolo", "GGG", "Paolino", "Ruffini", new Date());
		
		//lo inserisco nel database
		utenteServiceInstance.inserisciNuovo(daRimuovere);
		
		if(daRimuovere.getId() < 1)
			throw new RuntimeException("testRimuoviUtente FALLITO, utente non inserito correttamente");
		
		int conferma = utenteServiceInstance.listAll().size();
		System.out.println("elementi in tabella utente prima del delete: "+ conferma);
		//Rimuovo l utente
		utenteServiceInstance.rimuovi(daRimuovere.getId());
		
		if(utenteServiceInstance.listAll().size() == conferma)
			throw new RuntimeException("testRimoviUtente FAILED");
		
		System.out.println("elementi in tabella utente dopo del delete: "+ utenteServiceInstance.listAll().size() );

		System.out.println(".......testRimuoviUtente fine: PASSED.............");
	}
	
	
	
	private static void testTrovaTuttiConDataCreazineAGiugno(UtenteService utenteServiceInstance) {
		System.out.println(".......testTrovaTuttiConDataCreazineAGiugno inizio.............");

		List<Utente> result = utenteServiceInstance.trovaTuttiConDataCreazineAGiugno();
		
		if (result.size() == 0)
			throw new RuntimeException("testTrovaTuttiConDataCreazineAGiugno fallito ");
		for(Utente utenteItem : result)
			System.out.println(utenteItem.getNome()+" "+ utenteItem.getUsername());

		System.out.println(".......testTrovaTuttiConDataCreazineAGiugno fine: PASSED.............");
		
	}
	
	
	private static void testContaUtentiAdmin(UtenteService utenteServiceInstance) {
		System.out.println(".......testContaUtentiAdmin inizio.............");

		Long result = utenteServiceInstance.contaUtentiAdmin();
		
		if (result == 0)
			throw new RuntimeException("testContaUtentiAdmin fallito ");
		System.out.println(result);

		System.out.println(".......testContaUtentiAdmin fine: PASSED.............");
		
	}
	
	
	
	
	private static void testTrovaUtentiConPasswordMinoreDiOttoCaratteri(UtenteService utenteServiceInstance) {
		System.out.println(".......testTrovaUtentiConPasswordMinoreDiOttoCaratteri inizio.............");

		List<Utente> result = utenteServiceInstance.trovaUtentiConPasswordMinoreDiOttoCaratteri();
		
		if (result.size() == 0)
			throw new RuntimeException("testTrovaUtentiConPasswordMinoreDiOttoCaratteri fallito ");
		for(Utente utenteItem : result)
			System.out.println(utenteItem.getNome()+ " " + utenteItem.getUsername());

		System.out.println(".......testTrovaUtentiConPasswordMinoreDiOttoCaratteri fine: PASSED.............");
		
		
	}
	
	private static void testControllaSeUtentiBloccatiAlmenoUnAdmin(UtenteService utenteServiceInstance) {
		
		System.out.println(".......testControllaSeUtentiBloccatiAlmenoUnAdmin inizio.............");

		
		
		if (!utenteServiceInstance.controllaSeUtentiBloccatiAlmenoUnAdmin())
			throw new RuntimeException("testControllaSeUtentiBloccatiAlmenoUnAdmin fallito ");
		
		System.out.println(utenteServiceInstance.controllaSeUtentiBloccatiAlmenoUnAdmin());

		System.out.println(".......testControllaSeUtentiBloccatiAlmenoUnAdmin fine: PASSED.............");
		
	}
	
	
	
	private static void testTrovaDescrizioniRuoliConUtentiAssociati(RuoloService ruoloServiceInstance) {
		

		System.out.println(".......testTrovaDescrizioniRuoliConUtentiAssociati inizio.............");

		
		List<String> result = ruoloServiceInstance.trovaDescrizioniRuoliConUtentiAssociati();
		
		for(String stringItem : result)
			System.out.println(stringItem);
		
	

		System.out.println(".......testTrovaDescrizioniRuoliConUtentiAssociati fine: PASSED.............");
		
		
	}
	
	
	
	
	
//	private static void testRimoviRuolo(RuoloService ruoloServiceInstance) {
//		System.out.println(".......testRimoviRuolo inizio.............");
//
//		//Mi inserisco in tabella un nuovo utente e me lo configuro
//		Utente daRimuovere = new Utente("PierParolo", "GGG", "Paolino", "Ruffini", new Date());
//		
//		//lo inserisco nel database
//		utenteServiceInstance.inserisciNuovo(daRimuovere);
//		
//		if(daRimuovere.getId() < 1)
//			throw new RuntimeException("testRimoviRuolo FALLITO, utente non inserito correttamente");
//		
//		int conferma = utenteServiceInstance.listAll().size();
//		System.out.println("elementi in tabella utente prima del delete: "+ conferma);
//		//Rimuovo l utente
//		utenteServiceInstance.rimuovi(daRimuovere.getId());
//		
//		if(utenteServiceInstance.listAll().size() == conferma)
//			throw new RuntimeException("testRimoviUtente FAILED");
//		
//		System.out.println("elementi in tabella utente dopo del delete: "+ utenteServiceInstance.listAll().size() );
//
//		System.out.println(".......testRimuoviUtente fine: PASSED.............");
//	}
	

}
























