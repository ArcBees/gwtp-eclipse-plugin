package com.imagem.gwtpplugin.projectfile.src.client.place;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IUpdatableFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class Tokens implements IUpdatableFile {

	private final String EXTENSION = ".java";
	private String projectName;
	private String placePackage;
	private String token;

	public Tokens(String projectName, String placePackage) {
		this.projectName = projectName;
		this.placePackage = placePackage;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String getName() {
		return projectName + "Tokens";
	}

	@Override
	public String getPackage() {
		return placePackage;
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

		contents += "public class " + getName() + " {\n";
		contents += "	public static final String test = \"test\";\n"; // TODO Test
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

	@Override
	public InputStream updateFile(InputStream is) {
		String contents = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while((line = br.readLine()) != null) {
				contents += line + "\n";
			}
		}
		catch (IOException e) {
			return is;
		}

		contents = SourceEditor.addLine(contents, "	public static final String " + token + " = \"" + token + "\";\n", "public class " + getName() + " {\n");

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
