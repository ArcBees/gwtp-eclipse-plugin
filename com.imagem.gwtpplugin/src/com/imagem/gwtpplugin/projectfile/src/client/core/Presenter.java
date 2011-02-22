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

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.projectfile.ProjectClass;

/**
 * 
 * @author Michael Renaud
 *
 */
public class Presenter extends ProjectClass {

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
	
	private boolean isWidget;
	
	public Presenter(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
		
		isWidget = type.getSuperclassName().startsWith("PresenterWidget");
	}
	
	public Presenter(IPackageFragmentRoot root, String packageName, String elementName, boolean isWidget) throws JavaModelException {
		super(root, packageName, elementName);
		this.isWidget = isWidget;
		
		if(type == null) {
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
	}
	
	public IType createViewInterface() throws JavaModelException {
		String contents = "";
		contents += "public interface MyView extends View {\n";
		contents += "	// TODO Put your view methods here\n";
		contents += "}\n\n";
		
		cu.createImport(I_VIEW, null, null);

		return type.createType(contents, null, false, null);
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
