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
import de.p2tools.atplayer.controller.filter.AudioFilter;
import de.p2tools.atplayer.controller.filter.AudioFilterCheck;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2MenuButton;
import de.p2tools.p2lib.guitools.pcheckcombobox.P2CheckComboBox;
import de.p2tools.p2lib.guitools.prange.P2RangeBox;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.function.BooleanSupplier;

public class AudioFilterTextFilter extends VBox {

    public static final int FILTER_SPACING_TEXTFILTER = 10;

    private final P2MenuButton mbChannel;
    private final P2MenuButton mbGenre;
    private final PCboString cboTheme;
    private final PCboString cboThemeTitle;
    private final PCboString cboTitle;
    private final PCboString cboSomewhere;
    private final Slider slTimeRange = new Slider();
    private final Label lblTimeRangeValue = new Label();
    private final P2RangeBox slDur = new P2RangeBox("Länge:", true, FilterCheck.FILTER_ALL_OR_MIN,
            AudioFilterCheck.FILTER_DURATION_MAX_MINUTE);

    private final P2ToggleSwitch tglPodcast = new P2ToggleSwitch("Podcast:");
    private final Label lblOnly = new Label("Anzeigen:");
    private final P2CheckComboBox checkOnly = new P2CheckComboBox();

    private final ProgData progData;

