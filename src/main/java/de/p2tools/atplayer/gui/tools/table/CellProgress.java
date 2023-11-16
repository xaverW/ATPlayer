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

import de.p2tools.atplayer.controller.data.download.DownloadConstants;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.util.Callback;

public class CellProgress<S, T> extends TableCell<S, T> {

    public final Callback<TableColumn<DownloadData, Double>, TableCell<DownloadData, Double>> cellFactory
            = (final TableColumn<DownloadData, Double> param) -> new ProgressBarTableCell<>() {

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                DownloadData download = getTableView().getItems().get(getIndex());
                if (item <= DownloadConstants.PROGRESS_STARTED || item >= DownloadConstants.PROGRESS_FINISHED) {
                    String text = DownloadConstants.getTextProgress(download.getState(), item.doubleValue());
                    Label label = new Label(text);
                    setGraphic(label);
                }
            }
        }
    };
}
