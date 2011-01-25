package com.imagem.gwtpplugin.projectfile.src.client.event;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class Handler implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String eventPackage;

	public Handler(String name, String eventPackage) {
		this.name = name;
		this.eventPackage = eventPackage;
	}

	@Override
	public String getName() {
		return name + "Handler";
	}

	@Override
	public String getPackage() {
		return eventPackage;
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

		contents += "import com.google.gwt.event.shared.EventHandler;\n\n";
		
		contents += "public interface " + getName() + " extends EventHandler {\n\n";
		
		contents += "	public void on" + name + "(" + name + "Event event);\n";
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
