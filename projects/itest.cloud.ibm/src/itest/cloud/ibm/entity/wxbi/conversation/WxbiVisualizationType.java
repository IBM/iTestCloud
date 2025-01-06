/*********************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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
package itest.cloud.ibm.entity.wxbi.conversation;

import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This enum represents a type of visualization in an answer of a question that one may submitted in a conversation.
 */
public enum WxbiVisualizationType {

	AREA("Area", "com.ibm.vis.rave2bundlearea"),
	BAR("Bar", "com.ibm.vis.rave2bundlebar"),
	BAR_STACKED("Bar Stacked", "StackedBar"),
	BOX("Box", "BoxPlot"),
	COLUMN("Column", "com.ibm.vis.rave2bundlecolumn"),
	COLUMN_STACKED("Column Stacked", "StackedColumn"),
	CROSS_TAB("CrossTab", "crosstab"), //
	DECISION_TREE("DecisionTree", "com.ibm.vis.decisiontree"),
	DIAL("Dial", "com.ibm.vis.rave2bundleradialbar"),
	DRIVER_ANALYSIS("DriverAnalysis", "com.ibm.vis.rave2comet"),
	DUAL_AXIS("DualAxis", "com.ibm.vis.rave2bundlecomposite"),
	HEATMAP("Heatmap","com.ibm.vis.rave2heat"),
	LINE("Line", "com.ibm.vis.rave2line"),
	LIST("List", "JQGrid"),
	MARIMEKKO("Marimekko", "com.ibm.vis.rave2marimekko"),
	PACKED_BUBBLE("Packed Bubble", "com.ibm.vis.rave2bundlepackedbubble"),
	PIE("Pie", "com.ibm.vis.rave2bundlepie"),
	POINT("Point", "com.ibm.vis.rave2point"),
	RADAR("Radar", "com.ibm.vis.rave2bundleradar"),
	SCATTER("Scatter", "com.ibm.vis.ravescatter"),
	SPIRAL("Spiral", "com.ibm.vis.spiral"),
	SUMMARY("Summary", "summary"),
	SUNBIRST("Sunburst", "com.ibm.vis.sunburst"),
	TREE_MAP("Treemap", "com.ibm.vis.rave2bundletreemap"),
	WORD_CLOUD("WordCloud", "com.ibm.vis.rave2bundlewordcloud");

/**
 * Return the visualization type represented by a given id.
 *
 * @param visualizationId The id representing the desired visualization type as {@link String}.
 *
 * @return The visualization type represented by a given id as {@link WxbiVisualizationType}.
 */
public static WxbiVisualizationType getType(final String visualizationId) {
	for (WxbiVisualizationType type : values()) {
		if(type.label.equalsIgnoreCase(visualizationId) || type.id.equalsIgnoreCase(visualizationId)) return type;
	}
	throw new ScenarioFailedError("Id '" + visualizationId + "' is unknown by this method.");
}

private final String id;

/**
 * The textual representation of the suggested question.
 */
public final String label;

WxbiVisualizationType(final String label, final String id) {
	this.label = label;
	this.id = id;
}
}