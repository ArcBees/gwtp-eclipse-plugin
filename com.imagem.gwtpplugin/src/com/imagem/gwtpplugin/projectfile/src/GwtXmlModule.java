package com.imagem.gwtpplugin.projectfile.src;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;

public class GwtXmlModule implements IProjectFile {

	private final String extension = ".gwt.xml";
	private String projectName;
	private String projectPackage;
	private String path;

	public GwtXmlModule(String projectName, String projectPackage, String path) {
		this.projectName = projectName;
		this.projectPackage = projectPackage;
		this.path = path;
	}

	@Override
	public String getName() {
		return projectName;
	}

	@Override
	public String getPackage() {
		return projectPackage;
	}
	
	@Override
	public String getPath() {
		return path;
	}
	
	@Override
	public String getExtension() {
		return extension;
	}
	
	@Override
	public InputStream openContentStream() {	
		String contents = "<?xml version='1.0' encoding='UTF-8'?>\n";
		contents += "<module rename-to='" + projectName.toLowerCase() + "'>\n";
		contents += "	<!-- Inherit the core Web Toolkit stuff.                        -->\n";
		contents += "	<inherits name='com.google.gwt.user.User'/>\n";
		contents += "	<inherits name='com.google.gwt.inject.Inject'/>\n";
		contents += "	<inherits name='com.google.gwt.resources.Resources'/>\n\n";

		contents += "	<!-- Inherit the default GWT style sheet.  You can change       -->\n";
		contents += "	<!-- the theme of your GWT application by uncommenting          -->\n";
		contents += "	<!-- any one of the following lines.                            -->\n";
		contents += "	<inherits name='com.google.gwt.user.theme.standard.Standard'/>\n";
		contents += "	<!-- <inherits name='com.google.gwt.user.theme.chrome.Chrome'/> -->\n";
		contents += "	<!-- <inherits name='com.google.gwt.user.theme.dark.Dark'/>     -->\n\n";

		contents += "	<!-- Other module inherits                                      -->\n";
		contents += "	<inherits name='com.gwtplatform.mvp.Mvp'/>\n";
		contents += "	<inherits name='com.gwtplatform.dispatch.Dispatch'/>\n\n";

		contents += "	<!-- Specify the app entry point class.                         -->\n";
		contents += "	<entry-point class='" + projectPackage + ".client." + projectName + "'/>\n\n";

		contents += "	<!-- Specify the paths for translatable code                    -->\n";
		contents += "	<source path='client'/>\n";
		contents += "	<source path='shared'/>\n\n";

		contents += "	<define-configuration-property name='gin.ginjector' is-multi-valued='false' />\n";
		contents += "	<set-configuration-property name='gin.ginjector' value='" + projectPackage + ".client.gin." + projectName + "Ginjector' />\n\n";
		
		contents += "</module>";

		return new ByteArrayInputStream(contents.getBytes());
	}
}
