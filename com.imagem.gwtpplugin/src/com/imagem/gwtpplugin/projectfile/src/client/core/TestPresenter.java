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

package com.imagem.gwtpplugin.projectfile.src.client.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class TestPresenter implements IProjectFile {

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

	public TestPresenter(String projectName, String name, String presenterPackage, String placePackage) {
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
			}
			else
				contents += "import com.gwtplatform.mvp.client.proxy.Proxy;\n";
		}
		else
			contents += "import com.gwtplatform.mvp.client.PresenterWidget;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;\n";

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
					contents += "	@UseGatekeeper(" + gatekeeper + ".class)\n";
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
			contents += "		RevealRootLayoutContentEvent.fire(this, this);\n";
			contents += "	}\n\n";
		}

		contents += "	@Override\n";
		contents += "	protected void onReveal() {\n";
		contents += "		super.onReveal();\n";
		contents += "	}\n\n";
		
		contents += "	@Override\n";
		contents += "	protected void onBind() {\n";
		contents += "		super.onBind();\n";
		contents += "	}\n\n";

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}