    public AudioFilterTextFilter() {
        this.progData = ProgData.getInstance();
        this.mbChannel = new P2MenuButton(progData.filterWorker.getActFilterSettings().channelProperty(),
                progData.worker.getAllChannelList());
        this.mbGenre = new P2MenuButton(progData.filterWorker.getActFilterSettings().genreProperty(),
                progData.worker.getAllGenreList());

        final BooleanSupplier supplierReportReturn = () -> {
            progData.filterWorker.getActFilterSettings().reportFilterReturn();
            return true;
        };
        this.cboTheme = new PCboString(progData.stringListsLists.getFilterListAudioTheme(),
                progData.filterWorker.getActFilterSettings().themeProperty(), supplierReportReturn);
        this.cboThemeTitle = new PCboString(progData.stringListsLists.getFilterListAudioThemeTitle(),
                progData.filterWorker.getActFilterSettings().themeTitleProperty(), supplierReportReturn);
        this.cboTitle = new PCboString(progData.stringListsLists.getFilterListAudioTitle(),
                progData.filterWorker.getActFilterSettings().titleProperty(), supplierReportReturn);
        this.cboSomewhere = new PCboString(progData.stringListsLists.getFilterListAudioSomewhere(),
                progData.filterWorker.getActFilterSettings().somewhereProperty(), supplierReportReturn);

        // Sender, Thema, ..
        initDaysFilter();
        initDurFilter();
        addFilter();
        initPodcast();
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

        slTimeRange.setValue(progData.filterWorker.getActFilterSettings().getTimeRange());
        setLabelSlider();
        progData.filterWorker.getActFilterSettings().timeRangeProperty().addListener(
                l -> slTimeRange.setValue(progData.filterWorker.getActFilterSettings().getTimeRange()));

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slTimeRange.valueProperty().addListener((o, oldV, newV) -> {
            setLabelSlider();
            if (!slTimeRange.isValueChanging()) {
                progData.filterWorker.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
            }
        });

        slTimeRange.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        progData.filterWorker.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
                    }
                }
        );
    }

    private void initDurFilter() {
        slDur.minValueProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().minDurProperty());
        slDur.maxValueProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().maxDurProperty());
        // todo       slDur.setValuePrefix("");
    }

    private void addFilter() {
        VBox vBoxAll = new VBox();
        vBoxAll.setSpacing(FILTER_SPACING_TEXTFILTER);
        getChildren().add(vBoxAll);

        // Textfilter
        addTxt("Sender", mbChannel, vBoxAll, progData.filterWorker.getActFilterSettings().channelVisProperty());
        addTxt("Genre", mbGenre, vBoxAll, progData.filterWorker.getActFilterSettings().genreVisProperty());
        addTxt("Thema", cboTheme, vBoxAll, progData.filterWorker.getActFilterSettings().themeVisProperty());
        addTxt("Thema oder Titel", cboThemeTitle, vBoxAll, progData.filterWorker.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", cboTitle, vBoxAll, progData.filterWorker.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", cboSomewhere, vBoxAll, progData.filterWorker.getActFilterSettings().somewhereVisProperty());

        // Podcast
        vBoxAll.getChildren().add(P2GuiTools.getVDistance(5));
        vBoxAll.getChildren().add(tglPodcast);
        tglPodcast.visibleProperty().bind(progData.filterWorker.getActFilterSettings().podcastVisProperty());
        tglPodcast.managedProperty().bind(progData.filterWorker.getActFilterSettings().podcastVisProperty());

        // Zeit
        VBox vBox = addSlider();
        vBoxAll.getChildren().add(vBox);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        vBox.visibleProperty().bind(progData.filterWorker.getActFilterSettings().timeRangeVisProperty());
        vBox.managedProperty().bind(progData.filterWorker.getActFilterSettings().timeRangeVisProperty());

        // Länge
        vBox = slDur;
        vBoxAll.getChildren().add(vBox);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        vBox.visibleProperty().bind(progData.filterWorker.getActFilterSettings().durVisProperty());
        vBox.managedProperty().bind(progData.filterWorker.getActFilterSettings().durVisProperty());

        addShowAllFilter(vBoxAll);
    }

    private void initPodcast() {
        tglPodcast.setAllowIndeterminate(true);
        tglPodcast.setLabelLeft("Podcast [nur]:", "Podcast [alles]:", "Podcast [keine]:");
        tglPodcast.setTooltip(new Tooltip("Podcast [aus]: Alle Audios werden angezeigt.\n" +
                "Podcast [ein]: Es werden nur Podcasts angezeigt.\n" +
                "Podcast [invers]: Es werden keine Podcasts angezeigt."));

        setPodcast();
        tglPodcast.getCheckBox().setOnAction((mouseEvent) -> {
            if (tglPodcast.isIndeterminate()) {
                progData.filterWorker.getActFilterSettings().setPodcastOnOff(AudioFilter.PODCAST_FILTER_INVERS__SHOW_NO_POD);
            } else if (tglPodcast.isSelected()) {
                progData.filterWorker.getActFilterSettings().setPodcastOnOff(AudioFilter.PODCAST_FILTER_ON__SHOW_ONLY_POD);
            } else {
                progData.filterWorker.getActFilterSettings().setPodcastOnOff(AudioFilter.PODCAST_FILTER_OFF__SHOW_ALL);
            }
        });
    }

    private void setPodcast() {
        switch (progData.filterWorker.getActFilterSettings().podcastOnOffProperty().getValue()) {
            case AudioFilter.PODCAST_FILTER_OFF__SHOW_ALL:
                tglPodcast.setIndeterminate(false);
                tglPodcast.setSelected(false);
                break;
            case AudioFilter.PODCAST_FILTER_ON__SHOW_ONLY_POD:
                tglPodcast.setIndeterminate(false);
                tglPodcast.setSelected(true);
                break;
            case AudioFilter.PODCAST_FILTER_INVERS__SHOW_NO_POD:
                tglPodcast.setIndeterminate(true);
                tglPodcast.setSelected(false);
                break;
        }
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

    private void addTxt(String txt, Node control, VBox vBoxComplete, BooleanProperty booleanProperty) {
        VBox vBox = new VBox(2);
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        vBoxComplete.getChildren().add(vBox);

        vBox.visibleProperty().bind(booleanProperty);
        vBox.managedProperty().bind(booleanProperty);
    }

    private void addShowAllFilter(VBox vBoxAll) {
        checkOnly.setEmptyText("Alles");
        checkOnly.addItem("Nur neue", "Nur neue Audios anzeigen", progData.filterWorker.getActFilterSettings().onlyNewProperty());
        checkOnly.addItem("Nur Bookmarks", "Nur Bookmarks anzeigen", progData.filterWorker.getActFilterSettings().onlyBookmarkProperty());
        checkOnly.addItem("Keine gehörten", "Keine gehörten Audios anzeigen", progData.filterWorker.getActFilterSettings().noHistoryProperty());

        VBox vBox = new VBox(2);
        vBox.getChildren().addAll(lblOnly, checkOnly);
        vBox.visibleProperty().bind(progData.filterWorker.getActFilterSettings().onlyVisProperty());
        vBox.managedProperty().bind(progData.filterWorker.getActFilterSettings().onlyVisProperty());
        vBoxAll.getChildren().add(vBox);
    }
}
