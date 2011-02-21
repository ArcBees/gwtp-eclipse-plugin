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

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.projectfile.ProjectClass;

public class Tokens extends ProjectClass {

	public Tokens(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}

	public Tokens(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);
			
			String contents = "public class " + elementName + " {\n\n}";
			
			type = cu.createType(contents, null, false, null);
		}
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
