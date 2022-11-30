/*********************************************************************
 * Copyright (c) 2015, 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *********************************************************************/
package com.ibm.itest.cloud.common.core.factories;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.*;

import java.lang.reflect.Field;
import java.util.*;

import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.utils.CustomClassLoader;


/**
 * Abstract class for framework factory.
 * <p>
 * This first goal of this root class is to have a nice way to browser rapidly all
 * framework factory classes.
 * </p><p>
 * It might also offer some common implementation if at some point we turn
 * the fractory pattern to use singletons instead of static methods...
 * </p>
 */
@SuppressWarnings("unused")
public abstract class Factory {

	private final static List<Package> KNOWN_PACKAGES = new ArrayList<Package>();
	private final static Map<String, Class<?>> KNOWN_CLASSES = new HashMap<String, Class<?>>();
	private final static String[] FACTORY_CLASSES = {
		"com.ibm.team.cspf.jazz.internal.factories.JazzApplicationFactory",
		"com.ibm.team.cspf.clm.internal.factories.ClmWebPageFactory",
		"com.ibm.team.cspf.report.internal.factories.ReportWebPageFactory",
	};
	private final static Factory[] FACTORIES;
	static {
		int length = FACTORY_CLASSES.length;
		FACTORIES = new Factory[length];
		for (int i=0; i<length; i++) {
			try {
	            @SuppressWarnings("unchecked")
                Class<Factory> factoryClass = (Class<Factory>) CustomClassLoader.forName(FACTORY_CLASSES[i]);
	            Field instance = factoryClass.getField("INSTANCE");
	            FACTORIES[i] = (Factory) instance.get(null);
	            if (FACTORIES[i] != null) {
		            Map<String, Class<?>> mismatches = FACTORIES[i].mismatches;
		            if (mismatches.size() > 0) {
		            	String message = "Some classes does not match for factory "+getClassSimpleName(FACTORY_CLASSES[i]);
						println("WARNING: "+message);
		            	for (String className: mismatches.keySet()) {
		            		Class<?> mismatchClass = mismatches.get(className);
		            		println("	- class name: "+className+", class: "+mismatchClass.getSimpleName());
		            	}
		            	println("=> A refactoring surely occurred, hence class name(s) must be changed accordingly.");
		            	if (getParameterBooleanValue("checkFactory")) {
		            		throw new ScenarioFailedError(message);
		            	}
		            }
	            }
            } catch (Exception e) {
            	debugPrintln("		+ INFO: Factory '"+FACTORY_CLASSES[i]+"' has not been found.");
            }
		}
	}
	final Map<String, Class<?>> mismatches = new HashMap<String, Class<?>>();

protected Factory(final Map<String, Class<?>> classes) {
	for (String className: classes.keySet()) {
		Class<?> newClass = classes.get(className);
		Package classPackage = newClass.getPackage();
		if (!KNOWN_PACKAGES.contains(classPackage)) {
			KNOWN_PACKAGES.add(classPackage);
		}
		Class< ? > knownClass = KNOWN_CLASSES.get(className);
		if (knownClass == null) {
			if (!className.equals(newClass.getSimpleName())) {
				this.mismatches.put(className, newClass);
			}
			KNOWN_CLASSES.put(className, newClass);
		}
		else if (!knownClass.equals(newClass)) {
			throw new ScenarioFailedError("Duplicate class name '"+className+"' for two different classes: "+knownClass.getCanonicalName()+" vs. "+newClass.getCanonicalName());
		}
	}
}

protected static Class<?> getClassForName(final String className) throws ScenarioFailedError {
	Class<?> knownClass = KNOWN_CLASSES.get(className);
	if (knownClass == null) {
		for (Package javaPackage: KNOWN_PACKAGES) {
			try {
				return CustomClassLoader.forName(javaPackage.getName()+"."+className);
			}
			catch (ClassNotFoundException cnfe) {
				// skip
			}
		}
		String message = "Class '"+className+"' was not a factory known class.";
		throw new ScenarioFailedError(message);
	}
	return knownClass;
}

private static List<Package> getPackages() {
	List<Package> cpsfPackages = new ArrayList<Package>();
	for (Package javaPackage: Package.getPackages()) {
		if (javaPackage.getName().startsWith("com.ibm.itest.cloud")) {
			cpsfPackages.add(javaPackage);
		}
	}
	return cpsfPackages;
}
}
