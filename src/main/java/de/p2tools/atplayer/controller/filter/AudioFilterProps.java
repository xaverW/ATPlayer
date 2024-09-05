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

package de.p2tools.atplayer.controller.filter;

import de.p2tools.atplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_boolProp;
import de.p2tools.p2lib.configfile.config.Config_intProp;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import javafx.beans.property.*;

import java.util.ArrayList;

public class AudioFilterProps extends P2DataSample<AudioFilter> implements Comparable<AudioFilter> {

    public static String TAG = "SelectedFilter";

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty channel = new SimpleStringProperty();
    private final BooleanProperty channelVis = new SimpleBooleanProperty(true);
    private final StringProperty genre = new SimpleStringProperty();
    private final BooleanProperty genreVis = new SimpleBooleanProperty(false);
    private final StringProperty theme = new SimpleStringProperty();
    private final BooleanProperty themeVis = new SimpleBooleanProperty(true);
    private final StringProperty themeTitle = new SimpleStringProperty();
    private final BooleanProperty themeTitleVis = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty();
    private final BooleanProperty titleVis = new SimpleBooleanProperty(true);
    private final StringProperty somewhere = new SimpleStringProperty();
    private final BooleanProperty somewhereVis = new SimpleBooleanProperty(false);

    private final IntegerProperty timeRange = new SimpleIntegerProperty(15);
    private final BooleanProperty timeRangeVis = new SimpleBooleanProperty(true);

    private final IntegerProperty minDur = new SimpleIntegerProperty(0);
    private final IntegerProperty maxDur = new SimpleIntegerProperty(AudioFilterCheck.FILTER_DURATION_MAX_MINUTE);
    private final BooleanProperty durVis = new SimpleBooleanProperty(true);

    private final BooleanProperty onlyVis = new SimpleBooleanProperty(true);
    private final BooleanProperty onlyNew = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyBookmark = new SimpleBooleanProperty(false);
    private final BooleanProperty noHistory = new SimpleBooleanProperty(false);

    public static final int PODCAST_FILTER_OFF__SHOW_ALL = 0; // alles
    public static final int PODCAST_FILTER_ON__SHOW_ONLY_POD = 1; // nur Podcast
    public static final int PODCAST_FILTER_INVERS__SHOW_NO_POD = 2; // keine Podcast
    private final IntegerProperty podcastOnOff = new SimpleIntegerProperty(BlacklistFilterFactory.BLACKLILST_FILTER_OFF);
    private final BooleanProperty podcastVis = new SimpleBooleanProperty(false);


    private final IntegerProperty blacklistOnOff = new SimpleIntegerProperty(BlacklistFilterFactory.BLACKLILST_FILTER_OFF);

    public BooleanProperty[] sfBooleanPropArr = {channelVis, genreVis, themeVis, themeTitleVis, titleVis, somewhereVis,
            timeRangeVis, durVis, onlyVis,
            onlyNew, onlyBookmark, noHistory, podcastVis};

