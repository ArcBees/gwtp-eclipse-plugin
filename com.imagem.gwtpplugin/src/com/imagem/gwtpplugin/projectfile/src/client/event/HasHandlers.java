package com.imagem.gwtpplugin.projectfile.src.client.event;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class HasHandlers implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String eventPackage;

	public HasHandlers(String name, String eventPackage) {
		this.name = name;
		this.eventPackage = eventPackage;
	}

	@Override
	public String getName() {
		return "Has" + name + "Handlers";
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

		contents += "import com.google.gwt.event.shared.HandlerRegistration;\n";
		contents += "import com.google.gwt.event.shared.HasHandlers;\n\n";
		
		contents += "public interface " + getName() + " extends HasHandlers {\n\n";
		
		contents += "	public HandlerRegistration add" + name + "Handler(" + name + "Handler handler);\n";
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
