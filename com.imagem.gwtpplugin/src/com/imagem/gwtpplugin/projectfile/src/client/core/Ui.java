package com.imagem.gwtpplugin.projectfile.src.client.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class Ui implements IProjectFile {

	private final String EXTENSION = ".ui.xml";
	private String name;
	private String viewPackage;

	public Ui(String name, String viewPackage) {
		this.name = name;
		this.viewPackage = viewPackage;
	}

	@Override
	public String getName() {
		return name + "View";
	}

	@Override
	public String getPackage() {
		return viewPackage;
	}

	@Override
	public String getPath() {
		return "src/" + getPackage().replace('.', '/');
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "<!DOCTYPE ui:UiBinder SYSTEM \"http://dl.google.com/gwt/DTD/xhtml.ent\">\n\n";
		
		contents += "<ui:UiBinder xmlns:ui='urn:ui:com.google.gwt.uibinder'\n";
		contents += "		ui:generateFormat='com.google.gwt.i18n.rebind.format.PropertiesFormat'\n";
		contents += "		ui:generateKeys='com.google.gwt.i18n.rebind.keygen.MD5KeyGenerator'\n";
		contents += "		ui:generateLocales='default'>\n\n";

		contents += "	<!-- Layout your view here -->\n";
		
		contents += "</ui:UiBinder>\n";
		

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
