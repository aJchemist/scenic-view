/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.fxconnector.details;

import static org.fxconnector.ConnectorUtils.getBranchCount;

import org.fxconnector.StageID;
import org.fxconnector.event.FXConnectorEventDispatcher;

import static org.fxconnector.ConnectorUtils.getBranchCount;
import javafx.scene.*;

import org.fxconnector.StageID;
import org.fxconnector.event.FXConnectorEventDispatcher;

/**
 * 
 */
class ParentDetailPaneInfo extends DetailPaneInfo {

    ParentDetailPaneInfo(final FXConnectorEventDispatcher dispatcher, final StageID stageID) {
        super(dispatcher, stageID, DetailPaneType.PARENT);
    }

    Detail needsLayoutDetail;
    Detail childCountDetail;
    Detail branchCountDetail;

    @Override Class<? extends Node> getTargetClass() {
        return Parent.class;
    }

    @Override public boolean targetMatches(final Object candidate) {
        return candidate instanceof Parent;
    }

    @Override protected void createDetails() {
        childCountDetail = addDetail("child Count", "child count:");
        branchCountDetail = addDetail("branch Count", "branch count:");
        needsLayoutDetail = addDetail("needsLayout", "needsLayout:");
    }

    @Override protected void updateAllDetails() {
        final Parent parent = (Parent) getTarget();

        childCountDetail.setValue(parent != null ? Integer.toString(parent.getChildrenUnmodifiable().size()) : "-");
        branchCountDetail.setValue(parent != null ? Integer.toString(getBranchCount(parent)) : "-");

        // No property change events on these
        needsLayoutDetail.setValue(parent != null ? Boolean.toString(parent.isNeedsLayout()) : "-");
        needsLayoutDetail.setIsDefault(parent == null || !parent.isNeedsLayout());
        sendAllDetails();
    }

    @Override protected void updateDetail(final String propertyName) {
        // no descrete "properties"
    }
}
