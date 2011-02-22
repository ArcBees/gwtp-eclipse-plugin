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

package com.imagem.gwtpplugin.projectfile.src.client.place;

import org.eclipse.jdt.core.IField;
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
public class PlaceManager extends ProjectClass {

	private static final String C_PLACE_MANAGER_IMPL = "com.gwtplatform.mvp.client.proxy.PlaceManagerImpl";
	private static final String C_PLACE_REQUEST = "com.gwtplatform.mvp.client.proxy.PlaceRequest";
	private static final String I_EVENT_BUS = "com.google.gwt.event.shared.EventBus";
	private static final String I_TOKEN_FORMATTER = "com.gwtplatform.mvp.client.proxy.TokenFormatter";
	private static final String A_INJECT = "com.google.inject.Inject";
	
	public PlaceManager(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public PlaceManager(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);
			
			cu.createImport(C_PLACE_MANAGER_IMPL, null, null);
			String contents = "public class " + elementName + " extends PlaceManagerImpl {\n\n}";
			
			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IField createPlaceRequestField(IType annotation) throws JavaModelException {
		String fieldName = annotation.getElementName().substring(0, 1).toLowerCase() + annotation.getElementName().substring(1);
		
		cu.createImport(C_PLACE_REQUEST, null, null);
		String contents = "private final PlaceRequest " + fieldName + "Request;";
		
		return type.createField(contents, null, false, null);
	}
	
	public IMethod createConstructor(IType[] annotations, IField[] placeRequests) throws JavaModelException {
		String contents = "";
		
		cu.createImport(A_INJECT, null, null);
		contents += "@Inject\n";
		contents += "public " + type.getElementName() + "(\n";
		
		cu.createImport(I_EVENT_BUS, null, null);
		contents += "		final EventBus eventBus, \n";

		cu.createImport(I_TOKEN_FORMATTER, null, null);
		contents += "		final TokenFormatter tokenFormatter";
		
		String subContents = "";
		for(int i = 0; i < annotations.length; i++) {
			cu.createImport(annotations[i].getFullyQualifiedName(), null, null);
			String fieldName = annotations[i].getElementName().substring(0, 1).toUpperCase() + annotations[i].getElementName().substring(1);
			
			contents += ",\n		@" + annotations[i].getElementName() + " final String " + fieldName + "NameToken";
			subContents += "	this." + placeRequests[i].getElementName() + " = new PlaceRequest(" + fieldName + "NameToken);\n";
		}
		contents += ") {\n";
		contents += "	super(eventBus, tokenFormatter);\n\n";
		contents += subContents;
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createRevealDefaultPlaceMethod(IField defaultPlace) throws JavaModelException {
		String contents = "";
		
		contents += "@Override\n";
		contents += "public void revealDefaultPlace() {\n";
		contents += "	revealPlace(" + defaultPlace.getElementName() + ");\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
}
