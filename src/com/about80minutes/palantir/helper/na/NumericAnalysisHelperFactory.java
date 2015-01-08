package com.about80minutes.palantir.helper.na;

import java.awt.Dimension;

import javax.swing.SwingConstants;

import com.palantir.api.workspace.ApplicationInterface;
import com.palantir.api.workspace.HelperInterface;
import com.palantir.api.workspace.PalantirWorkspaceContext;
import com.palantir.api.workspace.SafeAbstractHelperFactory;
import com.palantir.api.workspace.applications.GraphApplicationInterface;

/**
 * Factory for the numeric analysis helper, this creates an instance of the
 * helper
 */
public class NumericAnalysisHelperFactory extends SafeAbstractHelperFactory {

	/**
	 * Constructor, initialises the numeric analysis helper
	 */
	public NumericAnalysisHelperFactory() {
		super("Numeric Analysis",
				new String [] { GraphApplicationInterface.APPLICATION_URI },
				new Integer [] { SwingConstants.HORIZONTAL },
				new Dimension(330,500),
				null,
				"com.about80minutes.palantir.helper.na.NumericAnalysisHelperFactory");
	}

	/**
	 * Builder method, this created a version of the helper
	 * 
	 * @param context {@link com.palantir.api.workspace.PalantirWorkspaceContext}
	 * to use for helper interaction
	 * @param app {@link com.palantir.api.workspace.ApplicationInterface}
	 * the interface context to pass to the helper
	 * 
	 * @return a {@link com.palantir.api.workspace.HelperInterface}
	 */
	public HelperInterface createHelper(PalantirWorkspaceContext context, ApplicationInterface app) {
		return new NumericAnalysisView(this, context);
	}
}
