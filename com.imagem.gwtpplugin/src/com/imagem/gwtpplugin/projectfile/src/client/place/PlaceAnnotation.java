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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class PlaceAnnotation implements IProjectFile {

	private final String EXTENSION = ".java";
	private String placeName;
	private String annotationPackage;

	public PlaceAnnotation(String placeName, String annotationPackage) {
		this.placeName = placeName;
		this.annotationPackage = annotationPackage;
	}
	
	@Override
	public String getName() {
		return placeName;
	}

	@Override
	public String getPackage() {
		return annotationPackage;
	}

	@Override
	public String getPath() {
		return "src/" + annotationPackage.replace('.', '/');
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import static java.lang.annotation.ElementType.FIELD;\n";
		contents += "import static java.lang.annotation.ElementType.METHOD;\n";
		contents += "import static java.lang.annotation.ElementType.PARAMETER;\n";
		contents += "import static java.lang.annotation.RetentionPolicy.RUNTIME;\n\n";
		
		contents += "import java.lang.annotation.Retention;\n";
		contents += "import java.lang.annotation.Target;\n\n";
		
		contents += "import com.google.inject.BindingAnnotation;\n\n";
		
		contents += "@BindingAnnotation\n";
		contents += "@Target({ FIELD, PARAMETER, METHOD })\n";
		contents += "@Retention(RUNTIME)\n";
		contents += "public @interface " + getName() + " {}\n";
		


		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
