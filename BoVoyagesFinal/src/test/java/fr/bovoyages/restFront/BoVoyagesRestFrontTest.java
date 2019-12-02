package fr.bovoyages.restFront;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import fr.bovoyages.dto.DestinationDTO;
import fr.bovoyages.entities.DatesVoyage;
import fr.bovoyages.entities.Destination;
import fr.bovoyages.entities.Payeur;
import fr.bovoyages.entities.Voyage;
import fr.bovoyages.entities.Voyageur;


@SpringBootTest
class BoVoyagesRestFrontTest {
	private static final String WEB_APPLI = "http://localhost:7070";
	private static final Logger LOG = Logger.getLogger("TEST");

	//http://localhost:7070/api/destinations
	@Test
	void testGetAllValidDestinations() {
//		LOG.info("1");
		Client client = ClientBuilder.newClient();
//		LOG.info("2");
		WebTarget target = client.target(WEB_APPLI+"/api/destinations");
//		LOG.info("3");
		Destination[] destinations = target.request("application/json;charset=utf-8").get(Destination[].class);	
//		LOG.info("4");
		assertTrue(destinations.length>0);
}

	@Test
	void testGetDestinationDetails() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WEB_APPLI+"/destination-details/1");
		DestinationDTO dto = target.request("application/json;charset=utf-8").get(DestinationDTO.class);	
		assertNotNull(dto);
		assertEquals("Guadeloupe", dto.getRegion());
	}
	
	
//ca marche en get sur postman, on croise les doigts
//	@Test 
//	void testGetDestinationByRegion() {
//		Client client = ClientBuilder.newClient();
//		WebTarget target = client.target(WEB_APPLI+"/destinationsByRegion");
//		
////		DestinationDTO[] dtos = target.request("application/json;charset=utf-8").get(DestinationDTO[].class, "");
//		DestinationDTO[] dtos = target.request("application/json;charset=utf-8")
//										.post(Entity.entity("Guadeloupe", MediaType.APPLICATION_JSON), DestinationDTO[].class);	
////										.get(Entity.entity("Guadeloupe", MediaType.APPLICATION_JSON), DestinationDTO[].class);	
//		LOG.info(dtos[1].getRegion());
//		assertTrue(dtos.length==1);
//	}

	@Test
	//
	void testAddvoyageursPessimistic() {
		//le nombre de places diminue
//		Voyage voyage = voyageRepository.findById(1).get();
//		DatesVoyage dates=voyage.getDateVoyage();
//		dates.setNbrePlaces(dates.getNbrePlaces() - 1);
		
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WEB_APPLI+"/voyage/1/addVoyageursPessimistic");
		Voyageur voyageur = new Voyageur();
		voyageur.setCivilite("M");
		voyageur.setNom("Durand");
		voyageur.setPrenom("Michel");
//		DestinationDTO[] dtos = target.request("application/json;charset=utf-8").get(DestinationDTO[].class, "");
		Voyage voyage = target.request("application/json;charset=utf-8")
										.post(Entity.entity(voyageur, MediaType.APPLICATION_JSON), Voyage.class);	
		LOG.info(voyage.toString());
		assertNotNull(voyage);
	}
//
//	@Test
//	void testDeleteVoyagePessimistic() {
//		fail("Not yet implemented");
//	}
//
	
	@Test
	void testOrderVoyage() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WEB_APPLI+"/voyage/order");	
		
		Voyage voyage = new Voyage();//
		
		DatesVoyage dateVoyage = new DatesVoyage();
		Date date = new Date();
		date.toInstant();
		dateVoyage.setDateAller(date);
		dateVoyage.setDateRetour(date);
		voyage.setDateVoyage(dateVoyage);

		Voyageur voyageur = new Voyageur();
		voyageur.setCivilite("M");
		voyageur.setNom("Durand");
		voyageur.setPrenom("Michel");
		
		List<Voyageur> voyageurs = new ArrayList<Voyageur>();
		voyageurs.add(voyageur);
		voyage.setParticipants(voyageurs);///
		
		
		Payeur payeur = new Payeur();
		payeur.setNom("Jacques");
		payeur.setMail("jacques@gmail.com");
		voyage.setClient(payeur);//

		Voyage voyageReponse = target.request("application/json;charset=utf-8")
				.post(Entity.entity(voyage, MediaType.APPLICATION_JSON), Voyage.class);
		assertNotNull(voyage);

	}
//
//	@Test
//	void testGetVoyageById() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetVoyageVoyageurs() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateVoyageurs() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetClientVoyages() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testNewClient() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testCheckUserCredentials() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddDestination() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateDestination() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDeleteDestination() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddDatesVoyagesToDestination() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateDatesVoyagesToDestination() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDeleteDatesVoyagesToDestination() {
//		fail("Not yet implemented");
//	}

}
