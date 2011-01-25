package com.imagem.gwtpplugin.projectfile.src.client.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class Translations implements IProjectFile {
	
	private final String EXTENSION = ".java";
	private String resourcePackage;
	
	public Translations(String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}

	@Override
	public String getName() {
		return "Translations";
	}

	@Override
	public String getPackage() {
		return resourcePackage;
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
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.gwt.i18n.client.Constants;\n\n";

		contents += "public interface " + getName() + " extends Constants {\n\n";
		// TODO create subsections
		
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
