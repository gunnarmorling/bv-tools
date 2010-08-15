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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;
import org.eclipse.jdt.ui.wizards.NewAnnotationWizardPage;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * <p>
 * An {@link NewTypeWizardPage} implementation which creates constraint annotation types as defined
 * by the Bean Validation API (JSR 303).
 * </p>
 * <p>
 * This class is based on code from {@link NewAnnotationWizardPage}.
 * </p> 
 * 
 * @author Gunnar Morling.
 */
public class NewConstraintAnnotationWizardPage extends NewAnnotationWizardPage {

	private static enum ConstraintType {
		PROPERTY_LEVEL, CLASS_LEVEL, BOTH;
	}
	
	private final static String PAGE_NAME= "NewConstraintAnnotationWizardPage"; //$NON-NLS-1$
    private final static int TYPE = NewTypeWizardPage.ANNOTATION_TYPE;
	
	private SelectionButtonDialogFieldGroup constraintTypeButtons;
	
	private SelectionButtonDialogFieldGroup furtherOptionsButtons;
	
	public NewConstraintAnnotationWizardPage() {

		setTitle(NewConstraintAnnotationWizardMessages.WizardPage_ConstraintAnnotationType);
		setDescription(NewConstraintAnnotationWizardMessages.WizardPage_CreateANewConstraintAnnotationType);
		
		setupConstraintTypeButtons();
		setupFurtherOptionsButtons();
	}

	private void setupFurtherOptionsButtons() {
		
		String[] furtherOptionButtonNames = new String[] {
			NewConstraintAnnotationWizardMessages.WizardPage_CreateInnerListAnnotation,
			NewConstraintAnnotationWizardMessages.WizardPage_AddDocumented
		};
		
		furtherOptionsButtons= new SelectionButtonDialogFieldGroup(SWT.CHECK, furtherOptionButtonNames, 1);
		furtherOptionsButtons.setLabelText(NewConstraintAnnotationWizardMessages.WizardPage_FurtherOptions);
		furtherOptionsButtons.setSelection(0, true);
		furtherOptionsButtons.setSelection(1, true);
	}

	private void setupConstraintTypeButtons() {
		String[] constraintTypeButtonNames = 
			new String[]{
				NewConstraintAnnotationWizardMessages.WizardPage_Property,
				NewConstraintAnnotationWizardMessages.WizardPage_Class,
				NewConstraintAnnotationWizardMessages.WizardPage_Both};
		
		constraintTypeButtons= new SelectionButtonDialogFieldGroup(SWT.RADIO, constraintTypeButtonNames, 4);
		constraintTypeButtons.setLabelText(NewConstraintAnnotationWizardMessages.WizardPage_ConstraintType);
		constraintTypeButtons.setSelection(0, true);
	}

	public void createControl(Composite parent) {
		
		initializeDialogUnits(parent);
		
		Composite wrapper = new Composite(parent, SWT.NONE);
		int columnCount= 4;
		setGridLayout(wrapper, columnCount);
		
		createContainerControls(wrapper, columnCount);
		createPackageControls(wrapper, columnCount);
		createEnclosingTypeControls(wrapper, columnCount);

		createSeparator(wrapper, columnCount);

		createTypeNameControls(wrapper, columnCount);
		createModifierControls(wrapper, columnCount);

		createCommentControls(wrapper, columnCount);
		enableCommentControl(true);
		
		createSeparator(wrapper, columnCount);
		
		createConstraintTypeControls(wrapper, columnCount);
		createMethodStubSelectionControls(wrapper, columnCount);
		
		setControl(wrapper);

		Dialog.applyDialogFont(wrapper);
	}
	
	protected void createConstraintTypeControls(Composite composite, int nColumns) {
		LayoutUtil.setHorizontalSpan(constraintTypeButtons.getLabelControl(composite), 1);

		Control control= constraintTypeButtons.getSelectionButtonsGroup(composite);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= nColumns - 2;
		control.setLayoutData(gd);

		DialogField.createEmptySpace(composite);
	}
	
	private void createMethodStubSelectionControls(Composite composite, int nColumns) {
		Control labelControl= furtherOptionsButtons.getLabelControl(composite);
		LayoutUtil.setHorizontalSpan(labelControl, nColumns);

		DialogField.createEmptySpace(composite);

		Control buttonGroup= furtherOptionsButtons.getSelectionButtonsGroup(composite);
		LayoutUtil.setHorizontalSpan(buttonGroup, nColumns - 1);
	}
	

