package com.imagem.gwtpplugin.projectfile.war;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.Platform;

import com.imagem.gwtpplugin.Activator;
import com.imagem.gwtpplugin.projectfile.IProjectFile;

public class Jar implements IProjectFile {
	
	private final String EXTENSION = ".jar";
	private String jarName;
	private String path;

	public Jar(String jarName, String path) {
		this.jarName = jarName;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return jarName;
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
		try {
			return Platform.getBundle(Activator.PLUGIN_ID).getEntry("/file/" + getName() + getExtension()).openStream();
		}
		catch (IOException e) {
			return null;
		}
	}
}
