package com.imagem.gwtpplugin.projectfile.src.client.gin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IUpdatableFile;
import com.imagem.gwtpplugin.projectfile.src.client.core.Presenter;
import com.imagem.gwtpplugin.projectfile.src.client.core.View;
import com.imagem.gwtpplugin.tool.Formatter;

public class ClientModule implements IUpdatableFile {

	private final String EXTENSION = ".java";
	private String projectName;
	private String ginPackage;
	private String placePackage;
	private String resourcePackage;
	private String clientPackage;
	private Presenter presenter;
	private View view;

	public ClientModule(String projectName, String ginPackage, String placePackage, String resourcePackage, String clientPackage) {
		this.projectName = projectName;
		this.ginPackage = ginPackage;
		this.placePackage = placePackage;
		this.resourcePackage = resourcePackage;
		this.clientPackage = clientPackage;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void setView(View view) {
		this.view = view;
	}

	@Override
	public String getName() {
		return projectName + "ClientModule";
	}

	@Override
	public String getPackage() {
		return ginPackage;
	}

	@Override
	public String getPath() {
		return "src/" + ginPackage.replace('.', '/');
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.inject.Singleton;\n";
		contents += "import com.gwtplatform.mvp.client.DefaultEventBus;\n";
		contents += "import com.gwtplatform.mvp.client.DefaultProxyFailureHandler;\n";
		contents += "import com.gwtplatform.mvp.client.EventBus;\n";
		contents += "import com.gwtplatform.mvp.client.RootPresenter;\n";
		contents += "import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.PlaceManager;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.TokenFormatter;\n\n";
		contents += "import " + clientPackage + ".ActionCallback;\n";
		contents += "import " + placePackage + "." + projectName + "PlaceManager;\n";
		contents += "import " + placePackage + "." + projectName + "Tokens;\n"; // TODO Test
		contents += "import " + placePackage + ".annotation.DefaultPlace;\n"; // TODO Test
		contents += "import " + resourcePackage + ".Resources;\n";
		contents += "import " + resourcePackage + ".Translations;\n";
		contents += "import " + clientPackage + ".core.presenter.TestPresenter;\n"; // TODO Test
		contents += "import " + clientPackage + ".core.view.TestView;\n"; // TODO Test

		contents += "public class " + getName() + " extends AbstractPresenterModule {\n\n";

		contents += "	@Override\n";
		contents += "	protected void configure() {\n";
		contents += "		// Singletons\n";
		contents += "		bind(EventBus.class).to(DefaultEventBus.class).in(Singleton.class);\n";
		contents += "		bind(PlaceManager.class).to(" + projectName + "PlaceManager.class).in(Singleton.class);\n";
		contents += "		bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);\n";
		contents += "		bind(ProxyFailureHandler.class).to(DefaultProxyFailureHandler.class).in(Singleton.class);\n";
		contents += "		bind(RootPresenter.class).asEagerSingleton();\n";
		contents += "		bind(Resources.class).in(Singleton.class);\n";
		contents += "		bind(Translations.class).in(Singleton.class);\n\n";

		contents += "		requestStaticInjection(ActionCallback.class);\n\n";

		contents += "		// Constants\n";
		contents += "		// TODO bind the defaultPlace\n";
		contents += "		bindConstant().annotatedWith(DefaultPlace.class).to(" + projectName + "Tokens.test);\n\n"; // TODO Test

		contents += "		// Presenters\n";
		contents += "		bindPresenter(TestPresenter.class, TestPresenter.MyView.class, TestView.class, TestPresenter.MyProxy.class);\n"; // TODO Test
		contents += "	}\n\n";

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

		if(presenter != null && view != null) {
			contents = SourceEditor.insertImport(contents, presenter.getPackage() + "." + presenter.getName());
			contents = SourceEditor.insertImport(contents, view.getPackage() + "." + view.getName());
			String bindType = "";
			if(presenter.isWidget()) {
				bindType = "		bindPresenterWidget(" + presenter.getName() + ".class, " + presenter.getName() + ".MyView.class, " + view.getName() + ".class);\n";
			}
			else {
				bindType = "		bindPresenter(" + presenter.getName() + ".class, " + presenter.getName() + ".MyView.class, " + view.getName() + ".class, " + presenter.getName() + ".MyProxy.class);\n";
			}
			String method = "	protected void configure() {\n";
			contents = SourceEditor.addLine(contents, bindType, method);
		}

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
