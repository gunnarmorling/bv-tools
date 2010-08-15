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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.ui.wizards.NewAnnotationWizardPage;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;

/**
 * <p>
 * An {@link INewWizard} implementation which creates constraint annotation types as defined
 * by the Bean Validation API (JSR 303).
 * </p>
 * <p>
 * This class is based on code from {@link NewAnnotationCreationWizard}.
 * </p> 
 * 
 * @author Gunnar Morling.
 */
public class NewConstraintAnnotationWizard extends NewElementWizard {

    private NewConstraintAnnotationWizardPage wizardPage;

    public NewConstraintAnnotationWizard() {
		
    	setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWANNOT);
		setWindowTitle(NewConstraintAnnotationWizardMessages.Wizard_NewConstraintAnnotationType);
	}
    
    public void addPages() {
		super.addPages();
		if (wizardPage == null) {
			wizardPage= new NewConstraintAnnotationWizardPage();
			wizardPage.init(getSelection());
		}
		addPage(wizardPage);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return !wizardPage.isEnclosingTypeSelected();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		wizardPage.createType(monitor); // use the full progress monitor
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		warnAboutTypeCommentDeprecation();
		boolean res= super.performFinish();
		if (res) {
			IResource resource= wizardPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				openResource((IFile) resource);
			}
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	public IJavaElement getCreatedElement() {
		return wizardPage.getCreatedType();
	}
}
