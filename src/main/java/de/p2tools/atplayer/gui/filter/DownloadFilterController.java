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

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.download.DownloadConstants;
import de.p2tools.atplayer.gui.tools.HelpText;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2MenuButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DownloadFilterController extends FilterController {

    private final P2MenuButton mbChannel;
    private final P2MenuButton mbGenre;
    private final PCboString cboTheme;
    private final PCboString cboTitle;
    private final ComboBox<String> cboState = new ComboBox<>();

    private final Spinner<Integer> spinnerAnz = new Spinner<>(1, 9, 1);
    private final Button btnClear = P2ButtonClearFilterFactory.getPButtonClearFilter();

    private final VBox vBoxFilter;
    private final ProgData progData;

    public DownloadFilterController() {
        super(ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON);
        vBoxFilter = getVBoxFilter(true);
        progData = ProgData.getInstance();

        this.mbChannel = new P2MenuButton(ProgConfig.FILTER_DOWNLOAD_CHANNEL,
                progData.worker.getAllChannelList());
        this.mbGenre = new P2MenuButton(ProgConfig.FILTER_DOWNLOAD_GENRE,
                progData.worker.getAllGenreList());
        this.cboTheme = new PCboString(progData.stringListsLists.getFilterListDownloadTheme(),
                ProgConfig.FILTER_DOWNLOAD_THEME);
        this.cboTitle = new PCboString(progData.stringListsLists.getFilterListDownloadTitle(),
                ProgConfig.FILTER_DOWNLOAD_TITLE);

        initLayout();
        initFilter();
        initNumberDownloads();
    }

    private void initFilter() {
        cboState.getItems().addAll(DownloadConstants.ALL,
                DownloadConstants.STATE_COMBO_NOT_STARTED,
                DownloadConstants.STATE_COMBO_WAITING,
                DownloadConstants.STATE_COMBO_STARTED,
                DownloadConstants.STATE_COMBO_LOADING,
                DownloadConstants.STATE_COMBO_ERROR);
        cboState.valueProperty().bindBidirectional(ProgConfig.FILTER_DOWNLOAD_STATE);
        btnClear.setOnAction(a -> clearFilter());
    }

    private void initLayout() {
        addCont("Sender", mbChannel, vBoxFilter);
        addCont("Genre", mbGenre, vBoxFilter);
        addCont("Thema", cboTheme, vBoxFilter);
        addCont("Titel", cboTitle, vBoxFilter);
        addCont("Status", cboState, vBoxFilter);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.getChildren().add(btnClear);
        hBox.setAlignment(Pos.TOP_RIGHT);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator3");
        sp.setMinHeight(0);
        vBoxFilter.getChildren().addAll(hBox, sp);

        VBox vb = new VBox(FilterController.FILTER_SPACING_TEXTFILTER);
        addCont("Gleichzeitige Downloads", spinnerAnz, vb);

        final Button btnHelp = P2Button.helpButton("Filter", HelpText.GUI_DOWNLOAD_FILTER);
        hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnHelp);
        vb.getChildren().add(hBox);

        vBoxFilter.getChildren().add(vb);
    }

    private void initNumberDownloads() {
        spinnerAnz.valueProperty().addListener((u, o, n) -> {
            ProgConfig.DOWNLOAD_MAX_DOWNLOADS.setValue(spinnerAnz.getValue());
        });
        ProgConfig.DOWNLOAD_MAX_DOWNLOADS.addListener((u, o, n) -> {
            spinnerAnz.getValueFactory().setValue(ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getValue());
        });
        spinnerAnz.getValueFactory().setValue(ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getValue());
    }

    private void clearFilter() {
        ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue("");
        ProgConfig.FILTER_DOWNLOAD_GENRE.setValue("");
        ProgConfig.FILTER_DOWNLOAD_THEME.setValue("");
        ProgConfig.FILTER_DOWNLOAD_TITLE.setValue("");
        if (cboState.getSelectionModel() != null) {
            cboState.getSelectionModel().selectFirst();
        }
    }
}
