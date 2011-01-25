package com.imagem.gwtpplugin.projectfile;

import java.io.InputStream;

public interface IUpdatableFile extends IProjectFile {

	public InputStream updateFile(InputStream is);
}