	private void setGridLayout(Composite composite, int columnCount) {
		GridLayout layout= new GridLayout();
		layout.numColumns= columnCount;
		composite.setLayout(layout);
	}
	
	private ConstraintType getConstraintType() {
		
		if (constraintTypeButtons.isSelected(0)) {
			return ConstraintType.PROPERTY_LEVEL;
		}
		else if (constraintTypeButtons.isSelected(1)) {
			return ConstraintType.CLASS_LEVEL;
		}
		else {
			return ConstraintType.BOTH;
		}
	}
	
	private boolean isAddListConstraint() {
		return furtherOptionsButtons.isSelected(0);
	}
	private boolean isAddDocumentedAnnotation() {
		return furtherOptionsButtons.isSelected(1);
	}

	@Override
	protected String constructCUContent(ICompilationUnit cu,
			String typeContent, String lineDelimiter) throws CoreException {
		
		StringBuilder metaAnnotations = new StringBuilder();
		
		appendTargetMetaAnnotation(metaAnnotations, lineDelimiter);
		appendRetentionMetaAnnotation(metaAnnotations, lineDelimiter);
		appendDocumentedMetaAnnotation(metaAnnotations, lineDelimiter);
		
		metaAnnotations.append("@Constraint(validatedBy={})"); //$NON-NLS-1$
		metaAnnotations.append(lineDelimiter);
		
		metaAnnotations.append(typeContent);
		
		return super.constructCUContent(cu, metaAnnotations.toString(), lineDelimiter);
	}

	private void appendRetentionMetaAnnotation(StringBuilder metaAnnotations,
			String lineDelimiter) {
		
		metaAnnotations.append("@Retention(RetentionPolicy.RUNTIME)"); //$NON-NLS-1$
		metaAnnotations.append(lineDelimiter);
	}

	private void appendDocumentedMetaAnnotation(StringBuilder metaAnnotations,
			String lineDelimiter) {
		
		if(isAddDocumentedAnnotation()) {
			metaAnnotations.append("@Documented"); //$NON-NLS-1$
			metaAnnotations.append(lineDelimiter);
		}
	}

	private void appendTargetMetaAnnotation(StringBuilder metaAnnotations,
			String lineDelimiter) {
		
		switch(getConstraintType()) {
		case PROPERTY_LEVEL: 
			metaAnnotations.append("@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})"); //$NON-NLS-1$
			metaAnnotations.append(lineDelimiter);
			break;
		case CLASS_LEVEL: 
			metaAnnotations.append("@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})"); //$NON-NLS-1$
			metaAnnotations.append(lineDelimiter);
			break;
		case BOTH: 
			metaAnnotations.append("@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})"); //$NON-NLS-1$
			metaAnnotations.append(lineDelimiter);
		}
	}

	@Override
	protected void createTypeMembers(IType newType, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		
		imports.addImport("javax.validation.Constraint"); //$NON-NLS-1$
		imports.addImport("javax.validation.Payload"); //$NON-NLS-1$
		imports.addImport("java.lang.annotation.Retention"); //$NON-NLS-1$
		imports.addImport("java.lang.annotation.RetentionPolicy"); //$NON-NLS-1$
		imports.addImport("java.lang.annotation.Target"); //$NON-NLS-1$
		imports.addImport("java.lang.annotation.ElementType"); //$NON-NLS-1$
		
		if(isAddDocumentedAnnotation()) {
			imports.addImport("java.lang.annotation.Documented"); //$NON-NLS-1$
		}
		
		newType.createMethod("String message() default \"\";", null, false, null); //$NON-NLS-1$
		newType.createMethod("Class<?>[] groups() default {};", null, false, null); //$NON-NLS-1$
		newType.createMethod("Class<? extends Payload>[] payload() default {};", null, false, null); //$NON-NLS-1$
		
		if(isAddListConstraint()) {
			
			StringBuilder typeBuilder = new StringBuilder();
			
			appendTargetMetaAnnotation(typeBuilder, NewConstraintAnnotationWizardMessages.WizardPage_Delimiter);
			appendRetentionMetaAnnotation(typeBuilder, NewConstraintAnnotationWizardMessages.WizardPage_Delimiter);
			appendDocumentedMetaAnnotation(typeBuilder, NewConstraintAnnotationWizardMessages.WizardPage_Delimiter);
			
			newType.createType(typeBuilder.toString() + "@interface List {\n "+ newType.getElementName() + "[] value();\n}\n", null, false, null); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
}
