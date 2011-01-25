package com.imagem.gwtpplugin.projectfile;

public class Variable {

	private final String type;
	private final String name;
	private String imports;
	
	public Variable(String type, String name) {
		this(type, name, "");
	}
	
	public Variable(String type, String name, String imports) {
		this.type = type;
		this.name = name;
		this.imports = imports;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImport() {
		return imports;
	}
	
	public String toString() {
		return type + " " + name;
	}
	
	public String getCapName() {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public void setImport(String imports) {
		this.imports = imports;
	}
}
