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

package com.imagem.gwtpplugin.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO Merge with SourceEditor
public class Formatter {

	public static String formatImports(String contents) {
		List<String> lines = getLines(contents);
		List<String> imports = getImports(lines);

		if(!imports.isEmpty()) {
			int firstIndex = lines.indexOf(imports.get(0)) -1;
			int lastIndex = lines.indexOf(imports.get(imports.size() - 1));

			Collections.sort(imports);

			lines = updateImports(lines, imports, firstIndex, lastIndex);

			return toText(lines);
		}
		else {
			return contents;
		}
	}

	private static List<String> getLines(String text) {
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

	private static String getRoot(String importLine) {
		return importLine.substring(7).replace('.', ',').split(",")[0];
	}

	private static String toText(List<String> lines) {
		String text = "";
		for(String line : lines) {
			text += line;
		}
		return text;
	}
}
