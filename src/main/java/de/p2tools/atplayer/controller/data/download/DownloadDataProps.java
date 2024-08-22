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

package de.p2tools.atplayer.controller.data.download;

import de.p2tools.atplayer.controller.config.ProgConst;
import de.p2tools.p2lib.configfile.config.*;
import de.p2tools.p2lib.configfile.configlist.ConfigStringList;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import de.p2tools.p2lib.mtdownload.DownloadSize;
import de.p2tools.p2lib.mtfilm.film.Data;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import de.p2tools.p2lib.tools.date.P2LDateProperty;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;

public class DownloadDataProps extends P2DataSample<DownloadData> {

    public static final String TAG = "DownloadData";
    private final ObservableList<String> urlList = FXCollections.observableArrayList(); // wenn mehrere Filme gestartet werden sollen
    private final IntegerProperty no = new SimpleIntegerProperty(ProgConst.NUMBER_NOT_EXISTS);
    private final IntegerProperty filmNo = new SimpleIntegerProperty(ProgConst.NUMBER_NOT_EXISTS);
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty genre = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final IntegerProperty state = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final IntegerProperty guiState = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final DoubleProperty progress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final DoubleProperty guiProgress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final IntegerProperty remaining = new SimpleIntegerProperty(DownloadConstants.REMAINING_NOT_STARTET);
    private final LongProperty bandwidth = new SimpleLongProperty(); // bytes per second

    private final DownloadSize downloadSize = new DownloadSize();

    private final P2LDateProperty filmDate = new P2LDateProperty(LocalDate.MIN);//zum Sortieren in der Tabelle
    private final StringProperty filmTime = new SimpleStringProperty("");
    private final IntegerProperty durationMinute = new SimpleIntegerProperty(0);
    private final StringProperty filmUrl = new SimpleStringProperty(""); //in normaler Auflösung
    private final StringProperty urlWebsite = new SimpleStringProperty("");

    private final StringProperty destFileName = new SimpleStringProperty("");
    private final StringProperty destPath = new SimpleStringProperty("");
    private final StringProperty destPathFile = new SimpleStringProperty("");

    private final StringProperty source = new SimpleStringProperty(DownloadConstants.ALL);
    private final BooleanProperty placedBack = new SimpleBooleanProperty(false);
    private final BooleanProperty infoFile = new SimpleBooleanProperty(false);
    public final Property[] properties = {no, filmNo, channel, genre, theme, title, description,
            state, progress, remaining, bandwidth, downloadSize,
            filmDate, filmTime, durationMinute,
            filmUrl, urlWebsite,
            destFileName, destPath, destPathFile,
            source, placedBack, infoFile};

    DownloadDataProps() {
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "DownloadData";
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new ConfigStringList("urlList", urlList));
        list.add(new Config_intProp("no", DownloadFieldNames.DOWNLOAD_NO, no));
        list.add(new Config_intProp("filmNr", DownloadFieldNames.DOWNLOAD_FILM_NO, filmNo));
        list.add(new Config_stringProp("channel", DownloadFieldNames.DOWNLOAD_CHANNEL, channel));
        list.add(new Config_stringProp(DownloadFieldNames.DOWNLOAD_GENRE, "genre", genre));
        list.add(new Config_stringProp(DownloadFieldNames.DOWNLOAD_THEME, "theme", theme));
        list.add(new Config_stringProp("title", DownloadFieldNames.DOWNLOAD_TITLE, title));
        list.add(new Config_stringProp("description", DownloadFieldNames.DOWNLOAD_DESCRIPTION, description));

        list.add(new Config_intProp("state", DownloadFieldNames.DOWNLOAD_STATE, state));
        list.add(new Config_doubleProp("progress", DownloadFieldNames.DOWNLOAD_PROGRESS, progress));
        list.add(new Config_intProp("remaining", remaining));
        list.add(new Config_longProp("bandwidth", bandwidth));

        list.add(new Config_lDateProp("filmDate", DownloadFieldNames.DOWNLOAD_DATE, filmDate));
        list.add(new Config_stringProp("filmTime", DownloadFieldNames.DOWNLOAD_TIME, filmTime));
        list.add(new Config_intProp("durationMinute", DownloadFieldNames.DOWNLOAD_DURATION, durationMinute));
        list.add(new Config_stringProp("filmUrl", DownloadFieldNames.DOWNLOAD_FILM_URL, filmUrl));
        list.add(new Config_stringProp("urlWebsite", DownloadFieldNames.DOWNLOAD_URL_WEBSITE, urlWebsite));

        list.add(new Config_stringProp("destFileName", DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME, destFileName));
        list.add(new Config_stringProp("destPath", DownloadFieldNames.DOWNLOAD_DEST_PATH, destPath));
        list.add(new Config_stringProp("destPathFile", destPathFile));

        list.add(new Config_stringProp("source", source));
        list.add(new Config_boolProp("placedBack", placedBack));
        list.add(new Config_boolProp("infoFile", DownloadFieldNames.DOWNLOAD_INFO_FILE, infoFile));

