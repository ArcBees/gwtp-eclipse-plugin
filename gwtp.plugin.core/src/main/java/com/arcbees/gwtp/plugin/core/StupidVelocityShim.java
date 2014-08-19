/**
 * Copyright 2014 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.gwtp.plugin.core;

import java.util.Map;
import java.util.Map.Entry;

// Can't use velocity in the plugin because of https://bugs.eclipse.org/bugs/show_bug.cgi?id=396554
// This is not a replacement for velocity but is custom tuned to this projects templates.
public class StupidVelocityShim {
    private static boolean stripUnknownKeys;

    public static String evaluate(final String input, final Map<String, Object> context) {
        int ifLevel = 0;
        final StringBuilder result = new StringBuilder();
        for (String line : input.split("\n")) {
            if (ifLevel == 0) {

                if (line.trim().startsWith("#set")) {
                    final String assignment = getValueInBrackets(line).trim();
                    final String[] split = assignment.split("=");
                    String key = split[0].trim();
                    String value = split[1].trim();
                    if (value.startsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (key.startsWith("$")) {
                        key = key.substring(1);
                        context.put(key, value);
                    }

                } else if (!line.trim().startsWith("#")) {
                    String originalLine = "";
                    while (!originalLine.equals(line)) {
                        originalLine = line;
                        for (final Entry<String, Object> entry : context.entrySet()) {
                            line = line.replace("${" + entry.getKey() + "}", entry.getValue() + "");
                        }
                    }
                    line = stripRemainingKeys(line);
                    if (!line.trim().isEmpty() || (originalLine.equals(line))) {
                        result.append(line).append("\n");
                    }
                }
            }
            if (line.trim().startsWith("#if")) {
                final String property = getValueInBrackets(line).trim();
                if (property.startsWith("$")) {
                    if (!context.containsKey(property.substring(1))) {
                        ifLevel += 1;
                        continue;
                    }
                }

            } else if (line.trim().startsWith("#else")) {
                if (ifLevel == 1) {
                    ifLevel = 0;
                } else if (ifLevel == 0) {
                    ifLevel = 1;
                }
            } else if (line.trim().startsWith("#end")) {
                ifLevel = Math.max(0, ifLevel - 1);
            }
        }
        return result.substring(0, Math.max(0, result.length() - 1)).toString();
    }

    private static String getValueInBrackets(final String input) {
        final int first = input.indexOf("(");
        final int last = input.lastIndexOf(")");
        return input.substring(first + 1, last);
    }

    public static void setStripUnknownKeys(final boolean strip) {
        stripUnknownKeys = strip;
    }

    private static final String stripRemainingKeys(final String line) {
        if (stripUnknownKeys && line.contains("${") && line.contains("}")) {
            if (line.indexOf("${") < line.indexOf("}")) {
                return line.replace(line.substring(line.indexOf("${"), line.indexOf("}") + 1), "");
            }
        }
        return line;
    }
}
