package com.imagem.gwtpplugin.projectfile.src.client.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class TestView implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String viewPackage;
	private String presenterPackage;
	private String resourcePackage;
	private boolean useUiBinder = false;

	public TestView(String name, String viewPackage, String presenterPackage, String resourcePackage) {
		this.name = name;
		this.viewPackage = viewPackage;
		this.presenterPackage = presenterPackage;
		this.resourcePackage = resourcePackage;
	}

	public void setUiBinder(boolean useUiBinder) {
		this.useUiBinder  = useUiBinder;
	}

	@Override
	public String getName() {
		return name + "View";
	}

	@Override
	public String getPackage() {
		return viewPackage;
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

		if(useUiBinder) {
			contents += "import com.google.gwt.core.client.GWT;\n";
			contents += "import com.google.gwt.uibinder.client.UiBinder;\n";
		}
		contents += "import com.google.gwt.user.client.ui.Widget;\n";
		contents += "import com.google.inject.Inject;\n";
		contents += "import com.gwtplatform.mvp.client.ViewImpl;\n";
		contents += "import " + presenterPackage + "." + name + "Presenter;\n";
		contents += "import " + resourcePackage + ".Resources;\n";
		contents += "import " + resourcePackage + ".Translations;\n\n";
		contents += "import com.google.gwt.user.client.ui.Label;\n";

		contents += "public class " + getName() + " extends ViewImpl implements " + name + "Presenter.MyView {\n\n";

		if(useUiBinder) {
			contents += "	interface Binder extends UiBinder<Widget, " + getName() + "> { }\n";
			contents += "	protected static final Binder binder = GWT.create(Binder.class);\n";
			contents += "	private final Widget widget;\n\n";
		}

		contents += "	@Inject\n";
		contents += "	public " + getName() + "(final Resources resources, final Translations translations) {\n";
		if(useUiBinder) {
			contents += "		widget = binder.createAndBindUi(this);\n";
		}
		else {
			contents += "		// TODO Create your controls here\n";
		}
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public Widget asWidget() {\n";
		if(useUiBinder) {
			contents += "		return widget;\n";
		}
		else {
			contents += "		// TODO Return the main panel of the view\n";
			contents += "		return new Label(\"Hello world!\");\n";
		}
		contents += "	}\n\n";

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
