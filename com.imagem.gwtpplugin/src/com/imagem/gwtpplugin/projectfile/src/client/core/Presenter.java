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
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class Presenter implements IProjectFile {

	private final String EXTENSION = ".java";
	private String projectName;
	private String name;
	private String presenterPackage;
	private String tokenClass;
	private boolean isProxyStandard = false;
	private boolean isPlace = false;
	//private boolean isWidget = false;
	private String tokenName = "";
	private String gatekeeper;
	private List<Boolean> methods;

	@Deprecated
	public Presenter(String projectName, String name, String presenterPackage) {
		this.projectName = projectName;
		this.name = name;
		this.presenterPackage = presenterPackage;
	}

	public void setProxyStandard(boolean isProxyStandard) {
		this.isProxyStandard = isProxyStandard;
	}

	public void setPlace(boolean isPlace) {
		this.isPlace = isPlace;
	}

	public void setWidget(boolean isWidget) {
		this.isWidget = isWidget;
	}

	public void setToken(String tokenClass, String tokenName) {
		this.tokenClass = tokenClass;
		this.tokenName = tokenName;
	}
	
	public void setGatekeeper(String gatekeeper) {
		this.gatekeeper = gatekeeper;
	}
	
	public void setMethodStubs(List<Boolean> methods) {
		this.methods = methods;
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
		return !isProxyStandard;
	}

	public boolean isWidget() {
		return isWidget;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.gwt.event.shared.EventBus;\n";
		contents += "import com.google.inject.Inject;\n";
		contents += "import com.gwtplatform.mvp.client.View;\n\n";
		if(!isWidget) {
			contents += "import com.gwtplatform.mvp.client.Presenter;\n";
			if(isProxyStandard)
				contents += "import com.gwtplatform.mvp.client.annotations.ProxyStandard;\n";
			else
				contents += "import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;\n";
			if(isPlace) {
				contents += "import com.gwtplatform.mvp.client.annotations.NameToken;\n";
				contents += "import com.gwtplatform.mvp.client.proxy.ProxyPlace;\n";
				contents += "import " + tokenClass + ";\n";
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
			if(isProxyStandard)
				contents += "	@ProxyStandard\n";
			else
				contents += "	@ProxyCodeSplit\n";
			if(isPlace) {
				contents += "	@NameToken(" + projectName + "Tokens." + tokenName + ")\n";
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
		
		if(methods.get(0)) {
			contents += "	@Override\n";
			contents += "	protected void onBind() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}
		
		if(methods.get(1)) {
			contents += "	@Override\n";
			contents += "	protected void onHide() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}
		
		if(methods.get(2)) {
			contents += "	@Override\n";
			contents += "	protected void onReset() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}
		
		if(methods.get(3)) {
			contents += "	@Override\n";
			contents += "	protected void onReveal() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}
		
		if(methods.get(4)) {
			contents += "	@Override\n";
			contents += "	protected void onUnbind() {\n";
			contents += "		super.onBind();\n";
			contents += "	}\n\n";
		}

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
	
	// New Version
	private static final String C_PRESENTER = "com.gwtplatform.mvp.client.Presenter";
	private static final String C_PRESENTER_WIDGET = "com.gwtplatform.mvp.client.PresenterWidget";
	private static final String C_EVENT_BUS = "com.google.gwt.event.shared.EventBus";
	private static final String I_VIEW = "com.gwtplatform.mvp.client.View";
	private static final String I_PROXY = "com.gwtplatform.mvp.client.proxy.Proxy";
	private static final String I_PROXY_PLACE = "com.gwtplatform.mvp.client.proxy.ProxyPlace";
	private static final String A_PROXY_STANDARD = "com.gwtplatform.mvp.client.annotations.ProxyStandard";
	private static final String A_PROXY_CODESPLIT = "com.gwtplatform.mvp.client.annotations.ProxyCodeSplit";
	private static final String A_NAME_TOKEN = "com.gwtplatform.mvp.client.annotations.NameToken";
	private static final String A_USE_GATEKEEPER = "com.gwtplatform.mvp.client.annotations.UseGatekeeper";
	private static final String A_INJECT = "com.google.inject.Inject";
	
	private IType type;
	private ICompilationUnit cu;
	private boolean isWidget;
	
	public Presenter(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		type = root.getJavaProject().findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
		
		isWidget = type.getSuperclassName().startsWith("PresenterWidget");
	}
	
	public Presenter(IPackageFragmentRoot root, String packageName, String elementName, boolean isWidget) throws JavaModelException {
		this.isWidget = isWidget;
		type = root.getJavaProject().findType(packageName + "." + elementName);
		if(type == null) {
			String cuName = elementName + ".java";
			
			IPackageFragment pack = root.createPackageFragment(packageName, false, null);
			ICompilationUnit cu = pack.createCompilationUnit(cuName, "", false, null);
			cu.createPackageDeclaration(packageName, null);
			
			String contents = "";
			
			if(isWidget) {
				cu.createImport(C_PRESENTER_WIDGET, null, null);
				contents += "public class " + elementName + " extends PresenterWidget<" + elementName + ".MyView> {\n\n}";
			}
			else {
				cu.createImport(C_PRESENTER, null, null);
				contents += "public class " + elementName + " extends Presenter<" + elementName + ".MyView, " + elementName + ".MyProxy> {\n\n}";
			}
				
			type = cu.createType(contents, null, false, null);
		}
		cu = type.getCompilationUnit();
	}
	
	public IType getType() {
		return type;
	}
	
	public IType createViewInterface() throws JavaModelException {
		String contents = "";
		contents += "public interface MyView extends View {\n";
		contents += "	// TODO Put your view methods here\n";
		contents += "}\n\n";
		
		cu.createImport(I_VIEW, null, null);

		type.createType(contents, null, false, null);
		return null;
	}
	
	public IType createProxyInterface(boolean isStandard) throws JavaModelException {
		String contents = "";
		
		if(isStandard) {
			cu.createImport(A_PROXY_STANDARD, null, null);
			contents += "@ProxyStandard\n";
		}
		else {
			cu.createImport(A_PROXY_CODESPLIT, null, null);
			contents += "@ProxyCodeSplit\n";
		}
		
		cu.createImport(I_PROXY, null, null);
		contents += "public interface MyProxy extends Proxy<" + type.getElementName() + "> {}";
		
		return type.createType(contents, null, false, null);
	}
	
	public IType createProxyPlaceInterface(boolean isStandard, IType tokens, String tokenName) throws JavaModelException {
		return createProxyPlaceInterface(isStandard, tokens, tokenName, null);
	}
	
	public IType createProxyPlaceInterface(boolean isStandard, IType tokens, String tokenName, IType gatekeeper) throws JavaModelException {
		String contents = "";
		
		if(isStandard) {
			cu.createImport(A_PROXY_STANDARD, null, null);
			contents += "@ProxyStandard\n";
		}
		else {
			cu.createImport(A_PROXY_CODESPLIT, null, null);
			contents += "@ProxyCodeSplit\n";
		}
		
		cu.createImport(A_NAME_TOKEN, null, null);
		cu.createImport(tokens.getFullyQualifiedName(), null, null);
		contents += "@NameToken(" + tokens.getElementName() + "." + tokenName + ")\n";
		
		if(gatekeeper != null) {
			cu.createImport(A_USE_GATEKEEPER, null, null);
			cu.createImport(gatekeeper.getFullyQualifiedName(), null, null);
			contents += "@UseGatekeeper(" + gatekeeper.getElementName() + ".class)\n";
		}
		
		cu.createImport(I_PROXY_PLACE, null, null);
		contents += "public interface MyProxy extends ProxyPlace<" + type.getElementName() + "> {}";
		
		return type.createType(contents, null, false, null);
	}
	
	public IMethod createConstructor() throws JavaModelException {
		String contents = "";
		
		cu.createImport(A_INJECT, null, null);
		contents += "@Inject\n";
		contents += "public " + type.getElementName() + "(\n";
		
		cu.createImport(C_EVENT_BUS, null, null);
		contents += "		final EventBus eventBus, \n";
		if(isWidget) {
			contents += "		final MyView view) {\n";
			contents += "	super(eventBus, view);\n";
		}
		else {
			contents += "		final MyView view, \n";
			contents += "		final MyProxy proxy) {\n";
			contents += "	super(eventBus, view, proxy);\n";
		}
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createRevealInParentMethod(IType revealEvent) throws JavaModelException {
		return createRevealInParentMethod(revealEvent, null, null);
	}
	
	public IMethod createRevealInParentMethod(IType revealEvent, IType parent, String contentSlot) throws JavaModelException {
		String contents = "";
		
		contents += "@Override\n";
		contents += "protected void revealInParent() {\n";
		
		cu.createImport(revealEvent.getFullyQualifiedName(), null, null);
		contents += "	" + revealEvent.getElementName() + ".fire(this, ";
		if(revealEvent.getElementName().equals("RevealContentEvent")) {
			cu.createImport(parent.getFullyQualifiedName(), null, null);
			contents += parent.getElementName() + "." + contentSlot + ", ";
		}
		contents += "this);\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createMethodStub(String name) throws JavaModelException {
		String contents = "";
		
		contents += "@Override\n";
		contents += "protected void " + name + "() {\n";
		contents += "	super." + name + "();\n";
		contents += "}";

		return type.createMethod(contents, null, false, null);
	}
}
