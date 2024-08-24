/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */


package de.p2tools.atplayer.gui.tools.table;

import de.p2tools.atplayer.controller.config.ProgColorList;
import de.p2tools.p2lib.atdata.AudioData;
import javafx.scene.control.TableRow;


public class TableRowAudio<T> extends TableRow<T> {

    public TableRowAudio() {
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setStyle("");
            setTooltip(null);

        } else {
            AudioData audioData = (AudioData) item;
            if (audioData.isBookmark()) {
                setStyle(ProgColorList.AUDIO_BOOKMARK.getCssBackground());

            } else if (audioData.isShown()) {
                setStyle(ProgColorList.AUDIO_HISTORY.getCssBackground());

            } else {
                setStyle("");
            }
        }
    }
}
