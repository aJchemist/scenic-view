/*
 * Scenic View, 
 * Copyright (C) 2012 Jonathan Giles, Ander Ruiz, Amy Fowler 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fxconnector.event;

import org.fxconnector.StageID;

public class MousePosEvent extends FXConnectorEvent {

    /**
     * 
     */
    private static final long serialVersionUID = -3472298445464561790L;
    private final String position;

    public MousePosEvent(final StageID id, final String position) {
        super(SVEventType.MOUSE_POSITION, id);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

}
