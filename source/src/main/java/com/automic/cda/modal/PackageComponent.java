package com.automic.cda.modal;

import java.util.List;
import java.util.Set;

public class PackageComponent {

	private String name;
	private Artifact artifact;
	private List<Condition> conditions;

	public PackageComponent() {
	}
	
	public PackageComponent(String name, Artifact artifact, List<Condition> conditions) {
		this.name = name;
		this.artifact = artifact;
		this.conditions = conditions;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public String toString() {
		return "Component [name=" + name + ", artifact=" + artifact + "]";
	}
	
	 public boolean evaluate(Set<String> data) {
	        List<Condition> conds = getConditions();

	        boolean match = true;
	        for (Condition cond : conds) {
	            match = cond.evaluate(data);
	            if (!match) {
	                break;
	            }
	        }
	        return match;
	    }
	
}
