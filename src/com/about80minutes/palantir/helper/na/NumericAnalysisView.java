package com.about80minutes.palantir.helper.na;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import jxl.common.Logger;
import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.IOUtils;

import com.about80minutes.util.FileChooserUtil;
import com.google.common.collect.Table;
import com.palantir.api.workspace.ApplicationContext;
import com.palantir.api.workspace.ApplicationInterface;
import com.palantir.api.workspace.HelperFactory;
import com.palantir.api.workspace.HelperInterface;
import com.palantir.api.workspace.PalantirFrame;
import com.palantir.api.workspace.PalantirWorkspaceContext;

/**
 * This contains the UI code of the numeric analysis helper
 */
public class NumericAnalysisView implements HelperInterface, Observer  {
	private static final Logger LOGGER = Logger.getLogger(NumericAnalysisView.class);

	private static final String RESOURCES_OWNER_ICON = "/kcalc.png";

	private HelperFactory factory;

	private JPanel panel = null;
	private JTable table = null;
	
	private NATableModel tableModel = null;

	private ExportAction exportAction = null;

	private Icon icon = null;
	private Image image = null;
	
	private NumericAnalysisController controller = null;
	
	/**
	 * Helper constructor, initialises controls and lays out interface.
	 * 
	 * @param factory a {@link com.palantir.api.workspace.HelperFactory} which
	 * created this helper
	 * @param palantirContext a {@link com.palantir.api.workspace.PalantirWorkspaceContext}
	 * to use when accessing Palantir
	 */
	public NumericAnalysisView(HelperFactory factory, PalantirWorkspaceContext palantirContext) {
		this.factory = factory;
		
		controller = new NumericAnalysisController(palantirContext);
		controller.addObserver(this);

		panel = new JPanel(new BorderLayout());
		
		tableModel = new NATableModel();
		table = new JTable(tableModel);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);

		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		exportAction = new ExportAction("Export");
		JButton exportButton = new JButton(exportAction);
		
		JPanel southBox = new JPanel(new MigLayout("insets 5 5 5 5"));
		southBox.add(exportButton);
		southBox.setBackground(new Color(120, 120, 120)); //add as separate panel to 
		
		panel.add(southBox, BorderLayout.SOUTH);
	}
	
	/**
	 * Implementation of declared method, this returns the default screen
	 * position for this helper
	 * 
	 * @return a {@link java.lang.String} containing the default location for
	 * sthe helper
	 */
	public String getDefaultPosition() {
		return BorderLayout.SOUTH;
	}

	/**
	 * Implementation of declared method, this returns the main UI component for
	 * this helper
	 * 
	 * @return a {@link javax.swing.JComponent} which is the main UI element of
	 * this helper
	 */
	public JComponent getDisplayComponent() {
		return panel;
	}

	/**
	 * Implementation of declared method, this returns the icon for this helper
	 * 
	 * @return a {@link java.awt.Image} which represents the helper icon
	 */
	public Image getFrameIcon() {
		if(image == null) {
			image = this.retrieveImage(RESOURCES_OWNER_ICON);
		}
		return image;
	}

	/**
	 * Implementation of declared method, this returns an icon for this helper
	 * 
	 * @return a {@link javax.swing.Icon} to be used by this helper
	 */
	public Icon getIcon() {
		if (icon == null) {
			icon = new ImageIcon(this.getFrameIcon());
		}
		return icon;
	}

	/**
	 * Utility method for obtaining an image by name
	 * 
	 * @param name a {@link java.lang.String} containing the name of the image
	 * 
	 * @return an {@link java.awt.Image} object
	 */
	private Image retrieveImage(String name) {
		Image tmpImage = null;
		try {
			tmpImage = new ImageIcon(ImageIO.read(NumericAnalysisView.class.getResource(name))).getImage();
		} catch (Exception e) {
			LOGGER.error(String.format("Error retrieving image: %s",name),  e);
		}
		return tmpImage;
	}

	/**
	 * Returns the factory that created this helper
	 * 
	 * @return a {@link com.palantir.api.workspace.HelperFactory} that created
	 * this helper
	 */
	public HelperFactory getFactory() {
		return factory;
	}

	/**
	 * Returns the title for this helper
	 * 
	 * @return a {@link java.lang.String} containing the title of this helper
	 */
	public String getTitle() {
		return "Numeric Analysis";
	}

	/**
	 * Null implementation of defined method.
	 * 
	 * @param constraint a {@link java.lang.String} that contains the constraint
	 */
	public void setConstraint(String constraint) {
		// do nothing
	}

	/**
	 * Null implementation of defined method.
	 * 
	 * @param window a {@link com.palantir.api.workspace.PalantirFrame} to which
	 * this helper is added
	 * @param tab a {@link com.palantir.api.workspace.ApplicationContext} that
	 * can be used by this helper
	 */
	public void setOwners(PalantirFrame window, ApplicationContext context) {
		// do nothing
	}

	/**
	 * Implementation of defined method, this handles the situation where the
	 * helper is removed from view.
	 * 
	 * @param ai a {@link com.palantir.api.workspace.ApplicationInterface} which
	 * can be used by this method. 
	 */
	public void dispose(ApplicationInterface ai) {
		ai.getSelectionAgent().getSelectionAgentSupport().removeSelectionAgentListener(controller.getNASelectionAgent());
	}

	/**
	 * Register any listeners used by this helper
	 * 
	 * @param ai a {@link com.palantir.api.workspace.ApplicationInterface}
	 */
	public void initialize(ApplicationInterface ai) {
		ai.getSelectionAgent().getSelectionAgentSupport().addSelectionAgentListener(controller.getNASelectionAgent());
	}
	
	/**
	 * Implementation of update method, this is called when an Observable
	 * notifies this observer
	 * 
	 * @param observable an {@link java.util.Observable} that has been updated
	 * in some way
	 * @param updateData an {@link java.lang.Object} which contains some data
	 * for the update 
	 */
	@SuppressWarnings("unchecked")
	public void update(Observable obsevable, Object updateData) {
		tableModel.setTableData((Table<String, String, String>)updateData);
	}

	/**
	 * Action for exporting some data from the helper
	 */
	@SuppressWarnings("serial")
	private class ExportAction extends AbstractAction {
		
		/**
		 * Constructor for this action
		 * 
		 * @param title a {@link java.lang.String} to use as the action title
		 */
		public ExportAction(String title) {
			super(title);
		}

		/**
		 * Completes the actions required by this action
		 * 
		 * @param an {@link java.awt.event.ActionEvent} to react to 
		 */
		public void actionPerformed(ActionEvent event) {
			File file = FileChooserUtil.showDialog(getDisplayComponent(), "Select File",
					"Select",
					"Select a file",
					's',
					null);
			if(file != null) {
				OutputStream output = null;
				try {
					output = new FileOutputStream(file);
					tableModel.toCSV(output);
				} catch (FileNotFoundException e) {
					LOGGER.error(String.format("file: %s not found",file.getName()), e);
					//report error
				} finally {
					if(output != null) {
						IOUtils.closeQuietly(output);
					}
				}
			}
		}
	}
}
