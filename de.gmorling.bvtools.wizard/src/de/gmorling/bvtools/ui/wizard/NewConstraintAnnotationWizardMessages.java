/*******************************************************************************
 * Copyright (c) 2010 Gunnar Morling
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Morling - initial API and implementation
 *******************************************************************************/
package de.gmorling.bvtools.ui.wizard;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for the constraint annotation wizard.
 * 
 * @author Gunnar Morling
 */
public class NewConstraintAnnotationWizardMessages extends NLS {

	private final static String BUNDLE_NAME = "de.gmorling.bvtools.ui.wizard.messages";
	
	public static String Wizard_NewConstraintAnnotationType;

	public static String WizardPage_AddDocumented;
	public static String WizardPage_Both;
	public static String WizardPage_Class;
	public static String WizardPage_ConstraintAnnotationType;
	public static String WizardPage_ConstraintType;
	public static String WizardPage_CreateANewConstraintAnnotationType;
	public static String WizardPage_CreateInnerListAnnotation;
	public static String WizardPage_Delimiter;
	public static String WizardPage_FurtherOptions;
	public static String WizardPage_Property;

	static {
		initializeMessages(BUNDLE_NAME, NewConstraintAnnotationWizardMessages.class);
	}
	
	private NewConstraintAnnotationWizardMessages() {}
}
