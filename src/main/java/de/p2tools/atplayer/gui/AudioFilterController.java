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

package de.p2tools.atplayer.gui;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2MenuButton;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneV;
import de.p2tools.p2lib.guitools.prange.P2RangeBox;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import de.p2tools.p2lib.tools.duration.PDuration;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class AudioFilterController extends P2ClosePaneV {

    public static final int FILTER_SPACING_TEXTFILTER = 10;

    private final ScrollPane scrollPane = new ScrollPane();
    private final P2MenuButton mbChannel;
    private final P2MenuButton mbGenre;
    private final ComboBox<String> cboTheme = new ComboBox<>();
    private final ComboBox<String> cboTitle = new ComboBox<>();
    private final ComboBox<String> cboSomewhere = new ComboBox<>();
    private final Slider slTimeRange = new Slider();
    private final Label lblTimeRangeValue = new Label();
    private final P2RangeBox slDur = new P2RangeBox("Länge:", true, FilterCheck.FILTER_ALL_OR_MIN,
            FilterCheck.FILTER_DURATION_MAX_MINUTE);

    private final Button btnClearFilter = P2ButtonClearFilterFactory.getPButtonClearSmall();
    private final Button btnGoBack = new Button("");
    private final Button btnGoForward = new Button("");
    private final ProgData progData;

    public AudioFilterController() {
        super(ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON, true);
        this.progData = ProgData.getInstance();
        this.mbChannel = new P2MenuButton(progData.actFilterWorker.getActFilterSettings().channelProperty(),
                progData.worker.getAllChannelList());
        this.mbGenre = new P2MenuButton(progData.actFilterWorker.getActFilterSettings().genreProperty(),
                progData.worker.getAllGenreList());

        // Sender, Thema, ..
        initButton();
        initDaysFilter();
        initDurFilter();
        initStringFilter();
        addFilter();
    }

    private void initButton() {
        btnGoBack.setGraphic(ProgIcons.ICON_BUTTON_BACKWARD.getImageView());
        btnGoBack.setOnAction(a -> progData.actFilterWorker.goBackward());
        btnGoBack.disableProperty().bind(progData.actFilterWorker.backwardPossibleProperty().not());
        btnGoBack.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));
        btnGoForward.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnGoForward.setOnAction(a -> progData.actFilterWorker.goForward());
        btnGoForward.disableProperty().bind(progData.actFilterWorker.forwardPossibleProperty().not());
        progData.actFilterWorker.forwardPossibleProperty().addListener((v, o, n) -> System.out.println(progData.actFilterWorker.forwardPossibleProperty().getValue().toString()));
        btnGoForward.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        btnClearFilter.setOnAction(a -> {
            PDuration.onlyPing("Filter löschen");
            progData.actFilterWorker.clearFilter();
        });
    }

    private void initDaysFilter() {
        slTimeRange.setMin(FilterCheck.FILTER_ALL_OR_MIN);
        slTimeRange.setMax(FilterCheck.FILTER_TIME_RANGE_MAX_VALUE);
        slTimeRange.setShowTickLabels(true);

        slTimeRange.setMajorTickUnit(10);
        slTimeRange.setBlockIncrement(5);

        slTimeRange.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == FilterCheck.FILTER_ALL_OR_MIN) return "alles";

                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        slTimeRange.setValue(progData.actFilterWorker.getActFilterSettings().getTimeRange());
        setLabelSlider();
        progData.actFilterWorker.getActFilterSettings().timeRangeProperty().addListener(
                l -> slTimeRange.setValue(progData.actFilterWorker.getActFilterSettings().getTimeRange()));

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slTimeRange.valueProperty().addListener((o, oldV, newV) -> {
            setLabelSlider();
            if (!slTimeRange.isValueChanging()) {
                progData.actFilterWorker.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
            }
        });

        slTimeRange.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        progData.actFilterWorker.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
                    }
                }
        );
    }

    private void initDurFilter() {
        slDur.minValueProperty().bindBidirectional(progData.actFilterWorker.getActFilterSettings().minDurProperty());
        slDur.maxValueProperty().bindBidirectional(progData.actFilterWorker.getActFilterSettings().maxDurProperty());
// todo       slDur.setValuePrefix("");
    }

    private void initStringFilter() {
        //Theme
        addTextFilter(cboTheme, progData.actFilterWorker.getLastThemaTitleFilter(),
                progData.actFilterWorker.getActFilterSettings().themeProperty());

        //Title
        addTextFilter(cboTitle, progData.actFilterWorker.getLastTitleFilter(),
                progData.actFilterWorker.getActFilterSettings().titleProperty());

        //Somewhere
        addTextFilter(cboSomewhere, progData.actFilterWorker.getLastSomewhereFilter(),
                progData.actFilterWorker.getActFilterSettings().somewhereProperty());

        FilterCheckRegEx fTheme = new FilterCheckRegEx(cboTheme.getEditor());
        cboTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fTheme.checkPattern());
        FilterCheckRegEx fTitle = new FilterCheckRegEx(cboTitle.getEditor());
        cboTitle.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fTitle.checkPattern());
        FilterCheckRegEx fSomewhere = new FilterCheckRegEx(cboSomewhere.getEditor());
        cboSomewhere.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fSomewhere.checkPattern());
    }

    private void addTextFilter(ComboBox<String> cbo, ObservableList<String> items, StringProperty strProp) {
        cbo.setEditable(true);
        cbo.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cbo.setVisibleRowCount(15);
        cbo.setItems(items);
        cbo.getEditor().setText(strProp.getValue());

        cbo.getEditor().textProperty().addListener((u, o, n) -> {
            if (strProp.getValueSafe().equals(cbo.getEditor().getText())) {
                return;
            }
            strProp.setValue(cbo.getEditor().getText());
        });
        cbo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (cbo.getSelectionModel().getSelectedIndex() >= 0) {
                        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                            //dann wird erst nach "RETURN" gestartet
                            progData.actFilterWorker.getActFilterSettings().reportFilterReturn();
                        }
                    }
                }
        );

        cbo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                progData.actFilterWorker.getActFilterSettings().reportFilterReturn();
            }
        });
        cbo.getEditor().setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                cbo.getEditor().clear();
            }
        });
        strProp.addListener((u, o, n) -> cbo.valueProperty().setValue(strProp.getValueSafe()));
    }

    private VBox addSlider() {
        VBox vBox;
        vBox = new VBox(2);
        HBox h = new HBox(new Label("Zeitraum:"), P2GuiTools.getHBoxGrower(), lblTimeRangeValue);
        vBox.getChildren().addAll(h, slTimeRange);
        getChildren().addAll(vBox);
        return vBox;
    }

    private void setLabelSlider() {
        final String txtAll = "alles";

        int i = (int) slTimeRange.getValue();
        String tNr = i + "";

        if (i == FilterCheck.FILTER_ALL_OR_MIN) {
            lblTimeRangeValue.setText(txtAll);
        } else {
            lblTimeRangeValue.setText(tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }

    private void addFilter() {
        VBox vBoxAll = new VBox();
        vBoxAll.setPadding(new Insets(P2LibConst.PADDING));
        vBoxAll.setSpacing(FILTER_SPACING_TEXTFILTER);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vBoxAll);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getVBoxAll().getChildren().add(scrollPane);

        VBox vBox;
        vBox = addTxt("Sender", mbChannel);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        vBoxAll.getChildren().add(vBox);

        vBox = addTxt("Genre", mbGenre);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        vBoxAll.getChildren().add(vBox);

        vBox = addTxt("Thema", cboTheme);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        vBoxAll.getChildren().add(vBox);

        vBox = addTxt("Titel", cboTitle);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        vBoxAll.getChildren().add(vBox);

        vBox = addTxt("Irgendwo", cboSomewhere);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        vBoxAll.getChildren().add(vBox);

        final Button btnHelpFilter = P2Button.helpButton(progData.primaryStage, "Infos über die Filter",
                HelpText.FILTER_INFO);


        //zweite Zeile
        vBox = addSlider();
        vBoxAll.getChildren().add(vBox);
        GridPane.setHgrow(vBox, Priority.ALWAYS);

        vBox = slDur;
        vBoxAll.getChildren().add(vBox);
        GridPane.setHgrow(vBox, Priority.ALWAYS);

        CheckBox chkOnlyNew = new CheckBox();
        chkOnlyNew.selectedProperty().bindBidirectional(progData.actFilterWorker.getActFilterSettings().onlyNewProperty());
        HBox hBoxNew = new HBox(P2LibConst.DIST_BUTTON);
        hBoxNew.getChildren().addAll(new Label("Nur neue:"), P2GuiTools.getHBoxGrower(), chkOnlyNew);

        CheckBox chkOnlyBookmark = new CheckBox();
        chkOnlyBookmark.selectedProperty().bindBidirectional(progData.actFilterWorker.getActFilterSettings().onlyBookmarkProperty());
        HBox hBoxBookmark = new HBox(P2LibConst.DIST_BUTTON);
        hBoxBookmark.getChildren().addAll(new Label("Nur Bookmarks:"), P2GuiTools.getHBoxGrower(), chkOnlyBookmark);

        CheckBox chkNoHistory = new CheckBox();
        chkNoHistory.selectedProperty().bindBidirectional(progData.actFilterWorker.getActFilterSettings().noHistoryProperty());
        HBox hBoxNoHistory = new HBox(P2LibConst.DIST_BUTTON);
        hBoxNoHistory.getChildren().addAll(new Label("Keine gehörten:"), P2GuiTools.getHBoxGrower(), chkNoHistory);

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator1");
        sp.setMinHeight(0);
        sp.setMaxHeight(1);
        vBoxAll.getChildren().add(sp);

        VBox vBoxChk = new VBox(P2LibConst.DIST_BUTTON);
        vBoxChk.setAlignment(Pos.CENTER_RIGHT);
        vBoxChk.getChildren().addAll(hBoxNew, hBoxBookmark, hBoxNoHistory);
        vBoxAll.getChildren().add(vBoxChk);

        sp = new Separator();
        sp.getStyleClass().add("pseperator1");
        sp.setMinHeight(0);
        sp.setMaxHeight(1);
        vBoxAll.getChildren().add(sp);

        vBoxAll.getChildren().add(P2GuiTools.getVDistance(5));

        HBox hBoxClear = new HBox(P2LibConst.DIST_BUTTON);
        hBoxClear.setAlignment(Pos.CENTER_RIGHT);
        hBoxClear.getChildren().addAll(btnGoBack, btnGoForward, P2GuiTools.getHBoxGrower(), btnClearFilter, btnHelpFilter);

        vBoxAll.getChildren().addAll(hBoxClear);
    }

    private VBox addTxt(String txt, Control control) {
        VBox vBox = new VBox(2);
        vBox.setMaxWidth(Double.MAX_VALUE);
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        return vBox;
    }
}
