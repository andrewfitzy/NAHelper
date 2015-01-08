package com.about80minutes.palantir.helper.na;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.about80minutes.util.NumericFunctions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.palantir.api.horizon.v1.extractor.DisplayableValue;
import com.palantir.api.horizon.v1.extractor.HValueExtractor;
import com.palantir.api.horizon.v1.extractor.HValueExtractors;
import com.palantir.api.horizon.v1.object.HObject;
import com.palantir.api.horizon.v1.object.HPropertyBaseType;
import com.palantir.api.horizon.v1.object.HPropertyType;
import com.palantir.api.workspace.PalantirWorkspaceContext;
import com.palantir.api.workspace.selection.SelectionAgentEvent;
import com.palantir.api.workspace.selection.SelectionAgentListener;
import com.palantir.services.Locator;
import com.palantir.services.interfaces.Identifiables;

/**
 * Controller class, this deals with the events thrown by NumericanalysisView
 */
public class NumericAnalysisController extends Observable {
	private static final Logger LOGGER = Logger.getLogger(NumericAnalysisController.class);
	
	private NASelectionAgent selectionAgent = null;
	private PalantirWorkspaceContext palantirContext = null;
	private Multimap<String, Float> cachedPropertyMap = null;
	
	/**
	 * Constructor for the controller
	 * 
	 * @param palantirContext a {@link com.palantir.api.workspace.PalantirWorkspaceContext}
	 * to use by this controller
	 */
	public NumericAnalysisController(PalantirWorkspaceContext palantirContext) {
		this.selectionAgent = new NASelectionAgent();
		this.palantirContext = palantirContext;
	}
	
	/**
	 * Getter to retrieve the NASelectionAgent
	 * 
	 * @return a {@link com.palantir.api.workspace.selection.SelectionAgentListener}
	 * to use for graph interactions
	 */
	public SelectionAgentListener getNASelectionAgent() {
		return this.selectionAgent;
	}
	
	/**
	 * Process the selected objects.
	 *
	 * @param items a {@link java.util.Collection} of {@link com.palantir.api.horizon.v1.object.HObject}s that are currently selected
	 */
	private void processHObjects(Collection<HObject> items) {
		//always reset the map first
		cachedPropertyMap = ArrayListMultimap.create();
		if (items != null) {
			// The propertyMap is a Mapping of the display string to all Float values which match that key
			for (HObject item : items) {
				Iterable<HPropertyType<?>> hobProps = item.getPropertyTypes();

				// Get its Properties
				for (HPropertyType<?> htype : hobProps ) {

					//Handle Properties of Number Base Type - currently doesn't
					//handle component properties
					if(htype.getPropertyBaseType() == HPropertyBaseType.NUMBER) {

						HValueExtractor<Float> propEx = HValueExtractors.newPropertyValueExtractor(htype);
						Iterable<DisplayableValue<Float>> propValues = propEx.getDisplayValues(item);

						for (DisplayableValue<Float> hold : propValues){
							String propName = palantirContext.getOntology().getPropertyTypeByUri(htype.getUri()).getDisplayName();
							cachedPropertyMap.put(propName, Float.valueOf(hold.getDisplayValue().replaceAll(",", "")));
						}
					}
				}
			}
		}
		this.processStats();
	}
	
	/**
	 * Generates the stats and caches them in a table notifying observers
	 */
	private void processStats() {
		Table<String, String, String> valueTable = HashBasedTable.create();
		for(String key : cachedPropertyMap.keySet()) {
			Collection<Float> vals = cachedPropertyMap.get(key);
			valueTable.put(key, NATableModel.COUNT_COLUMN, NumericFunctions.formatNumber(vals.size()));
			valueTable.put(key, NATableModel.MIN_COLUMN, NumericFunctions.formatNumber(Collections.min(vals)));
			valueTable.put(key, NATableModel.MAX_COLUMN, NumericFunctions.formatNumber(Collections.max(vals)));
			valueTable.put(key, NATableModel.MEAN_COLUMN, NumericFunctions.formatNumber(NumericFunctions.getMean(vals)));
			valueTable.put(key, NATableModel.MEDIAN_COLUMN, NumericFunctions.formatNumber(NumericFunctions.getMedian(vals)));
			valueTable.put(key, NATableModel.MODE_COLUMN, NumericFunctions.formatNumberList(NumericFunctions.getModes(vals)));
			valueTable.put(key, NATableModel.SUM_COLUMN, NumericFunctions.formatNumber(NumericFunctions.getSum(vals)));
			valueTable.put(key, NATableModel.RANGE_COLUMN, NumericFunctions.formatNumber(NumericFunctions.getRange(vals)));
			valueTable.put(key, NATableModel.STANDARD_DEVIATION_COLUMN, NumericFunctions.formatNumber(NumericFunctions.getStdDeviation(vals))); 
		}
		this.setChanged();
		this.notifyObservers(valueTable);
	}
	
	/**
	 * This class contains the actions that should be performed in reacting to
	 * object selection events.
	 */
	private class NASelectionAgent implements SelectionAgentListener {

		/**
		 * Null implementation, not reacting to filters being applied
		 * currently
		 * 
		 * @param event a {@link com.palantir.api.workspace.selection.SelectionAgentEvent}
		 * to react to
		 */
		public void handleFilterEvent(SelectionAgentEvent event) {
			// ignore
		}

		/**
		 * Null implementation, not reacting to updates being applied
		 * currently
		 * 
		 * @param event a {@link com.palantir.api.workspace.selection.SelectionAgentEvent}
		 * to react to
		 */
		public void handleUpdateEvent(SelectionAgentEvent event) {
			// ignore
		}

		/**
		 * Reacts to selections within the Palantir application
		 * 
		 * @param event a {@link com.palantir.api.workspace.selection.SelectionAgentEvent}
		 * to react to
		 */
		public void handleSelectionEvent(SelectionAgentEvent event) {

			final Collection<Locator> objectsToLoad = Lists.newArrayList(event.getItemGroup().getObjectLocatorsDefaultFilter());

			if (!objectsToLoad.isEmpty()) {
				final Collection<Long> selectionIDs = Identifiables.getIdList(objectsToLoad);

				//anonymous class to deal with selection in the background
				palantirContext.getMonitoredExecutorService().execute(new SwingWorker<Collection<HObject>, Void>() {

					/**
					 * Perform some actions in the background, this loads
					 * the selected objects as HObjects
					 * 
					 * @return a {@link java.util.Collection} of {@link com.palantir.api.horizon.v1.object.HObject}
					 * objects.
					 */
					@Override
					protected Collection<HObject> doInBackground() throws Exception {
						List<HObject> hobs = new ArrayList<HObject>(palantirContext.getHorizonConnection().loadHObjects(selectionIDs));							
						return hobs;
					}

					/**
					 *Perform some actions when the background execution is complete
					 */
					protected void done() {
						try {
							NumericAnalysisController.this.processHObjects(get());
						} catch (InterruptedException e) {									
							LOGGER.error("Error handling selection",  e);
						} catch (ExecutionException e) {
							LOGGER.error("Error handling selection",  e);
						}
					}
				});
			} else {
				NumericAnalysisController.this.processHObjects(null);
			}
		}
	}
}
