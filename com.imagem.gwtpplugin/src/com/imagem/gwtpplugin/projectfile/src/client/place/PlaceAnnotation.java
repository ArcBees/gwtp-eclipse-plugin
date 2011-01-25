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
