package com.imagem.gwtpplugin.projectfile.war;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;

public class WebXml implements IProjectFile {

	private final String EXTENSION = ".xml";
	private final String NAME = "web";
	private String projectName;
	private String projectPackage;
	private String path;

	public WebXml(String projectName, String projectPackage, String path) {
		this.projectName = projectName;
		this.projectPackage = projectPackage;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getPackage() {
		return projectPackage;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		contents += "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n";
		contents += "	xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\"\n";
		contents += "	version=\"2.5\">\n\n";
		
		contents += "	<display-name>" + projectName + "</display-name>\n\n";
		
		contents += "	<!-- Default page to serve -->\n";
		contents += "	<welcome-file-list>\n";
		contents += "		<welcome-file>" + projectName + ".html</welcome-file>\n";
		contents += "	</welcome-file-list>\n\n";
		
		contents += "	<!--\n";
		contents += "		This Guice listener hijacks all further filters and servlets. Extra\n";
		contents += "		filters and servlets have to be configured in your\n";
		contents += "		ServletModule#configureServlets() by calling\n";
		contents += "		serve(String).with(Class<? extends HttpServlet>) and\n";
		contents += "		filter(String).through(Class<? extends Filter)\n";
		contents += "	-->\n";
		contents += "	<listener>\n";
		contents += "		<listener-class>" + projectPackage + ".server.guice." + projectName + "GuiceServletConfig</listener-class>\n";
		contents += "	</listener>\n\n";
		
		contents += "	<filter>\n";
		contents += "		<filter-name>guiceFilter</filter-name>\n";
		contents += "		<filter-class>com.google.inject.servlet.GuiceFilter</filter-class>\n";
		contents += "	</filter>\n\n";
		
		contents += "	<filter-mapping>\n";
		contents += "		<filter-name>guiceFilter</filter-name>\n";
		contents += "		<url-pattern>/*</url-pattern>\n";
		contents += "	</filter-mapping>\n\n";
		
		contents += "</web-app>";

		return new ByteArrayInputStream(contents.getBytes());
	}

}
