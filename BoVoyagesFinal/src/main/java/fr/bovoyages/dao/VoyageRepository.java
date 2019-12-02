package fr.bovoyages.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.bovoyages.entities.Payeur;
import fr.bovoyages.entities.Voyage;
import fr.bovoyages.entities.Voyageur;

@Repository
public interface VoyageRepository extends JpaRepository<Voyage, Long>{
	List<Voyage> findByClient(Payeur c);

	void save(Voyageur voyageur); 
}
