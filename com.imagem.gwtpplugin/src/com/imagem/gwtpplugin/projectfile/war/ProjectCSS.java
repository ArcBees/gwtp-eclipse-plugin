package com.imagem.gwtpplugin.projectfile.war;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;

public class ProjectCSS implements IProjectFile {

	private final String EXTENSION = ".css";
	private String projectName;
	private String path;
	
	public ProjectCSS(String projectName, String path) {
		this.projectName = projectName;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return projectName;
	}

	@Override
	public String getPackage() {
		return "";
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
		String contents = "/** Add css rules here for your application. **/\n\n";

		return new ByteArrayInputStream(contents.getBytes());
	}
}