        return list.toArray(new Config[]{});
    }

    @Override
    public int compareTo(DownloadData arg0) {
        int ret;
        if (((ret = Data.sorter.compare(getChannel(), arg0.getChannel())) == 0)) {
            if ((ret = Data.sorter.compare(getGenre(), arg0.getGenre())) == 0) {
                return Data.sorter.compare(getTheme(), arg0.getTheme());
            }
        }

        return ret;
    }

    public ObservableList<String> getUrlList() {
        return urlList;
    }

    public String getUrl() {
        if (urlList.isEmpty()) {
            return "";
        } else {
            return urlList.get(0);
        }
    }

    public void setUrl(String url) {
        urlList.setAll(url);
    }

    public LocalDate getFilmDate() {
        return filmDate.get();
    }

    public void setFilmDate(LocalDate filmDate) {
        this.filmDate.set(filmDate);
    }

    public void setFilmDate(String date) {
        LocalDate d = P2LDateFactory.fromString(date);
        this.filmDate.setValue(d);
    }

    public P2LDateProperty filmDateProperty() {
        return filmDate;
    }

    public String getFilmTime() {
        return filmTime.get();
    }

    public void setFilmTime(String filmTime) {
        this.filmTime.set(filmTime);
    }

    public StringProperty filmTimeProperty() {
        return filmTime;
    }

    public int getNo() {
        return no.get();
    }

    public void setNo(int no) {
        this.no.set(no);
    }

    public IntegerProperty noProperty() {
        return no;
    }

    public int getFilmNo() {
        return filmNo.get();
    }

    public void setFilmNo(int filmNo) {
        this.filmNo.set(filmNo);
    }

    public IntegerProperty filmNoProperty() {
        return filmNo;
    }

    public String getChannel() {
        return channel.get();
    }

    public void setChannel(String channel) {
        this.channel.set(channel);
    }

    public StringProperty channelProperty() {
        return channel;
    }

    public String getGenre() {
        return genre.get();
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public String getTheme() {
        return theme.get();
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public int getState() {
        return state.get();
    }

    public void setState(int state) {
        this.state.set(state);
        Platform.runLater(() -> guiState.setValue(state));
    }

    public IntegerProperty stateProperty() {
        return state;
    }

    public Double getProgress() {
        return progress.getValue();
    }

    public void setProgress(double progress) {
        this.progress.setValue(progress);
        Platform.runLater(() -> guiProgress.setValue(progress));
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    // GuiProps
    public int getGuiState() {
        return guiState.get();
    }

    public IntegerProperty guiStateProperty() {
        return guiState;
    }

    public double getGuiProgress() {
        return guiProgress.get();
    }

    public DoubleProperty guiProgressProperty() {
        return guiProgress;
    }

    public int getRemaining() {
        return remaining.get();
    }

    public IntegerProperty remainingProperty() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining.set(remaining);
    }

    public long getBandwidth() {
        return bandwidth.get();
    }

    public LongProperty bandwidthProperty() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth.set(bandwidth);
    }

    public void setBandwidthEnd(long bandwidth) {
        this.bandwidth.setValue(-1 * bandwidth);
    }

    public DownloadSize getDownloadSize() {
        return downloadSize;
    }

    public DownloadSize downloadSizeProperty() {
        return downloadSize;
    }

    public int getDurationMinute() {
        return durationMinute.get();
    }

    public void setDurationMinute(int durationMinute) {
        this.durationMinute.set(durationMinute);
    }

    public IntegerProperty durationMinuteProperty() {
        return durationMinute;
    }

    public String getFilmUrl() {
        return filmUrl.get();
    }

    public void setFilmUrl(String filmUrl) {
        this.filmUrl.set(filmUrl);
    }

    public StringProperty filmUrlProperty() {
        return filmUrl;
    }

    public String getUrlWebsite() {
        return urlWebsite.get();
    }

    public StringProperty urlWebsiteProperty() {
        return urlWebsite;
    }

    public void setUrlWebsite(String urlWebsite) {
        this.urlWebsite.set(urlWebsite);
    }

    public String getDestFileName() {
        return destFileName.get();
    }

    public void setDestFileName(String destFileName) {
        this.destFileName.set(destFileName);
    }

    public StringProperty destFileNameProperty() {
        return destFileName;
    }

    public String getDestPath() {
        return destPath.get();
    }

    public void setDestPath(String destPath) {
        this.destPath.set(destPath);
    }

    public StringProperty destPathProperty() {
        return destPath;
    }

    public String getDestPathFile() {
        return destPathFile.get();
    }

    public StringProperty destPathFileProperty() {
        return destPathFile;
    }

    public String getSource() {
        return source.get();
    }

    public StringProperty sourceProperty() {
        return source;
    }

    public void setSource(String source) {
        this.source.set(source);
    }

    public boolean isPlacedBack() {
        return placedBack.get();
    }

    public void setPlacedBack(boolean placedBack) {
        this.placedBack.set(placedBack);
    }

    public BooleanProperty placedBackProperty() {
        return placedBack;
    }

    public boolean isInfoFile() {
        return infoFile.get();
    }

    public void setInfoFile(boolean infoFile) {
        this.infoFile.set(infoFile);
    }

    public BooleanProperty infoFileProperty() {
        return infoFile;
    }

    public int compareTo(DownloadDataProps arg0) {
        return getChannel().compareTo(arg0.getChannel());
    }
}
