package fr.bovoyages.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.bovoyages.entities.DatesVoyage;
import fr.bovoyages.entities.Destination;

public class DestinationDTO implements Serializable{
	private long id;
	private String region;
	private String description;
	private List<DatesVoyage> dates = new ArrayList<DatesVoyage>();
	
	public DestinationDTO(){}
	public DestinationDTO(Destination destination) {
		this.id = destination.getId();
		this.region = destination.getRegion();
		this.description = destination.getDescription();
		this.dates=destination.getDates();
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getRegion() {
		return region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public List<DatesVoyage> getDates() {
		return dates;
	}
	public void setDates(List<DatesVoyage> dates) {
		this.dates = dates;
	}
	
	
	

}
