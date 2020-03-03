package piHoleLCDStat;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.*;
import java.net.Socket;

public class dash extends StackPane {
    Gauge queriesBlockedGauge;

    VBox queriesPane;
    Gauge queriesGauge;

    VBox systemStatsVBox;
    VBox otherPiHoleStatsVBox;

    Gauge cpuGauge;
    Gauge memoryGauge;
    
    Label queriesPaneMoreInfoLabel;

    Gauge tempGauge;
    Gauge diskUsageGauge;

    boolean debugMode = false;
    boolean isPaneChangeTimerOn = true;

    long piHoleStatsFetcherSleep, systemStatsFetcherSleep, paneChangerTaskSleep;

    String telNetIP;
    int telNetPort;

    double sHeight, sWidth;

    boolean piHoleStatus;

    Label statusLabel;
    Label uniqueDomainsLabel;
    Label queriesForwardedLabel;
    Label queriesCachedLabel;
    Label clientsEverSeenLabel;
    Label uniqueClientsLabel;
    Label topDomainLabel;
    Label topAdLabel;
    Label topClientLabel;

    Color goodColour, badColour;

    double fontSize;

    public dash()
    {
        try {
            readConfig();
            debug("Init dash ...");

            setStyle("-fx-font-size: "+fontSize);
            queriesBlockedGauge = new Gauge();
            queriesBlockedGauge.setSkinType(Gauge.SkinType.SLIM);
            queriesBlockedGauge.setValueColor(Color.WHITE);
            queriesBlockedGauge.setTitleColor(Color.WHITE);
            queriesBlockedGauge.setBarColor(goodColour);
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
            queriesGauge.setBarColor(goodColour);
            queriesGauge.setCacheHint(CacheHint.SPEED);

            cpuGauge = new Gauge();
            cpuGauge.setSkinType(Gauge.SkinType.SIMPLE_SECTION);
            cpuGauge.setTitleColor(Color.WHITE);
            cpuGauge.setTitle("CPU");
            cpuGauge.setValue(temp);
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
            tempGauge.setValue(temp);
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
            diskUsageGauge.setValue(temp);
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
            VBox.setVgrow(systemStatsVBox,Priority.SOMETIMES);
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

            topDomainLabel = new Label();
            topDomainLabel.setWrapText(true);

            topAdLabel = new Label();
            topAdLabel.setWrapText(true);

            topClientLabel = new Label();
            topClientLabel.setWrapText(true);

            String hostName = getShellOutput("hostname");
            String ip = getShellOutput("hostname -I");

            Label hostLabel = new Label("Hostname : "+hostName);
            hostLabel.setWrapText(true);

            Label ipLabel = new Label("IP : "+ip);
            ipLabel.setWrapText(true);


            otherPiHoleStatsVBox = new VBox(otherPiHoleStatsHeading,hostLabel,ipLabel,statusH,uniqueDomansH,queriesForwardedH,queriesCachedH,clientsEverSeenH,uniqueClientsH,topDomainLabel,topAdLabel,topClientLabel);
            statusLabel.setTextFill(Color.LIGHTGREEN);
            otherPiHoleStatsVBox.setPadding(new Insets(0,5,0,5));
            otherPiHoleStatsVBox.setSpacing(3);
            otherPiHoleStatsVBox.getStyleClass().add("bg");


            getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            setPadding(new Insets(5));
            getChildren().add(systemStatsVBox);
            getChildren().add(otherPiHoleStatsVBox);


            queriesPaneMoreInfoLabel = new Label("0 Blocked\n0 Domains On Blocklist");
            queriesPaneMoreInfoLabel.setPadding(new Insets(0,0,0,5));

            queriesPane = new VBox(queriesGauge,queriesPaneMoreInfoLabel);
            VBox.setVgrow(queriesGauge,Priority.SOMETIMES);
            queriesPane.getStyleClass().add("bg");
            queriesPane.setSpacing(5);
            getChildren().add(queriesPane);
            getChildren().add(queriesBlockedGauge);

            setPrefSize(sWidth,sHeight);

            queriesBlockedGauge.toFront();

            topClientLabel.setPrefWidth(sWidth-10);
            topDomainLabel.setPrefWidth(sWidth-10);
            topAdLabel.setPrefWidth(sWidth-10);
            hostLabel.setPrefWidth(sWidth-10);
            ipLabel.setPrefWidth(sWidth-10);

            /*topClientLabel.prefWidthProperty().bind(otherPiHoleStatsVBox.widthProperty());
            topDomainLabel.prefWidthProperty().bind(otherPiHoleStatsVBox.widthProperty());
            topAdLabel.prefWidthProperty().bind(otherPiHoleStatsVBox.widthProperty());
            hostLabel.prefWidthProperty().bind(otherPiHoleStatsVBox.widthProperty());
            ipLabel.prefWidthProperty().bind(otherPiHoleStatsVBox.widthProperty());*/

            setOnTouchPressed(event -> switchNextPane());

            init();
            debug("Init Done!");
        }
        catch (Exception e)
        {
            debug("ERROR OCCURRED! ");
            e.printStackTrace();
            debug("Quitting ...");
            isQuit = true;
            Platform.exit();
        }
    }

