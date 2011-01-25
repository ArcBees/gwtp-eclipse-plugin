package com.imagem.gwtpplugin.projectfile.src.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class ActionCallback implements IProjectFile {

	private final String EXTENSION = ".java";
	private String clientPackage;

	public ActionCallback(String clientPackage) {
		this.clientPackage = clientPackage;
	}

	@Override
	public String getName() {
		return "ActionCallback";
	}

	@Override
	public String getPackage() {
		return clientPackage;
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

		contents += "import com.google.gwt.user.client.rpc.AsyncCallback;\n\n";

		contents += "public abstract class " + getName() + "<T> implements AsyncCallback<T> {\n\n";

		contents += "	public " + getName() + "() {\n";
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public void onFailure(Throwable caught) {\n";
		contents += "		caught.printStackTrace();\n";
		contents += "		// TODO Put failure handling here\n";
		contents += "	}\n\n";

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
