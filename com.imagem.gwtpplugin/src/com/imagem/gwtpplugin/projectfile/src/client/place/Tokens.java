/**
 * Copyright 2011 Les Systèmes Médicaux Imagem Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imagem.gwtpplugin.projectfile.src.client.place;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IUpdatableFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class Tokens implements IUpdatableFile {

	private final String EXTENSION = ".java";
	private String projectName;
	private String placePackage;
	private String token;

	@Deprecated
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

	// New Version
	private IType type;

	public Tokens(IJavaProject project, String fullyQualifiedName) throws JavaModelException {
		// TODO Create if doesn't exist
		type = project.findType(fullyQualifiedName);
	}
	
	public IType getType() {
		return type;
	}

	public IField createTokenField(String tokenName) throws JavaModelException {
		String contents = "public static final String " + tokenName.replaceAll("!", "") + " = \"" + tokenName + "\";";

		return type.createField(contents, null, false, null);
	}

	public IMethod createTokenGetter(String tokenName) throws JavaModelException {
		tokenName = tokenName.replaceAll("!", "");

		String methodName = "get" + tokenName.substring(0, 1).toUpperCase() + tokenName.substring(1) + "()";
		String contents = "public static String " + methodName + " {return " + tokenName + ";}";


		return type.createMethod(contents, null, false, null);
	}

}
