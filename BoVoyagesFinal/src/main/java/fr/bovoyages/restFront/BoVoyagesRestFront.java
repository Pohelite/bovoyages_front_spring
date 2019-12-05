package fr.bovoyages.restFront;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.bovoyages.dao.DatesVoyageRepository;
import fr.bovoyages.dao.DestinationRepository;
import fr.bovoyages.dao.PayeurRepository;
import fr.bovoyages.dao.VoyageRepository;
import fr.bovoyages.dto.DatesVoyageDTO;
import fr.bovoyages.dto.DestinationDTO;
import fr.bovoyages.entities.DatesVoyage;
import fr.bovoyages.entities.Destination;
import fr.bovoyages.entities.Image;
import fr.bovoyages.entities.Payeur;
import fr.bovoyages.entities.Voyage;
import fr.bovoyages.entities.Voyageur;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class BoVoyagesRestFront {
	@Autowired
	private DestinationRepository destiRepo;
	@Autowired
	private VoyageRepository voyageRepo;
	@Autowired
	private DatesVoyageRepository datesVoyageRepo;
	@Autowired
	private PayeurRepository payeurRepo;
	@Autowired
	private JavaMailSender mailSender;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////FRONT////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////DESTINATIONS//////////////////////////////////////////////////////////
	@GetMapping("/api/destinations")
	public List<DestinationDTO> getAllValidDestinations() {
		List<Destination> destinations = destiRepo.findAll();
		List<DestinationDTO> dtos = new ArrayList<DestinationDTO>();
		for (Destination d : destinations) {
			if (d.getDeleted() != 1) {
				dtos.add(new DestinationDTO(d));
			}
		}
		return dtos;
	}

	@GetMapping("/destination-details/{id}")
	public DestinationDTO getDestinationDetails(@PathVariable("id") long id) {
		Destination destination = destiRepo.findById(id).get();
		if (destination.getDeleted() != 1) {
			return new DestinationDTO(destination);
		} else {
			return new DestinationDTO();
		}
	}

	@GetMapping("/destinationsByRegion")
	public List<DestinationDTO> getDestinationByRegion(@RequestBody String region) {
		List<Destination> destinations = destiRepo.getByRegionStartingWith(region);
		List<DestinationDTO> dtos = new ArrayList<DestinationDTO>();
		for (Destination d : destinations) {
			if (d.getDeleted() != 1) {
				dtos.add(new DestinationDTO(d));
			}
		}
		return dtos;
	}

//	@PostMapping("/destinationsByRegion")
//	public List<DestinationDTO> getDestinationByRegion(@RequestBody String region) {
//		List<Destination> destinations = destiRepo.getByRegionStartingWith(region);
//		List<DestinationDTO> dtos = new ArrayList<DestinationDTO>();
//		for (Destination d : destinations) {
//			if (d.getDeleted() != 1) {
//				dtos.add(new DestinationDTO(d));
//			}
//		}
//		return dtos;
//	}

	// récupération des dates pour une destination donnée
	@GetMapping("/destination/dates/{id}")
	public List<DatesVoyageDTO> getDatesByDestinationId(@PathVariable("id") long id) {
		Destination destination = destiRepo.findById(id).get();
		List<DatesVoyageDTO> dtos = new ArrayList<DatesVoyageDTO>();
		List<DatesVoyage> dates = destination.getDates();
		for (DatesVoyage d : dates) {
			dtos.add(new DatesVoyageDTO(d));
		}
		return dtos;
	}
	
	
///////////////////////////////////////////DESTINATIONS//////////////////////////////////////////////////////////

///////////////////////////////////////////VOYAGES//////////////////////////////////////////////////////////

//cette méthode devra être appelée à chaque fois que le client ajoute un
//voyageur à son voyage
//afin de décrémenter le nombre de places disponibles pour les dates choisies
	@PostMapping("/voyage/{id}/addVoyageursPessimistic")
	public Voyage addvoyageursPessimistic(@PathVariable("id") long id, @RequestBody Voyageur voyageur) {
		Voyage voyage = voyageRepo.findById(id).get();
		DatesVoyage dates = voyage.getDateVoyage();
		dates.setNbrePlaces(dates.getNbrePlaces() - 1);
		datesVoyageRepo.save(dates);
		List<Voyageur> listeVoyageurs = voyage.getParticipants();
		listeVoyageurs.add(voyageur);
		// methode ajoutee
		voyageRepo.save(voyageur);
		voyage.setParticipants(listeVoyageurs);
		return voyage;
	}

//cette méthode devra être appelée si le client ne confirme pas son voyage
//afin de supprimer de la base le voyage et les voyageurs qu'il aura ajouté à son voyage
	@DeleteMapping("/voyage/cancel")
	public String deleteVoyagePessimistic(@RequestBody Voyage voyage) {
		DatesVoyage dates = voyage.getDateVoyage();
		dates.setNbrePlaces(dates.getNbrePlaces() + voyage.getParticipants().size());
		datesVoyageRepo.save(dates);
		voyageRepo.delete(voyage);
		return "La commande de votre voyage a bien été annulée";
	}

	@PostMapping("/voyage/order")
	public Voyage orderVoyage(@RequestBody Voyage voyage) {
		DatesVoyage dates = voyage.getDateVoyage();
		if (dates.getNbrePlaces() < voyage.getParticipants().size()) {
			return null;
		}
		float prixTotal=dates.getPrixHT() * voyage.getParticipants().size();
		voyage.setPrixTotal(prixTotal);
		voyageRepo.save(voyage);
//		Payeur client = voyage.getClient();
//		String mailClient = client.getMail();
		// envoi du mail de confirmation
//		SimpleMailMessage mail = new SimpleMailMessage();
//		mail.setTo(mailClient);
//		mail.setFrom("no-reply@bovoyages.net");
//		mail.setSubject("Merci pour votre commande");
//		mail.setText("Très cher " + client.getNom() + ",\n\nBoVoyages vous remercie pour votre commande."
//				+ "\nVous recevrez très prochainement un courrier avec vos billets et les détails de votre voyage"
//				+ "\n\nLe Président,\nEmmanuel Macron");
//		mailSender.send(mail);
		return voyage;
	}

	@GetMapping("/voyage/{id}")
	public Voyage getVoyageById(@PathVariable("id") long id) {
		Voyage v = voyageRepo.findById(id).get();
		return v;
	}

	@GetMapping("/voyage/{id}/voyageurs")
	public List<Voyageur> getVoyageVoyageurs(@PathVariable("id") long id) {
		Voyage voyage = voyageRepo.findById(id).get();
		return voyage.getParticipants();
	}

	@PostMapping("/voyage/{id}/updateVoyageurs")
	public Voyage updateVoyageurs(@PathVariable("id") long id, @RequestBody List<Voyageur> voyageurs) {
		Voyage voyage = voyageRepo.findById(id).get();
		voyage.setParticipants(voyageurs);
		voyageRepo.save(voyage);
		return voyage;
	}

///////////////////////////////////////////IMAGES//////////////////////////////////////////////////////////
	@GetMapping("/destination-details/images/{id}")
	public List<Image> getImagesByDestinationId(@PathVariable("id") long id) {
		Destination destination = destiRepo.findById(id).get();
		List<Image> images = destination.getImages();
		return images;
	}
	
	
	
	
	
	
	
///////////////////////////////////////////CLIENT/USER/////////////////////////////////////////////////////////

	@PostMapping("/client/allVoyages")
	public List<Voyage> getClientVoyages(@RequestBody Payeur client) {
		return voyageRepo.findByClient(client);
	}

	@PostMapping("/client/new")
	public String newClient(@RequestParam("nom") String nom, @RequestParam("mail") String mail,
			@RequestParam("password") String password) {
		if (payeurRepo.findByNom(nom) != null) {
			return "User already exists";
		} else if (payeurRepo.findByMail(mail) != null) {
			return "Email already exists";
		} else {
			payeurRepo.persistUser(nom, mail, password);
			return "User created";
		}
	}

//	@PostMapping("/connexion")
//	public String checkUserCredentials(@RequestParam("nom") String nom, @RequestParam("password") String password) {
//		String msg = "coucou" + nom + password;
//		if (payeurRepo.findByNom(nom) != null) {
//			if (password.equals(payeurRepo.getClientPassword(nom))) {
//				msg = "ok";
//			} else {
//				msg = "Mot de passe incorrect";
//			}
//		} else {
//			msg = "Nom d'utilisateur incorrect";
//		}
//
//		return msg;
//	}

	@PostMapping("/connexion")
	public Boolean checkUserCredentials(@RequestBody Identifiants identifiants) {
		String id = identifiants.getIdentifiant();
		String pwd = identifiants.getPassword();
		String msg = "coucou" + id;
		if (payeurRepo.findByNom(id) != null) {
			if (pwd.equals(payeurRepo.getClientPassword(id))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////BACK/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@PostMapping("/destination/add")
	public DestinationDTO addDestination(@RequestBody Destination destination) {
		destiRepo.save(destination);
		return new DestinationDTO(destination);
	}

	@PostMapping("destination/update")
	public DestinationDTO updateDestination(@RequestBody Destination destination) {
		long oldId = destination.getId();
		destiRepo.delete(destiRepo.findById(oldId).get());
		destination.setId(oldId);
		destiRepo.save(destination);
		return new DestinationDTO(destination);
	}

	@PostMapping("/destination/delete")
	public DestinationDTO deleteDestination(@RequestBody Destination destination) {
		destination.setDeleted(1);
		destiRepo.save(destination);
		return new DestinationDTO(destination);
	}

	@PostMapping("/destination/{id}/dates/add")
	public DestinationDTO addDatesVoyagesToDestination(@PathVariable("id") long idDest,
			@RequestBody DatesVoyage newDates) {
		Destination destination = destiRepo.findById(idDest).get();
		List<DatesVoyage> listeDates = destination.getDates();
		listeDates.add(newDates);
		destination.setDates(listeDates);
		destiRepo.save(destination);
		return new DestinationDTO(destination);
	}

	@PostMapping("/destination/{id}/dates/update")
	public DestinationDTO updateDatesVoyagesToDestination(@PathVariable("id") long idDest,
			@RequestBody DatesVoyage datesToUpdate) {
		Destination destination = destiRepo.findById(idDest).get();
		List<DatesVoyage> listeDates = destination.getDates();
		long oldIdDates = datesToUpdate.getId();
		datesVoyageRepo.delete(datesVoyageRepo.findById(oldIdDates).get());
		datesToUpdate.setId(oldIdDates);
		listeDates.add(datesToUpdate);
		destination.setDates(listeDates);
		destiRepo.save(destination);
		return new DestinationDTO(destination);
	}

	@PostMapping("/destination/{id}/dates/delete")
	public DestinationDTO deleteDatesVoyagesToDestination(@PathVariable("id") long idDest,
			@RequestBody DatesVoyage datesToErase) {
		Destination destination = destiRepo.findById(idDest).get();
		List<DatesVoyage> listeDates = destination.getDates();
		listeDates.remove(datesToErase);
		destination.setDates(listeDates);
		destiRepo.save(destination);
		return new DestinationDTO(destination);
	}

}
