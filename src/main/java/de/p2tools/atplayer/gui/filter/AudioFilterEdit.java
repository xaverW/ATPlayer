/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
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

package de.p2tools.atplayer.gui.filter;

import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AudioFilterEdit extends VBox {

    private final Button btnEditFilter = new Button("");
    private final Button btnClearFilter = P2ButtonClearFilterFactory.getPButtonClearSmall();
    private final Button btnGoBack = new Button("");
    private final Button btnGoForward = new Button("");

    private final ProgData progData;

    public AudioFilterEdit() {
        this.progData = ProgData.getInstance();

        initButton();
        addFilter();
    }

    private void initButton() {
        btnGoBack.setGraphic(ProgIcons.ICON_BUTTON_BACKWARD.getImageView());
        btnGoBack.setOnAction(a -> progData.filterWorker.goBackward());
        btnGoBack.disableProperty().bind(progData.filterWorker.backwardPossibleProperty().not());
        btnGoBack.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));
        btnGoForward.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnGoForward.setOnAction(a -> progData.filterWorker.goForward());
        btnGoForward.disableProperty().bind(progData.filterWorker.forwardPossibleProperty().not());
        progData.filterWorker.forwardPossibleProperty().addListener((v, o, n) -> System.out.println(progData.filterWorker.forwardPossibleProperty().getValue().toString()));
        btnGoForward.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        btnClearFilter.setOnAction(a -> {
            P2Duration.onlyPing("Filter löschen");
            progData.filterWorker.clearFilter();
        });

        btnEditFilter.setGraphic(ProgIcons.ICON_BUTTON_EDIT.getImageView());
        btnEditFilter.setOnAction(a -> new FilterEditDialog(progData));
        btnEditFilter.setTooltip(new Tooltip("Filter ein/ausschalten"));
    }


    private void addFilter() {
        final Button btnHelpFilter = P2Button.helpButton(progData.primaryStage, "Infos über die Filter",
                HelpText.FILTER_INFO);
        HBox hBoxClear = new HBox(P2LibConst.DIST_BUTTON);
        hBoxClear.setAlignment(Pos.CENTER_RIGHT);
        hBoxClear.getChildren().addAll(btnEditFilter, btnGoBack, btnGoForward,
                P2GuiTools.getHBoxGrower(), btnClearFilter, btnHelpFilter);

        getChildren().add(hBoxClear);
    }
}
