package com.imagem.gwtpplugin.projectfile.src.client.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class Presenter implements IProjectFile {

	private final String EXTENSION = ".java";
	private String projectName;
	private String name;
	private String presenterPackage;
	private String placePackage;

	private boolean isCodeSplit = false;
	private boolean isPlace = false;
	private boolean isWidget = false;
	private String token = "";
	private String gatekeeper;
	private List<Boolean> onMethods;

	public Presenter(String projectName, String name, String presenterPackage, String placePackage) {
		this.projectName = projectName;
		this.name = name;
		this.presenterPackage = presenterPackage;
		this.placePackage = placePackage;
	}

	public void setCodeSplit(boolean isCodeSplit) {
		this.isCodeSplit = isCodeSplit;
	}

	public void setPlace(boolean isPlace) {
		this.isPlace = isPlace;
	}

	public void setWidget(boolean isWidget) {
		this.isWidget = isWidget;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public void setGatekeeper(String gatekeeper) {
		this.gatekeeper = gatekeeper;
	}
	
	public void setOnMethods(List<Boolean> onMethods) {
		this.onMethods = onMethods;
	}

	@Override
	public String getName() {
		return name + "Presenter";
	}

	@Override
	public String getPackage() {
		return presenterPackage;
	}

	@Override
	public String getPath() {
		return "src/" + getPackage().replace('.', '/');
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	public boolean isCodeSplit() {
		return isCodeSplit;
	}

	public boolean isWidget() {
		return isWidget;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.inject.Inject;\n";
		contents += "import com.gwtplatform.mvp.client.EventBus;\n";
		contents += "import com.gwtplatform.mvp.client.View;\n\n";
		if(!isWidget) {
			contents += "import com.gwtplatform.mvp.client.Presenter;\n";
			if(isCodeSplit)
				contents += "import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;\n";
			else
				contents += "import com.gwtplatform.mvp.client.annotations.ProxyStandard;\n";
			if(isPlace) {
				contents += "import com.gwtplatform.mvp.client.annotations.NameToken;\n";
				contents += "import com.gwtplatform.mvp.client.proxy.ProxyPlace;\n";
				contents += "import " + placePackage + "." + projectName + "Tokens;\n";
				if(gatekeeper != null && !gatekeeper.isEmpty()) {
					contents += "import com.gwtplatform.mvp.client.annotations.UseGatekeeper;\n";
					contents += "import " + gatekeeper + ";\n";
				}
			}
			else
				contents += "import com.gwtplatform.mvp.client.proxy.Proxy;\n";
		}
		else
			contents += "import com.gwtplatform.mvp.client.PresenterWidget;\n";

		if(!isWidget)
			contents += "public class " + getName() + " extends Presenter<" + getName() + ".MyView, " + getName() + ".MyProxy> {\n\n";
		else
			contents += "public class " + getName() + " extends PresenterWidget<" + getName() + ".MyView> {\n\n";

		contents += "	public interface MyView extends View {\n";
		contents += "		// TODO Put your view methods here\n";
		contents += "	}\n\n";

		if(!isWidget) {
			if(isCodeSplit)
				contents += "	@ProxyCodeSplit\n";
			else
				contents += "	@ProxyStandard\n";
			if(isPlace) {
				contents += "	@NameToken(" + projectName + "Tokens." + token + ")\n";
				if(gatekeeper != null && !gatekeeper.isEmpty()) {
					String[] gatekeeperSplit = gatekeeper.split("\\.");
					contents += "	@UseGatekeeper(" + gatekeeperSplit[gatekeeperSplit.length - 1] + ".class)\n";
				}
				contents += "	public interface MyProxy extends ProxyPlace<" + getName() + "> {}\n\n";
			}
			else
				contents += "	public interface MyProxy extends Proxy<" + getName() + "> {}\n\n";
		}

		contents += "	@Inject\n";
		contents += "	public " + getName() + "(\n";
		contents += "			final EventBus eventBus, \n";
		if(!isWidget) {
			contents += "			final MyView view, \n";
			contents += "			final MyProxy proxy) {\n";
			contents += "		super(eventBus, view, proxy);\n";
		}
		else {
			contents += "			final MyView view) {\n";
			contents += "		super(eventBus, view);\n";
		}
		contents += "	}\n\n";

		if(!isWidget) {
			contents += "	@Override\n";
			contents += "	protected void revealInParent() {\n";
			contents += "		// TODO Put the right RevealEvent here\n";
			contents += "	}\n\n";
		}
		
		if(onMethods.get(0)) {
			contents += "	@Override\n";
			contents += "	protected void onBind() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}
		
		if(onMethods.get(1)) {
			contents += "	@Override\n";
			contents += "	protected void onHide() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}
		
		if(onMethods.get(2)) {
			contents += "	@Override\n";
			contents += "	protected void onReset() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}
		
		if(onMethods.get(3)) {
			contents += "	@Override\n";
			contents += "	protected void onReveal() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}
		
		if(onMethods.get(4)) {
			contents += "	@Override\n";
			contents += "	protected void onUnbind() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