    public StringProperty[] sfStringPropArr = {name, channel, genre, theme, themeTitle, title, somewhere};
    public IntegerProperty[] sfIntegerPropArr = {timeRange, minDur, maxDur, podcastOnOff, blacklistOnOff};

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("name", name));
        list.add(new Config_stringProp("channel", channel));
        list.add(new Config_boolProp("channelVis", channelVis));

        list.add(new Config_stringProp("genre", genre));
        list.add(new Config_boolProp("genreVis", genreVis));
        list.add(new Config_stringProp("theme", theme));
        list.add(new Config_boolProp("themeVis", themeVis));
        list.add(new Config_stringProp("themeTitle", themeTitle));
        list.add(new Config_boolProp("themeTitleVis", themeTitleVis));
        list.add(new Config_stringProp("title", title));
        list.add(new Config_boolProp("titleVis", titleVis));
        list.add(new Config_stringProp("somewhere", somewhere));
        list.add(new Config_boolProp("somewhereVis", somewhereVis));

        list.add(new Config_intProp("timeRange", timeRange));
        list.add(new Config_boolProp("timeRangeVis", timeRangeVis));
        list.add(new Config_boolProp("durVis", durVis));
        list.add(new Config_intProp("minDur", minDur));
        list.add(new Config_intProp("maxDur", maxDur));

        list.add(new Config_boolProp("onlyVis", onlyVis));
        list.add(new Config_boolProp("onlyNew", onlyNew));
        list.add(new Config_boolProp("onlyBookmark", onlyBookmark));
        list.add(new Config_boolProp("noHistory", noHistory));

        list.add(new Config_intProp("podcastOnOff", podcastOnOff));
        list.add(new Config_boolProp("podcastVis", podcastVis));

        list.add(new Config_intProp("blacklistOnOff", blacklistOnOff));

        return list.toArray(new Config[]{});
    }

    public boolean isSame(AudioFilter sf, boolean compareName) {
        if (sf == null) {
            return false;
        }

        for (int i = 0; i < sfBooleanPropArr.length; ++i) {
            if (!this.sfBooleanPropArr[i].getValue().equals(sf.sfBooleanPropArr[i].getValue())) {
                return false;
            }
        }
        int ii = compareName ? 0 : 1;//wenn der Name mit verglichen werden soll, dann Start bei 0, sonst 1
        for (int i = ii; i < sfStringPropArr.length; ++i) {
            if (!this.sfStringPropArr[i].getValue().equals(sf.sfStringPropArr[i].getValue())) {
                return false;
            }
        }
        for (int i = 0; i < sfIntegerPropArr.length; ++i) {
            if (!this.sfIntegerPropArr[i].getValue().equals(sf.sfIntegerPropArr[i].getValue())) {
                return false;
            }
        }
        return true;
    }

    public AudioFilter getCopy() {
        AudioFilter sf = new AudioFilter();
        this.copyTo(sf);
        return sf;
    }

    public void copyTo(AudioFilter sf) {

        for (int i = 0; i < sfBooleanPropArr.length; ++i) {
            sf.sfBooleanPropArr[i].setValue(this.sfBooleanPropArr[i].getValue());
        }
        for (int i = 0; i < sfStringPropArr.length; ++i) {
            sf.sfStringPropArr[i].setValue(this.sfStringPropArr[i].getValue());
        }
        for (int i = 0; i < sfIntegerPropArr.length; ++i) {
            sf.sfIntegerPropArr[i].setValue(this.sfIntegerPropArr[i].getValue());
        }
    }

    @Override
    public String getTag() {
        return TAG;
    }


    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getChannel() {
        return channel.getValueSafe();
    }

    public void setChannel(String sender) {
        this.channel.set(sender);
    }

    public StringProperty channelProperty() {
        return channel;
    }

    public boolean isChannelVis() {
        return channelVis.get();
    }

    public BooleanProperty channelVisProperty() {
        return channelVis;
    }

    public String getGenre() {
        return genre.getValueSafe();
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public boolean isGenreVis() {
        return genreVis.get();
    }

    public BooleanProperty genreVisProperty() {
        return genreVis;
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

    public boolean isThemeVis() {
        return themeVis.get();
    }

    public BooleanProperty themeVisProperty() {
        return themeVis;
    }

    public String getThemeTitle() {
        return themeTitle.get();
    }

    public void setThemeTitle(String theme) {
        this.themeTitle.set(theme);
    }

    public StringProperty themeTitleProperty() {
        return themeTitle;
    }

    public boolean isThemeTitleVis() {
        return themeTitleVis.get();
    }

    public BooleanProperty themeTitleVisProperty() {
        return themeTitleVis;
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

    public boolean isTitleVis() {
        return titleVis.get();
    }

    public BooleanProperty titleVisProperty() {
        return titleVis;
    }

    public String getSomewhere() {
        return somewhere.get();
    }

    public void setSomewhere(String somewhere) {
        this.somewhere.set(somewhere);
    }

    public StringProperty somewhereProperty() {
        return somewhere;
    }

    public boolean isSomewhereVis() {
        return somewhereVis.get();
    }

    public BooleanProperty somewhereVisProperty() {
        return somewhereVis;
    }

    public int getTimeRange() {
        return timeRange.get();
    }

    public void setTimeRange(int timeRange) {
        this.timeRange.set(timeRange);
    }

    public IntegerProperty timeRangeProperty() {
        return timeRange;
    }

    public boolean isTimeRangeVis() {
        return timeRangeVis.get();
    }

    public BooleanProperty timeRangeVisProperty() {
        return timeRangeVis;
    }

    public int getMinDur() {
        return minDur.get();
    }

    public IntegerProperty minDurProperty() {
        return minDur;
    }

    public void setMinDur(int minDur) {
        this.minDur.set(minDur);
    }

    public int getMaxDur() {
        return maxDur.get();
    }

    public IntegerProperty maxDurProperty() {
        return maxDur;
    }

    public void setMaxDur(int maxDur) {
        this.maxDur.set(maxDur);
    }

    public boolean isDurVis() {
        return durVis.get();
    }

    public BooleanProperty durVisProperty() {
        return durVis;
    }

    public boolean isOnlyVis() {
        return onlyVis.get();
    }

    public BooleanProperty onlyVisProperty() {
        return onlyVis;
    }

    public void setOnlyVis(boolean onlyNew) {
        this.onlyVis.set(onlyNew);
    }

    public boolean isOnlyNew() {
        return onlyNew.get();
    }

    public void setOnlyNew(boolean onlyNew) {
        this.onlyNew.set(onlyNew);
    }

    public BooleanProperty onlyNewProperty() {
        return onlyNew;
    }

    public boolean isOnlyBookmark() {
        return onlyBookmark.get();
    }

    public BooleanProperty onlyBookmarkProperty() {
        return onlyBookmark;
    }

    public void setOnlyBookmark(boolean onlyBookmark) {
        this.onlyBookmark.set(onlyBookmark);
    }

    public boolean isNoHistory() {
        return noHistory.get();
    }

    public BooleanProperty noHistoryProperty() {
        return noHistory;
    }

    public void setNoHistory(boolean noHistory) {
        this.noHistory.set(noHistory);
    }

    public int getPodcastOnOff() {
        return podcastOnOff.get();
    }

    public IntegerProperty podcastOnOffProperty() {
        return podcastOnOff;
    }

    public void setPodcastOnOff(int podcastOnOff) {
        this.podcastOnOff.set(podcastOnOff);
    }

    public boolean isPodcastVis() {
        return podcastVis.get();
    }

    public BooleanProperty podcastVisProperty() {
        return podcastVis;
    }

    public int getBlacklistOnOff() {
        return blacklistOnOff.get();
    }

    public IntegerProperty blacklistOnOffProperty() {
        return blacklistOnOff;
    }

    public void setBlacklistOnOff(int blacklistOnOff) {
        this.blacklistOnOff.set(blacklistOnOff);
    }

    @Override
    public String toString() {
        return name.getValue();
    }

    @Override
    public int compareTo(AudioFilter o) {
        return name.getValue().compareTo(o.getName());
    }
}
