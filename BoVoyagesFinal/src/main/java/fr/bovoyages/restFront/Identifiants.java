package fr.bovoyages.restFront;

public class Identifiants {
	private String identifiant;
	private String password;
	
	public Identifiants(String identifiant, String password) {
		super();
		this.identifiant = identifiant;
		this.password = password;
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
