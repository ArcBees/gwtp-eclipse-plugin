/**
 * Copyright 2011 IMAGEM Solutions TI santé
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

package com.imagem.gwtpplugin.projectfile.src.client.gin;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
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
public class Ginjector extends ProjectClass {

	private static final String C_DISPATCH_ASYNC_MODULE = "com.gwtplatform.dispatch.client.gin.DispatchAsyncModule";
	private static final String C_PROVIDER = "com.google.inject.Provider";
	private static final String I_ASYNC_PROVIDER = "com.google.gwt.inject.client.AsyncProvider";
	private static final String I_PLACE_MANAGER = "com.gwtplatform.mvp.client.proxy.PlaceManager";
	private static final String A_GIN_MODULES = "com.google.gwt.inject.client.GinModules";
	private static final String I_EVENT_BUS = "com.google.gwt.event.shared.EventBus";
	private static final String I_DISPATCH_ASYNC = "com.gwtplatform.dispatch.client.DispatchAsync";
	private static final String I_PROXY_FAILURE_HANDLER = "com.gwtplatform.mvp.client.proxy.ProxyFailureHandler";
	private static final String I_GINJECTOR = "com.google.gwt.inject.client.Ginjector";
	
	public Ginjector(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public Ginjector(IPackageFragmentRoot root, String packageName, String elementName, IType presenterModule) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);
			
			cu.createImport(A_GIN_MODULES, null, null);
			cu.createImport(C_DISPATCH_ASYNC_MODULE, null, null);
			cu.createImport(presenterModule.getFullyQualifiedName(), null, null);
			String contents = "@GinModules({ DispatchAsyncModule.class, " + presenterModule.getElementName() + ".class })\n";

			cu.createImport(I_GINJECTOR, null, null);
			contents += "public interface " + elementName + " extends Ginjector {\n\n}";
			
			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IMethod[] createDefaultGetterMethods() throws JavaModelException {
		IMethod[] methods = new IMethod[4];

		cu.createImport(I_DISPATCH_ASYNC, null, null);
		methods[0] = type.createMethod("DispatchAsync getDispatcher();", null, false, null);
		
		cu.createImport(I_EVENT_BUS, null, null);
		methods[1] = type.createMethod("EventBus getEventBus();", null, false, null);
		
		cu.createImport(I_PLACE_MANAGER, null, null);
		methods[2] = type.createMethod("PlaceManager getPlaceManager();", null, false, null);
		
		cu.createImport(I_PROXY_FAILURE_HANDLER, null, null);
		methods[3] = type.createMethod("ProxyFailureHandler getProxyFailureHandler();", null, false, null);
		
		return methods;
	}
	
	public IMethod createProvider(IType presenter) throws JavaModelException {
		IAnnotation annotation = null;
		IJavaElement[] children = presenter.getChildren();
		for(IJavaElement child : children) {
			if(child instanceof IType && ((IType) child).getElementName().equals("MyProxy")) {
				annotation = ((IType) child).getAnnotation("ProxyStandard");
				break;
			}
		}
		
		cu.createImport(annotation.exists() ? C_PROVIDER : I_ASYNC_PROVIDER, null, null);
		cu.createImport(presenter.getFullyQualifiedName(), null, null);
		
		String contents = (annotation.exists() ? "Provider<" : "AsyncProvider<") + presenter.getElementName() + "> get" + presenter.getElementName() + "();";
		return type.createMethod(contents, null, false, null);
	}

}
