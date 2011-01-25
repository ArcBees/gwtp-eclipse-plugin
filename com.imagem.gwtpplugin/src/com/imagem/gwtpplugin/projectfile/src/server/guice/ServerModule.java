package com.imagem.gwtpplugin.projectfile.src.server.guice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IUpdatableFile;
import com.imagem.gwtpplugin.projectfile.src.server.handler.ActionHandler;
import com.imagem.gwtpplugin.projectfile.src.shared.action.Action;
import com.imagem.gwtpplugin.tool.Formatter;

public class ServerModule implements IUpdatableFile {

	private final String EXTENSION = ".java";
	private String guicePackage;
	private Action action;
	private ActionHandler actionHandler;

	public ServerModule(String guicePackage) {
		this.guicePackage = guicePackage;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public void setActionHandler(ActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	@Override
	public String getName() {
		return "ServerModule";
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

		contents += "import com.gwtplatform.dispatch.server.guice.HandlerModule;\n\n";

		contents += "/**\n";
		contents += " * Module qui lie les actions aux actionHandlers et aux actionValidators\n";
		contents += " */\n";
		contents += "public class " + getName() + " extends HandlerModule {\n\n";

		contents += "	@Override\n";
		contents += "	protected void configureHandlers() {\n";
		contents += "	}\n";

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
		
		if(action != null && actionHandler != null) {
			contents = SourceEditor.insertImport(contents, action.getPackage() + "." + action.getName());
			contents = SourceEditor.insertImport(contents, actionHandler.getPackage() + "." + actionHandler.getName());

			String line = "		bindHandler(" + action.getName() + ".class, " + actionHandler.getName() + ".class);\n";
			String method = "	protected void configureHandlers() {\n";
			contents = SourceEditor.addLine(contents, line, method);
		}
		
		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
