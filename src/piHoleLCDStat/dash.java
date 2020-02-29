package piHoleLCDStat;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.*;
import java.net.Socket;

public class dash extends StackPane {
    Gauge queriesBlockedGauge;

    VBox queriesPane;
    Gauge queriesGauge;

    VBox systemStatsVBox;

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

    public dash()
    {
        try {
            debug("Init dash ...");
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

            Label vboxHeader = new Label("System Stats");
            vboxHeader.setPadding(new Insets(0,0,10,0));
            vboxHeader.setFont(new Font("Roboto Regular",15));

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

            systemStatsVBox = new VBox(vboxHeader,h1,h2);
            systemStatsVBox.setAlignment(Pos.TOP_CENTER);
            systemStatsVBox.getStyleClass().add("bg");

            getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            getStyleClass().add("basePane");

            setPadding(new Insets(5));
            getChildren().add(systemStatsVBox);

            queriesPaneMoreInfoLabel = new Label("0 Blocked\n0 Domains On Blocklist");
            queriesPaneMoreInfoLabel.setFont(new Font("Roboto Regular",13));
            queriesPaneMoreInfoLabel.setPadding(new Insets(0,0,0,5));

            queriesPane = new VBox(queriesGauge,queriesPaneMoreInfoLabel);
            queriesPane.getStyleClass().add("bg");
            queriesPane.setSpacing(5);
            getChildren().add(queriesPane);
            getChildren().add(queriesBlockedGauge);

            readConfig();

            setPrefSize(sWidth,sHeight);

            queriesBlockedGauge.toFront();

            setOnTouchPressed(event -> {
                if(currentPane == pane.queriesBlocked)
                {
                    switchPane(pane.queries);
                }
                else if(currentPane == pane.queries)
                {
                    switchPane(pane.systemStats);
                }
                else if(currentPane == pane.systemStats)
                {
                    switchPane(pane.queriesBlocked);
                }
            });

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

        currentPane = p;
    }
    
    pane currentPane = pane.queriesBlocked;
    enum pane{
        queriesBlocked,queries,systemStats
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

                bw.write(">stats\r\n");
                bw.flush();


                StringBuilder output = new StringBuilder();

                while (!isQuit) {
                    int l = br.read();

                    if (output.toString().endsWith("---EOM---")) {

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
                            }
                        }

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

                        output = new StringBuilder();
                        Thread.sleep(piHoleStatsFetcherSleep);
                        bw.write(">stats\r\n");
                        bw.flush();
                    } else {
                        output.append((char) l);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };

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
                    if (currentPane == pane.queriesBlocked) {
                        Platform.runLater(() -> switchPane(pane.queries));
                    } else if (currentPane == pane.queries) {
                        Platform.runLater(() -> switchPane(pane.systemStats));
                    } else if (currentPane == pane.systemStats) {
                        Platform.runLater(() -> switchPane(pane.queriesBlocked));
                    }
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
