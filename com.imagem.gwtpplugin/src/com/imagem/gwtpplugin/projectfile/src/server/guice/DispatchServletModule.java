package com.imagem.gwtpplugin.projectfile.src.server.guice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class DispatchServletModule implements IProjectFile {
	
	private final String EXTENSION = ".java";
	private String projectName;
	private String guicePackage;
	private boolean useSessionCookie = true;

	public DispatchServletModule(String projectName, String guicePackage) {
		this.projectName = projectName;
		this.guicePackage = guicePackage;
	}

	public void useSessionCookie(boolean useSessionCookie) {
		this.useSessionCookie = useSessionCookie;
	}

	@Override
	public String getName() {
		return "DispatchServletModule";
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

		contents += "import com.google.inject.servlet.ServletModule;\n";
		contents += "import com.gwtplatform.dispatch.server.DispatchServiceImpl;\n";
		contents += "import com.gwtplatform.dispatch.shared.ActionImpl;\n";
		if(useSessionCookie)
			contents += "import com.gwtplatform.dispatch.shared.SecurityCookie;\n";

		contents += "public class " + getName() + " extends ServletModule {\n\n";

		contents += "	@Override\n";
		contents += "	protected void configureServlets() {\n";
		// TODO < GWT 2.1
		//contents += "		serve(\"/" + projectName.toLowerCase() + "/\" + ActionImpl.DEFAULT_SERVICE_NAME).with(DispatchServiceImpl.class);\n\n";
		
		// >= GWT 2.1
		contents += "		serve(\"/\" + ActionImpl.DEFAULT_SERVICE_NAME).with(DispatchServiceImpl.class);\n\n";
		
		if(useSessionCookie)
			contents += "		bindConstant().annotatedWith(SecurityCookie.class).to(\"sessionID\");\n\n";
		
		contents += "	}\n\n";

		contents += "}\n";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