    private void switchNextPane()
    {
        if (currentPane == pane.queries) {
            Platform.runLater(() -> switchPane(pane.systemStats));
        } else if (currentPane == pane.systemStats) {
            Platform.runLater(() -> switchPane(pane.otherPiHoleStats));
        } else if(currentPane == pane.otherPiHoleStats) {
            Platform.runLater(() -> switchPane(pane.queriesBlocked));
        } else if(currentPane == pane.queriesBlocked) {
            Platform.runLater(() -> switchPane(pane.queries));
        }
    }

    private void readConfig() throws Exception
    {
        debug("Reading config ...");

        BufferedReader bf = new BufferedReader(new FileReader(new File("config")));
        String[] conf = bf.readLine().split("::");

        debugMode = conf[0].equals("true");
        isPaneChangeTimerOn = conf[1].equals("true");

        piHoleStatsFetcherSleep = Long.parseLong(conf[2]);
        systemStatsFetcherSleep = Long.parseLong(conf[3]);
        paneChangerTaskSleep = Long.parseLong(conf[4]);

        telNetIP = conf[5];
        telNetPort = Integer.parseInt(conf[6]);

        sHeight = Double.parseDouble(conf[7]);
        sWidth = Double.parseDouble(conf[8]);

        fontSize = Double.parseDouble(conf[9]);

        goodColour = Color.valueOf(conf[10]);
        badColour = Color.valueOf(conf[11]);

        bf.close();

        debug("... Done!");
    }
    
    private void switchPane(pane p)
    {
        if(p==pane.queriesBlocked)
            queriesBlockedGauge.toFront();
        else if(p==pane.queries)
            queriesPane.toFront();
        else if(p==pane.systemStats)
            systemStatsVBox.toFront();
        else if(p==pane.otherPiHoleStats)
            otherPiHoleStatsVBox.toFront();

        currentPane = p;
    }
    
    pane currentPane = pane.queriesBlocked;
    enum pane{
        queriesBlocked,queries,systemStats,otherPiHoleStats
    }


    boolean isQuit = false;

    public void init()
    {
        debug("Starting piHoleStatsFetcher ...");
        Thread t1 = new Thread(piHoleStatsFetcher);
        t1.setPriority(1);
        t1.start();

        debug("Starting systemStatsFetcher ...");
        Thread t2 = new Thread(systemStatsFetcher);
        t2.setPriority(1);
        t2.start();


        if(isPaneChangeTimerOn)
        {
            debug("Starting paneChangerTask ...");
            Thread t3 = new Thread(paneChangerTask);
            t3.setPriority(1);
            t3.start();
        }
    }

    Socket s;
    BufferedWriter bw;
    BufferedReader br;

    String queriesBlockedToday="0";
    double queriesBlockedPercentToday=0;
    long totalDnsQueries=0;
    long totalBlocked = 0;
    double upperLimit=0;

