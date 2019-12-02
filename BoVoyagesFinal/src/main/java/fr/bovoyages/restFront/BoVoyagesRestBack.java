package fr.bovoyages.restFront;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.hash.Hashing;

import fr.bovoyages.dao.CommercialRepository;
import fr.bovoyages.entities.Commercial;

@Controller
public class BoVoyagesRestBack {
	@Autowired CommercialRepository comRepo;
	
	@GetMapping("/signin")
	public String signin(Model model) {
		Commercial commercial=new Commercial();
		model.addAttribute("commmercial", commercial);
		return "signin";
	}
	
	@PostMapping("/connexion")
	public String connexion(@RequestParam String password, Commercial commercial, Model model) {
		String msg="";
		
		if(comRepo.getByUsername(commercial.getUsername()).isPresent()) {
		String salt=comRepo.getCommercialSalt(commercial.getUsername());
		String toCalculate=salt+password;
		String calculatedHashPlusSalt=Hashing.sha256()
				.hashString(toCalculate, StandardCharsets.UTF_8)
				.toString();
		String baseHashAndSalt=comRepo.getCommercialSaltedHash(commercial.getUsername());
			if(calculatedHashPlusSalt.equals(baseHashAndSalt)) {
				model.addAttribute("Commercial", commercial);
				msg="connexion";
			}
			else {
				msg="home";
			}
		}
		return msg;
	}
	
	@PostMapping("/addCommercial")
	public String addCommercial(String username, String password) {
		String msg="";
		Commercial com=new Commercial(username);
		comRepo.save(com);
		String salt=String.valueOf(Math.random());
		String toCalculate=salt+password;
		String calculatedHashPlusSalt=Hashing.sha256()
				.hashString(toCalculate, StandardCharsets.UTF_8)
				.toString();
		comRepo.saveCommercialSaltAndSaltedHash(com.getUsername(), salt, calculatedHashPlusSalt);
		return msg;
	}
}
