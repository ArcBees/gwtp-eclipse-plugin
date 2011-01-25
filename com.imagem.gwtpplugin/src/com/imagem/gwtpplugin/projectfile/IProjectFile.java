package com.imagem.gwtpplugin.projectfile;

import java.io.InputStream;

public interface IProjectFile {

	public String getName();
	public String getPackage();
	public String getPath();
	public String getExtension();
	public InputStream openContentStream();
}
