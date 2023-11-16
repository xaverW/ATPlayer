module atplayer {
    opens de.p2tools.atplayer;
    exports de.p2tools.atplayer;

    opens de.p2tools.atplayer.controller.data;
    opens de.p2tools.atplayer.controller.data.download;
    opens de.p2tools.atplayer.controller.audio;
    opens de.p2tools.atplayer.controller.config;
    exports de.p2tools.atplayer.controller.config;

    requires de.p2tools.p2lib;
    requires javafx.controls;
    requires org.controlsfx.controls;

    requires java.logging;
    requires java.desktop;

    requires commons.cli;
    requires com.fasterxml.jackson.core;
    requires org.tukaani.xz;

    requires okhttp3;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.io;
}

