package com.imagem.gwtpplugin.projectfile.src.server.guice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class GuiceServletConfig implements IProjectFile {
	
	private final String EXTENSION = ".java";
	private String projectName;
	private String guicePackage;

	public GuiceServletConfig(String projectName, String guicePackage) {
		this.projectName = projectName;
		this.guicePackage = guicePackage;
	}

	@Override
	public String getName() {
		return projectName + "GuiceServletConfig";
	}

	@Override
	public String getPackage() {
		return guicePackage;
	}

	@Override
	public String getPath() {
		return "src/" + getPackage().replace('.', '/');
	}
	
	@Override
	public String getExtension() {
		return EXTENSION;
	}

	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.inject.Guice;\n";
		contents += "import com.google.inject.Injector;\n";
		contents += "import com.google.inject.servlet.GuiceServletContextListener;\n\n";

		contents += "public class " + getName() + " extends GuiceServletContextListener {\n\n";

		contents += "	@Override\n";
		contents += "	protected Injector getInjector() {\n";
		contents += "		return Guice.createInjector(new ServerModule(), new DispatchServletModule());\n";
		contents += "	}\n";

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
