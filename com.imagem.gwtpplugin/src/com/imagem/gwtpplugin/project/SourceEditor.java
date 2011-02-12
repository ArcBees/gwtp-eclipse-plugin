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

package com.imagem.gwtpplugin.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import com.imagem.gwtpplugin.projectfile.Field;

public class SourceEditor extends Creator {
	
	public static IPath getBasePath(IPath currentPath) {
		IPath basePath = currentPath.uptoSegment(2).removeFirstSegments(1);
		for(int i = 2; i < currentPath.segmentCount(); i++) {
			if(currentPath.segment(i).equals("client") || currentPath.segment(i).equals("shared") || currentPath.segment(i).equals("server"))
				break;
			else
				basePath = basePath.append(currentPath.segment(i));
		}
		return basePath;
	}

	@Deprecated
	public static Field[] getVariables(IProject project, IPath basePath, String...vars) {
		Field[] variables = new Field[vars.length];
		for(int i = 0; i < vars.length; i++) {
			variables[i] = getVariable(project, basePath, vars[i]);
		}
		return variables;
	}
	
	@Deprecated
	public static Field getVariable(IProject project, IPath basePath, String var) {
		/*IPath modelPath = basePath.append("shared").append("model");
		Field variable = new Field(var.split(" ")[0], var.split(" ")[1]);
		
		IResource res = project.findMember(modelPath.append(variable.getType() + ".java"));
		if(res != null)
			variable.setQualifiedType(toPackage(modelPath.append(variable.getType())));
		else if(variable.getType().equals("Date") || variable.getType().equals("List") || variable.getType().equals("Map"))
			variable.setQualifiedType("java.util." + variable.getType());
		
		return variable;*/
		return null;
	}

	/**
	 * Adds an import if not already present
	 * 
	 * @param source
	 * @param newImport
	 */
	// TODO simplify import insertion
	public static String insertImport(String source, String newImport) {
		if(!source.contains("import " + newImport + ";\n")) {
			List<String> lines = toLines(source);
			List<String> imports = getImports(lines);

			int firstIndex = lines.indexOf(imports.get(0)) -1;
			int lastIndex = lines.indexOf(imports.get(imports.size() - 1));

			imports.add("import " + newImport + ";\n");
			Collections.sort(imports);

			lines = updateImports(lines, imports, firstIndex, lastIndex);

			return toText(lines);
		}

		return source;
	}

	public static String formatImports(String source) {
		List<String> lines = toLines(source);
		List<String> imports = getImports(lines);

		if(!imports.isEmpty()) {
			int firstIndex = lines.indexOf(imports.get(0)) -1;
			int lastIndex = lines.indexOf(imports.get(imports.size() - 1));

			Collections.sort(imports);

			lines = updateImports(lines, imports, firstIndex, lastIndex);

			return toText(lines);
		}
		else {
			return source;
		}
	}

	public static String addLine(String source, String line, String method) {
		List<String> lines = toLines(source);
		List<String> methodLines = getMethodLines(lines, method);

		int firstIndex = lines.indexOf(method) + 1;
		//int lastIndex = lines.indexOf(methodLines.get(methodLines.size() - 1));
		int lastIndex = getLastIndex(lines, method) - 1;

		methodLines.add(line);

		lines = updateMethod(lines, methodLines, firstIndex, lastIndex);

		return toText(lines);
	}

	private static List<String> toLines(String text) {
		List<String> lines = new ArrayList<String>();
		String line = "";
		for(int i = 0; i < text.length(); i++) {
			line += text.charAt(i);
			if(text.charAt(i) == '\n') {
				lines.add(line);
				line = "";
			}
		}
		if(!line.isEmpty()) {
			lines.add(line);
		}
		return lines;
	}

	private static List<String> getImports(List<String> lines) {
		List<String> imports = new ArrayList<String>();
		for(String line : lines) {
			if(line.startsWith("import ") && line.endsWith(";\n")) {
				imports.add(line);
			}
		}
		return imports;
	}

	private static List<String> getMethodLines(List<String> lines, String method) {
		List<String> methodLines = new ArrayList<String>();
		int i = lines.indexOf(method) + 1;
		if(!lines.get(i).equals(getTabs(method) + "}\n")) {
			do {
				methodLines.add(lines.get(i++));
			} while(!lines.get(i).equals(getTabs(method) + "}\n"));
		}

		return methodLines;
	}

	private static int getLastIndex(List<String> lines, String method) {
		int i = lines.indexOf(method);
		do {
			i++;
		} while(!lines.get(i).equals(getTabs(method) + "}\n"));

		return i;
	}

	private static List<String> updateImports(List<String> lines, List<String> sortedImports, int first, int last) {
		for(int i = last; i >= first; i--) {
			lines.remove(i);
		}

		int i = first;
		String lastRoot = "";
		for(String line : sortedImports) {
			String root = getRoot(line);
			if(!lastRoot.equals(root)) {
				lastRoot = root;
				lines.add(i++, "\n");
			}
			lines.add(i++, line);
		}
		if(!lines.get(i).equals("\n"))
			lines.add(i, "\n");
		return lines;
	}

	private static List<String> updateMethod(List<String> lines, List<String> methodLines, int first, int last) {
		for(int i = last; i >= first; i--) {
			lines.remove(i);
		}

		int i = first;
		for(String line : methodLines) {
			lines.add(i++, line);
		}

		return lines;
	}

	private static String getRoot(String importLine) {
		// TODO return importLine.substring(7).split("\.")[0];
		return importLine.substring(7).replace('.', ',').split(",")[0];
	}

	private static String getTabs(String method) {
		String tabs = "";
		for(char c : method.toCharArray()) {
			if(c == '	')
				tabs += "	";
			else
				break;
		}
		return tabs;
	}

	private static String toText(List<String> lines) {
		String text = "";
		for(String line : lines) {
			text += line;
		}
		return text;
	}
}