    double temp=0;

    Task<Void> piHoleStatsFetcher = new Task<>() {
        @Override
        protected Void call() {
            try {
                debug("Init Socket ...");
                s = new Socket("127.0.0.1", 4711);
                bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                debug("... Done! Ready!");

                prevCommand = ">stats";
                bw.write(">stats\r\n");
                bw.flush();

                StringBuilder output = new StringBuilder();

                while (!isQuit) {
                    int l = br.read();

                    if (output.toString().endsWith("---EOM---") && !output.toString().replace("\n","").equals("---EOM---")) {
                        switch (prevCommand) {
                            case ">stats":
                                String[] outputArr = output.toString().split("\n");
                                for (String eachVal : outputArr) {
                                    String[] part = eachVal.split(" ");
                                    switch (part[0]) {
                                        case "ads_percentage_today":
                                            queriesBlockedPercentToday = Double.parseDouble(part[1]);
                                            break;
                                        case "ads_blocked_today":
                                            queriesBlockedToday = part[1];
                                            break;
                                        case "dns_queries_today":
                                            totalDnsQueries = Long.parseLong(part[1]);
                                            break;
                                        case "domains_being_blocked":
                                            totalBlocked = Long.parseLong(part[1]);
                                            break;
                                        case "status":
                                            piHoleStatus = part[1].equals("enabled");
                                            break;
                                        case "unique_domains":
                                            Platform.runLater(() -> uniqueDomainsLabel.setText(part[1]));
                                            break;
                                        case "queries_forwarded":
                                            Platform.runLater(() -> queriesForwardedLabel.setText(part[1]));
                                            break;
                                        case "queries_cached":
                                            Platform.runLater(() -> queriesCachedLabel.setText(part[1]));
                                            break;
                                        case "clients_ever_seen":
                                            Platform.runLater(() -> clientsEverSeenLabel.setText(part[1]));
                                            break;
                                        case "unique_clients":
                                            Platform.runLater(() -> uniqueClientsLabel.setText(part[1]));
                                            break;
                                    }
                                }

                                setPiHoleStatusUIChanges();

                                Platform.runLater(() -> {
                                    queriesBlockedGauge.setValue(queriesBlockedPercentToday);

                                    queriesGauge.setValue(totalDnsQueries);
                                    queriesPaneMoreInfoLabel.setText(queriesBlockedToday + " Blocked\n" + totalBlocked + " Domains On Blocklist");
                                });

                                if (upperLimit < totalDnsQueries) {
                                    upperLimit = totalDnsQueries;
                                    Platform.runLater(() -> queriesGauge.setMaxValue(totalDnsQueries));
                                    if (totalDnsQueries > 500) {
                                        Platform.runLater(() -> queriesGauge.setMinValue(totalDnsQueries - 500));
                                    }
                                }
                                Thread.sleep(piHoleStatsFetcherSleep / 4);
                                prevCommand = ">top-domains (1)";
                                bw.write(">top-domains (1)\r\n");
                                bw.flush();
                                break;
                            case ">top-domains (1)":
                                String[] topDomainArr = secondLineFetcher(output.toString()).split(" ");
                                Platform.runLater(() -> topDomainLabel.setText("Top Domain: (" + topDomainArr[1] + " Queries)\n" + topDomainArr[2]));
                                Thread.sleep(piHoleStatsFetcherSleep / 4);
                                prevCommand = ">top-ads (1)";
                                bw.write(">top-ads (1)\r\n");
                                bw.flush();
                                break;
                            case ">top-ads (1)":
                                String[] topAdArr = secondLineFetcher(output.toString()).split(" ");
                                Platform.runLater(() -> topAdLabel.setText("Top Ad: (" + topAdArr[1] + " Queries)\n" + topAdArr[2]));
                                Thread.sleep(piHoleStatsFetcherSleep / 4);
                                prevCommand = ">top-clients (1)";
                                bw.write(">top-clients (1)\r\n");
                                bw.flush();
                                break;
                            case ">top-clients (1)":
                                String[] topClient = secondLineFetcher(output.toString()).split(" ");
                                if (topClient.length == 4)
                                    Platform.runLater(() -> topClientLabel.setText("Top Client: (" + topClient[1] + " Queries)\n" + topClient[2] + "\n" + topClient[3]));
                                else
                                    Platform.runLater(() -> topClientLabel.setText("Top Client:  (" + topClient[1] + " Queries)\n" + topClient[2]));
                                Thread.sleep(piHoleStatsFetcherSleep / 4);
                                prevCommand = ">stats";
                                bw.write(">stats\r\n");
                                bw.flush();
                                break;
                        }

                        output = new StringBuilder();
                    } else {
                        output.append((char) l);
                    }
                }

                bw.write(">quit\r\n");
                bw.flush();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    String prevCommand="";

    boolean prevStatus = false;
    private void setPiHoleStatusUIChanges()
    {
        if(piHoleStatus!=prevStatus)
        {
            prevStatus = piHoleStatus;

            if(piHoleStatus)
            {
                Platform.runLater(()->{
                    statusLabel.setText("Enabled");
                    statusLabel.setTextFill(Color.LIGHTGREEN);
                    queriesBlockedGauge.setBarColor(goodColour);
                    queriesGauge.setBarColor(goodColour);
                });
            }
            else
            {
                Platform.runLater(()->{
                    statusLabel.setText("Disabled");
                    statusLabel.setTextFill(Color.RED);
                    queriesBlockedGauge.setBarColor(badColour);
                    queriesGauge.setBarColor(badColour);
                });
            }
        }
    }

    double totalMem = 0;
    double usedMem = 0;
    double cpuLoad = 0;
    double diskUsagePercent = 0;

    Task<Void> systemStatsFetcher = new Task<>() {
        @Override
        protected Void call() {
            try {
                while (!isQuit) {
                    String temperatureRaw = getShellOutput("/opt/vc/bin/vcgencmd measure_temp");
                    temp = Double.parseDouble(temperatureRaw.replace("temp=", "").replace("'C", ""));

                    int index = 0;
                    for (String e : secondLineFetcher(getShellOutput("free -m")).split(" ")) {
                        if (!e.replace(" ", "").replace("Mem:", "").equals(""))
                        {
                            if (index == 0)
                                totalMem = Double.parseDouble(e);
                            else if (index == 1)
                                usedMem = Double.parseDouble(e);
                            index++;
                        }
                    }

                    cpuLoad = Double.parseDouble(getShellOutput("cat /proc/loadavg").split(" ")[0]);

                    for (String e2 : secondLineFetcher(getShellOutput("df -h")).split(" ")) {
                        if (!e2.replace(" ", "").equals(""))
                        {
                            if (e2.contains("%")) {
                                diskUsagePercent = Double.parseDouble(e2.substring(0, e2.length() - 1));
                            }
                        }
                    }

                    Platform.runLater(() -> {
                        tempGauge.setValue(temp);
                        memoryGauge.setValue((usedMem / totalMem) * 100);
                        cpuGauge.setValue(cpuLoad);
                        diskUsageGauge.setValue(diskUsagePercent);
                    });

                    Thread.sleep(systemStatsFetcherSleep);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };
    
    Task<Void> paneChangerTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                while (isPaneChangeTimerOn && !isQuit) {
                    Thread.sleep(paneChangerTaskSleep);
                    Platform.runLater(()->switchNextPane());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    private void debug(String txt)
    {
        if(debugMode) System.out.println(txt);
    }

    private String getShellOutput(String cmd) throws Exception
    {
        Process p = Runtime.getRuntime().exec(cmd);
        InputStreamReader isr = new InputStreamReader(p.getInputStream());

        StringBuilder sb = new StringBuilder();
        while (true)
        {
            int x = isr.read();
            if(x!=-1)
            {
                sb.append((char) x);
            }
            else break;
        }

        isr.close();
        p.destroy();

        return sb.toString();
    }

    private String secondLineFetcher(String chunk)
    {
        return chunk.split("\n")[1];
    }
}
