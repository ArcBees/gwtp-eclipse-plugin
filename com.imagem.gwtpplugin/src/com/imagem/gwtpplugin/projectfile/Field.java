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

package com.imagem.gwtpplugin.projectfile;

import org.eclipse.jdt.core.IType;

/**
 * 
 * @author Michael Renaud
 *
 */
public class Field {

	private IType type;
	private String primitive;
	private String name;
	
	public void setType(IType type) {
		this.type = type;
	}
	
	public void setPrimitiveType(String type) {
		this.primitive = type;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public IType getType() {
		return type;
	}
	
	public String getPrimitiveType() {
		return primitive;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isPrimitiveType() {
		return primitive != null && !primitive.isEmpty();
	}
	
	/*public boolean isPrimaryType() {
		if(qualifiedType.equals("boolean") || qualifiedType.equals("byte") || qualifiedType.equals("short") || qualifiedType.equals("int") || 
				qualifiedType.equals("long") || qualifiedType.equals("float") || qualifiedType.equals("double") || qualifiedType.equals("char"))
			return true;
		return false;
	}*/
}
