package com.imagem.gwtpplugin.projectfile.src.shared.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.projectfile.Variable;
import com.imagem.gwtpplugin.tool.Formatter;

public class Model implements IProjectFile {
	
	private final String EXTENSION = ".java";
	private String name;
	private String modelPackage;
	private Variable[] variables;
	
	public Model(String name, String modelPackage) {
		this.name = name;
		this.modelPackage = modelPackage;
	}
	
	public void setVariables(Variable[] variables) {
		this.variables = variables;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPackage() {
		return modelPackage;
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
		Random generator = new Random();
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import java.io.Serializable;\n\n";
		
		contents += "public class " + getName() + " implements Serializable {\n\n";
		
		contents += "	private static final long serialVersionUID = " + generator.nextLong() + "L;\n\n";
		
		for(Variable var : variables) {
			contents += "	private " + var.toString() + ";\n";
		}
		contents += "\n";
		
		contents += "	public " + getName() + "() {\n";
		contents += "		init();\n";
		contents += "	}\n\n";
		
		contents += "	public void clear() {\n";
		contents += "		init();\n";
		contents += "	}\n\n";
		
		contents += "	private void init() {\n";
		for(Variable var : variables) {
			String defaultValue = "\"\"";
			if(var.getType().equals("int"))
				defaultValue = "0";
			else if(var.getType().equals("char"))
				defaultValue = "''";
			else if(var.getType().equals("boolean"))
				defaultValue = "false";
			else if(!var.getType().equals("String"))
				defaultValue = "new " + var.getType() + "()";
			
			contents += "		set" + var.getCapName() + "(" + defaultValue + ");\n";
		}
		contents += "	}\n\n";
		
		for(Variable var : variables) {
			String methodName = "set" + var.getCapName();
			if(var.getType().equals("boolean")) {
				if(var.getName().startsWith("is"))
					methodName = "set" + var.getName().substring(2);
			}
			contents += "	public void " + methodName + "(" + var.getType() + " " + var.getName() + ") {this." + var.getName() + " = " + var.getName() + ";}\n";
		}
		contents += "\n";
		
		for(Variable var : variables) {
			String methodName = "get" + var.getCapName();
			if(var.getType().equals("boolean")) {
				if(var.getName().startsWith("is"))
					methodName = var.getName().substring(2);
			}
			contents += "	public " + var.getType() + " " + methodName + "() {return " + var.getName() + ";}\n";
		}
		contents += "\n";
		
		contents += "}";
		
		for(Variable var : variables) {
			if(!var.getImport().isEmpty())
				contents = SourceEditor.insertImport(contents, var.getImport());
		}
		
		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
