package piHoleLCDStat;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class dashBase extends StackPane {
    Gauge queriesBlockedGauge, queriesGauge, cpuGauge, memoryGauge, tempGauge, diskUsageGauge;
    VBox queriesPane, systemStatsVBox, otherPiHoleStatsVBox, topDomainsMainVBox, topAdsMainVBox, topClientsMainVBox, topDomainsVBox, topAdsVBox, topClientsVBox;
    Label queriesPaneMoreInfoLabel, statusLabel, uniqueDomainsLabel, queriesForwardedLabel, queriesCachedLabel, clientsEverSeenLabel, uniqueClientsLabel, hostLabel, ipLabel;

    public void initNodes()
    {
        queriesBlockedGauge = new Gauge();
        queriesBlockedGauge.setSkinType(Gauge.SkinType.SLIM);
        queriesBlockedGauge.setValueColor(Color.WHITE);
        queriesBlockedGauge.setTitleColor(Color.WHITE);

        queriesBlockedGauge.setTitle("Queries Blocked");
        queriesBlockedGauge.setValue(0);
        queriesBlockedGauge.getStyleClass().add("bg");
        queriesBlockedGauge.setUnitColor(Color.WHITE);
        queriesBlockedGauge.setUnit("%");
        queriesBlockedGauge.setCache(true);
        queriesBlockedGauge.setCacheHint(CacheHint.SPEED);

        queriesGauge = new Gauge();
        queriesGauge.setSkinType(Gauge.SkinType.TILE_SPARK_LINE);
        queriesGauge.setValueColor(Color.WHITE);
        queriesGauge.setTitleColor(Color.WHITE);
        queriesGauge.setTitle("Total Queries");
        queriesGauge.setValue(0);
        queriesGauge.setBackgroundPaint(Paint.valueOf("#000000"));
        queriesGauge.setUnit("0");
        queriesGauge.setCache(true);

        queriesGauge.setCacheHint(CacheHint.SPEED);

        cpuGauge = new Gauge();
        cpuGauge.setSkinType(Gauge.SkinType.SIMPLE_SECTION);
        cpuGauge.setTitleColor(Color.WHITE);
        cpuGauge.setTitle("CPU");

        cpuGauge.setValueColor(Color.WHITE);
        cpuGauge.setUnitColor(Color.WHITE);
        cpuGauge.setSections(new Section(0,60,Color.GREEN), new Section(60.01,80,Color.ORANGE), new Section(80.01,100,Color.RED));
        cpuGauge.getStyleClass().add("bg");
        cpuGauge.setUnit("%");
        cpuGauge.setCache(true);
        cpuGauge.setCacheHint(CacheHint.SPEED);

        memoryGauge = new Gauge();
        memoryGauge.setSkinType(Gauge.SkinType.SIMPLE_SECTION);
        memoryGauge.setTitleColor(Color.WHITE);
        memoryGauge.setTitle("MEM");
        memoryGauge.setValue(0);
        memoryGauge.setValueColor(Color.WHITE);
        memoryGauge.setUnitColor(Color.WHITE);
        memoryGauge.setSections(new Section(0,65,Color.GREEN), new Section(65.01,75,Color.ORANGE), new Section(75.01,100,Color.RED));
        memoryGauge.getStyleClass().add("bg");
        memoryGauge.setUnit("%");
        memoryGauge.setCache(true);
        memoryGauge.setCacheHint(CacheHint.SPEED);

        tempGauge = new Gauge();
        tempGauge.setSkinType(Gauge.SkinType.SIMPLE_SECTION);
        tempGauge.setTitleColor(Color.WHITE);
        tempGauge.setTitle("TEMP");

        tempGauge.setValueColor(Color.WHITE);
        tempGauge.setUnitColor(Color.WHITE);
        tempGauge.setSections(new Section(0,40,Color.BLUE), new Section(40.01,60,Color.GREEN), new Section(60.01,70,Color.ORANGE), new Section(70.01,100, Color.RED));
        tempGauge.getStyleClass().add("bg");
        tempGauge.setUnit("°C");
        tempGauge.setCache(true);
        tempGauge.setCacheHint(CacheHint.SPEED);

        diskUsageGauge = new Gauge();
        diskUsageGauge.setSkinType(Gauge.SkinType.SIMPLE_SECTION);
        diskUsageGauge.setTitleColor(Color.WHITE);
        diskUsageGauge.setTitle("DISK");

        diskUsageGauge.setValueColor(Color.WHITE);
        diskUsageGauge.setUnitColor(Color.WHITE);
        diskUsageGauge.setSections(new Section(0,75,Color.GREEN), new Section(75.01,100,Color.RED));
        diskUsageGauge.getStyleClass().add("bg");
        diskUsageGauge.setUnit("%");
        diskUsageGauge.setCache(true);
        diskUsageGauge.setCacheHint(CacheHint.SPEED);


        HBox h1 = new HBox(cpuGauge,memoryGauge);
        h1.setSpacing(5);
        h1.setAlignment(Pos.CENTER);
        h1.setPadding(new Insets(0,1,0,1));

        HBox h2 = new HBox(tempGauge,diskUsageGauge);
        h2.setSpacing(5);
        h2.setAlignment(Pos.CENTER);
        h2.setPadding(new Insets(0,1,0,1));

        systemStatsVBox = new VBox(h1,h2);
        VBox.setVgrow(systemStatsVBox, Priority.SOMETIMES);
        systemStatsVBox.setAlignment(Pos.CENTER);
        systemStatsVBox.getStyleClass().add("bg");


        Label x = new Label("Other Pi-Hole Stats");
        HBox otherPiHoleStatsHeading = new HBox(x);
        otherPiHoleStatsHeading.setAlignment(Pos.CENTER);
        HBox.setHgrow(otherPiHoleStatsHeading,Priority.SOMETIMES);
        otherPiHoleStatsHeading.setPadding(new Insets(0,0,2,0));

        statusLabel = new Label("Enabled");
        HBox statusH = new HBox(new Label("Status: "),statusLabel);

        uniqueDomainsLabel = new Label("0");
        HBox uniqueDomansH = new HBox(new Label("Unique Domains: "),uniqueDomainsLabel);

        queriesForwardedLabel = new Label("0");
        HBox queriesForwardedH = new HBox(new Label("Queries Forwarded: "),queriesForwardedLabel);

        queriesCachedLabel = new Label("0");
        HBox queriesCachedH = new HBox(new Label("Queries Cached: "),queriesCachedLabel);

        clientsEverSeenLabel = new Label("0");
        HBox clientsEverSeenH = new HBox(new Label("Clients ever seen: "),clientsEverSeenLabel);

        uniqueClientsLabel = new Label("0");
        HBox uniqueClientsH = new HBox(new Label("Unique Clients: "),uniqueClientsLabel);


        hostLabel= new Label();
        hostLabel.setWrapText(true);

        ipLabel = new Label();
        ipLabel.setWrapText(true);


        otherPiHoleStatsVBox = new VBox(otherPiHoleStatsHeading,hostLabel,ipLabel,statusH,uniqueDomansH,queriesForwardedH,queriesCachedH,clientsEverSeenH,uniqueClientsH);
        statusLabel.setTextFill(Color.LIGHTGREEN);
        otherPiHoleStatsVBox.setPadding(new Insets(0,5,0,5));
        otherPiHoleStatsVBox.getStyleClass().add("bg");


        getStylesheets().add(getClass().getResource("style.css").toExternalForm());



        queriesPaneMoreInfoLabel = new Label("0 Blocked\n0 Domains On Blocklist");
        queriesPaneMoreInfoLabel.setPadding(new Insets(0,0,0,5));

        queriesPane = new VBox(queriesGauge,queriesPaneMoreInfoLabel);
        VBox.setVgrow(queriesGauge,Priority.SOMETIMES);
        queriesPane.getStyleClass().add("bg");
        queriesPane.setSpacing(5);

        topDomainsVBox = new VBox();
        topDomainsMainVBox = new VBox(new Label("Top Domains"),topDomainsVBox);
        topDomainsMainVBox.setAlignment(Pos.TOP_CENTER);
        topDomainsMainVBox.getStyleClass().add("bg");

        topClientsVBox = new VBox();
        topClientsMainVBox = new VBox(new Label("Top Clients"),topClientsVBox);
        topClientsMainVBox.setAlignment(Pos.TOP_CENTER);
        topClientsMainVBox.getStyleClass().add("bg");

        topAdsVBox = new VBox();
        topAdsMainVBox = new VBox(new Label("Top Blocked Ads"),topAdsVBox);
        topAdsMainVBox.setAlignment(Pos.TOP_CENTER);
        topAdsMainVBox.getStyleClass().add("bg");


        setPadding(new Insets(5));
        getChildren().addAll(systemStatsVBox, otherPiHoleStatsVBox, queriesPane, queriesBlockedGauge, topDomainsMainVBox, topClientsMainVBox, topAdsMainVBox);

        queriesBlockedGauge.toFront();

    }
}
