package com.imagem.gwtpplugin.projectfile.src.shared.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.projectfile.Variable;
import com.imagem.gwtpplugin.tool.Formatter;

public class Result implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String actionPackage;
	private Variable[] parameters;

	public Result(String name, String actionPackage) {
		this.name = name;
		this.actionPackage = actionPackage;
	}

	public void setParameters(Variable... parameters) {
		this.parameters = parameters;
	}

	@Override
	public String getName() {
		return name + "Result";
	}

	@Override
	public String getPackage() {
		return actionPackage;
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
		// TODO model finder
		Random generator = new Random();
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.gwtplatform.dispatch.shared.Result;\n";
		contents += "\n";

		contents += "public class " + getName() + " implements Result {\n\n";

		contents += "	private static final long serialVersionUID = " + generator.nextLong() + "L;\n\n";

		for(Variable param : parameters) {
			contents += "	private " + param.toString() + ";\n";
		}
		contents += "\n";

		if(parameters.length > 0) {
			contents += "	@SuppressWarnings(\"unused\")\n";
			contents += "	private " + getName() + "() {\n";
			contents += "		// For serialization only\n";
			contents += "	}\n\n";
		}

		contents += "	public " + getName() + "(";
		String separator = "";
		for(Variable param : parameters) {
			contents += separator + param.toString();
			separator = ", ";
		}
		contents += ") {\n";
		for(Variable param : parameters) {
			contents += "		this." + param.getName() + " = " + param.getName() + ";\n";
		}
		contents += "	}\n\n";

		for(Variable param : parameters) {
			contents += "	public " + param.getType() + " get" + param.getCapName() + "() {\n";
			contents += "		return " + param.getName() + ";\n";
			contents += "	}\n\n";
		}

		contents += "}";

		for(Variable param : parameters) {
			if(!param.getImport().isEmpty())
				contents = SourceEditor.insertImport(contents, param.getImport());
		}

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
