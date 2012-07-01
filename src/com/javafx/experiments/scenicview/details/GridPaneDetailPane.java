/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javafx.experiments.scenicview.details;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

/**
 * 
 * @author aim
 */
public class GridPaneDetailPane extends DetailPane {
    Detail gapDetail;
    Detail alignmentDetail;
    Detail gridLinesVisibleDetail;
    Detail rowConstraintsDetail;
    Detail columnConstraintsDetail;

    private ListChangeListener<RowConstraints> rowListener;

    // private ListChangeListener<ColumnConstraints> columnListener;

    @Override public Class<? extends Node> getTargetClass() {
        return GridPane.class;
    }

    @Override public boolean targetMatches(final Object candidate) {
        return candidate instanceof GridPane;
    }

    @Override protected void createDetails() {
        int row = 0;
        gapDetail = addDetail("gap", "hgap/vgap:", row++);
        alignmentDetail = addDetail("alignment", "alignment:", row++);
        gridLinesVisibleDetail = addDetail("gridLinesVisible", "gridLinesVisible:", row++);
        rowConstraintsDetail = addDetail("rows", "rowConstraints:", new GridConstraintDisplay(), row++);
        columnConstraintsDetail = addDetail("columns", "columnConstraints:", new GridConstraintDisplay(), row++);

        rowListener = new ListChangeListener<RowConstraints>() {
            @Override public void onChanged(final Change<? extends RowConstraints> change) {
                updateRowConstraints();
            }
        };

        // columnListener = new ListChangeListener<ColumnConstraints>() {
        // @Override public void onChanged(Change<? extends ColumnConstraints>
        // change) {
        // updateColumnConstraints();
        // }
        // };

    }

    // need to generify
    @Override public void setTarget(final Object target) {
        if (getTarget() != null) {
            final GridPane old = (GridPane) getTarget();
            old.getRowConstraints().removeListener(rowListener);
        }
        if (target != null) {
            // GridPane gridpane = (GridPane)target;
            // MH: Looks like a temporary solution, do not understand the
            // purpose
            // gridpane.getRowConstraints().addListener(null);
        }
        super.setTarget(target);
    }

    @Override protected void updateAllDetails() {
        updateDetail("*");
        updateRowConstraints();
        updateColumnConstraints();
    }

    @Override protected void updateDetail(final String propertyName) {
        final boolean all = propertyName.equals("*") ? true : false;

        final GridPane gridpane = (GridPane) getTarget();
        if (all || propertyName.equals("hgap") || propertyName.equals("vgap")) {
            gapDetail.valueLabel.setText(gridpane != null ? (gridpane.getHgap() + " / " + gridpane.getVgap()) : "-");
            gapDetail.setIsDefault(gridpane == null || (gridpane.getHgap() == 0 && gridpane.getVgap() == 0));
            gapDetail.setSimpleSizeProperty(gridpane != null ? gridpane.hgapProperty() : null, gridpane != null ? gridpane.vgapProperty() : null);
            if (!all)
                gapDetail.updated();
            if (!all)
                return;
        }
        if (all || propertyName.equals("alignment")) {
            alignmentDetail.valueLabel.setText(gridpane != null ? gridpane.getAlignment().toString() : "-");
            alignmentDetail.setIsDefault(gridpane == null || gridpane.getAlignment() == Pos.TOP_LEFT);
            alignmentDetail.setEnumProperty(gridpane != null ? gridpane.alignmentProperty() : null, Pos.class);
            if (!all)
                alignmentDetail.updated();
            if (!all)
                return;
        }
        if (all || propertyName.equals("gridLinesVisible")) {
            gridLinesVisibleDetail.valueLabel.setText(gridpane != null ? Boolean.toString(gridpane.isGridLinesVisible()) : "-");
            gridLinesVisibleDetail.setIsDefault(gridpane == null || !gridpane.isGridLinesVisible());
            gridLinesVisibleDetail.setSimpleProperty(gridpane != null ? gridpane.gridLinesVisibleProperty() : null);
            if (!all)
                gridLinesVisibleDetail.updated();
            if (!all)
                return;
        }

    }

    private void updateColumnConstraints() {
        final GridPane gridpane = (GridPane) getTarget();
        final GridConstraintDisplay display = (GridConstraintDisplay) columnConstraintsDetail.valueNode;

        final ObservableList<ColumnConstraints> columns = gridpane != null ? gridpane.getColumnConstraints() : null;
        display.setConstraints(columns);

        if (columns != null) {
            for (int rowIndex = 1; rowIndex <= columns.size(); rowIndex++) {
                final ColumnConstraints cc = columns.get(rowIndex - 1);
                int colIndex = 0;
                display.add(new Text(Integer.toString(rowIndex - 1)), colIndex++, rowIndex);
                display.addSize(cc.getMinWidth(), rowIndex, colIndex++);
                display.addSize(cc.getPrefWidth(), rowIndex, colIndex++);
                display.addSize(cc.getMaxWidth(), rowIndex, colIndex++);
                display.add(new Text(cc.getPercentWidth() != -1 ? f.format(cc.getPercentWidth()) : "-"), colIndex++, rowIndex);
                display.addObject(cc.getHgrow(), rowIndex, colIndex++);
                display.addObject(cc.getHalignment(), rowIndex, colIndex++);
                display.add(new Text(Boolean.toString(cc.isFillWidth())), colIndex, rowIndex);
            }
        }
    }

    private void updateRowConstraints() {
        final GridPane gridpane = (GridPane) getTarget();
        final GridConstraintDisplay display = (GridConstraintDisplay) rowConstraintsDetail.valueNode;

        final ObservableList<RowConstraints> rows = gridpane != null ? gridpane.getRowConstraints() : null;
        display.setConstraints(rows);

        if (rows != null) {
            for (int rowIndex = 1; rowIndex <= rows.size(); rowIndex++) {
                final RowConstraints rc = rows.get(rowIndex - 1);
                int colIndex = 0;
                display.add(new Text(Integer.toString(rowIndex - 1)), colIndex++, rowIndex);
                display.addSize(rc.getMinHeight(), rowIndex, colIndex++);
                display.addSize(rc.getPrefHeight(), rowIndex, colIndex++);
                display.addSize(rc.getMaxHeight(), rowIndex, colIndex++);
                display.add(new Text(rc.getPercentHeight() != -1 ? f.format(rc.getPercentHeight()) : "-"), colIndex++, rowIndex);
                display.addObject(rc.getVgrow(), rowIndex, colIndex++);
                display.addObject(rc.getValignment(), rowIndex, colIndex++);
                display.add(new Text(Boolean.toString(rc.isFillHeight())), colIndex, rowIndex);
            }
        }
    }
}
