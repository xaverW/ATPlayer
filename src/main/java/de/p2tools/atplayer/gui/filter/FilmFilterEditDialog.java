/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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
import de.p2tools.atplayer.controller.config.ProgConst;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;


public class FilmFilterEditDialog extends P2DialogExtra {

    private final ProgData progData;

    public FilmFilterEditDialog(ProgData progData) {
        super(progData.primaryStage, null, "Filtereinstellungen", true, true, DECO.NO_BORDER);
        this.progData = progData;

        init(true);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void make() {
        init();

        final Button btnHelp = P2Button.helpButton(getStage(), "Filtereinstellungen", HelpText.GUI_FILMS_EDIT_FILTER);
        final Button btnOk = new Button("_Ok");
        btnOk.setOnAction(event -> close());
        addOkButton(btnOk);
        addHlpButton(btnHelp);
    }

    public void init() {
        final TabPane tabPane = new TabPane();
        VBox vBox = getVBoxCont();
        vBox.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        addFilterTab(tabPane);
        addConfigTab(tabPane);
    }

    public void addFilterTab(TabPane tabPane) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, P2LibConst.PADDING_VBOX, P2LibConst.PADDING_VBOX, P2LibConst.PADDING_VBOX));
        vBox.setSpacing(15);

        Tab tab = new Tab();
        tab.setText("Filter");
        tab.setClosable(false);
        tab.setContent(vBox);
        tabPane.getTabs().add(tab);

        P2ToggleSwitch tglChannel = new P2ToggleSwitch("Sender");
        tglChannel.setMaxWidth(Double.MAX_VALUE);
        tglChannel.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().channelVisProperty());
        vBox.getChildren().add(tglChannel);

        P2ToggleSwitch tglGenre = new P2ToggleSwitch("Genre");
        tglGenre.setMaxWidth(Double.MAX_VALUE);
        tglGenre.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().genreVisProperty());
        vBox.getChildren().add(tglGenre);

        // Thema
        P2ToggleSwitch tglTheme = new P2ToggleSwitch("Thema");
        tglTheme.setMaxWidth(Double.MAX_VALUE);
        tglTheme.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().themeVisProperty());

        P2ToggleSwitch tglThemeTitle = new P2ToggleSwitch("Thema oder Titel");
        tglThemeTitle.setMaxWidth(Double.MAX_VALUE);
        tglThemeTitle.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().themeTitleVisProperty());
        vBox.getChildren().add(tglThemeTitle);

        P2ToggleSwitch tglTitle = new P2ToggleSwitch("Titel");
        tglTitle.setMaxWidth(Double.MAX_VALUE);
        tglTitle.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().titleVisProperty());
        vBox.getChildren().add(tglTitle);

        P2ToggleSwitch tglSomewhere = new P2ToggleSwitch("Irgendwo");
        tglSomewhere.setMaxWidth(Double.MAX_VALUE);
        tglSomewhere.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().somewhereVisProperty());
        vBox.getChildren().add(tglSomewhere);

        P2ToggleSwitch tglTimeRange = new P2ToggleSwitch("Zeitraum [Tage]");
        tglTimeRange.setMaxWidth(Double.MAX_VALUE);
        tglTimeRange.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().timeRangeVisProperty());
        vBox.getChildren().add(tglTimeRange);

        P2ToggleSwitch tglMinMax = new P2ToggleSwitch("Filmlänge Min/Max");
        tglMinMax.setMaxWidth(Double.MAX_VALUE);
        tglMinMax.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().durVisProperty());
        vBox.getChildren().add(tglMinMax);

        P2ToggleSwitch tglPodcast = new P2ToggleSwitch("Podcast");
        tglPodcast.setMaxWidth(Double.MAX_VALUE);
        tglPodcast.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().podcastVisProperty());
        vBox.getChildren().add(tglPodcast);

        P2ToggleSwitch tglOnly = new P2ToggleSwitch("Anzeigen");
        tglOnly.setMaxWidth(Double.MAX_VALUE);
        tglOnly.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().onlyVisProperty());
        vBox.getChildren().add(tglOnly);
    }

    public void addConfigTab(TabPane tabPane) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, P2LibConst.PADDING_VBOX, P2LibConst.PADDING_VBOX, P2LibConst.PADDING_VBOX));
        vBox.setSpacing(15);

        Tab tab = new Tab();
        tab.setText("Einstellungen");
        tab.setClosable(false);
        tab.setContent(vBox);
        tabPane.getTabs().add(tab);

        // Anlegen
        P2ToggleSwitch tglReturn = new P2ToggleSwitch("In Textfeldern Suchbeginn erst mit \"Return\" starten");
        tglReturn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILTER_RETURN);

        Label lblValue = new Label();
        lblValue.setMinWidth(Region.USE_COMPUTED_SIZE);

        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(ProgConst.SYSTEM_FILTER_MAX_WAIT_TIME);
        slider.setMinorTickCount(4);//dann 5 Teile, 500/5=alle 100 kann eingeloggt werden :)
        slider.setBlockIncrement(200);//Bedienung über die Tastatur
        slider.setMajorTickUnit(500);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ProgConfig.SYSTEM_FILTER_WAIT_TIME.setValue(Double.valueOf(slider.getValue()).intValue());
            setLabel(lblValue);
        });
        slider.setValue(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
        setLabel(lblValue);
        slider.disableProperty().bind(tglReturn.selectedProperty());
        lblValue.disableProperty().bind(tglReturn.selectedProperty());

        // Einbauen
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(0));
        gridPane.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        int row = 0;
        gridPane.add(new Label("Suchbeginn nach Eingabe verzögern:"), 0, row);
        gridPane.add(lblValue, 1, row);
        GridPane.setHalignment(lblValue, HPos.RIGHT);

        gridPane.add(slider, 0, ++row, 2, 1);
        GridPane.setHgrow(slider, Priority.ALWAYS);
        slider.setPadding(new Insets(0, 0, 0, 0));

        gridPane.add(new Label(), 0, ++row, 2, 1);
        gridPane.add(tglReturn, 0, ++row, 2, 1);


        gridPane.getColumnConstraints().addAll(
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrowRight());

        vBox.getChildren().addAll(gridPane);
    }

    private int setLabel(Label lblValue) {
        int intValue = ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue();
        lblValue.setText("  " + intValue + " ms");
        return intValue;
    }
}
