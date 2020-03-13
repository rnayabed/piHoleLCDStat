package piHoleLCDStat;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.Socket;

import static piHoleLCDStat.programInfo.*;

public class dash extends dashBase {

    boolean debugMode = false, isPaneChangeTimerOn = true, piHoleStatus;
    long piHoleStatsFetcherSleep, systemStatsFetcherSleep, paneChangerTaskSleep;
    String telNetIP;
    int telNetPort, topDomainsLimit, topClientsLimit, topAdsLimit;
    double sHeight, sWidth, fontSize;
    Color goodColour, badColour;

    public dash()
    {
        try {
            System.out.println("\n\npiHoleLCDStat"+
                    "\nAuthor : "+AUTHOR+
                    "\nVersion : "+VERSION+
                    "\nSource : "+REPO_LINK+
                    "\nBuild : "+BUILD_DATE+
                    "\nJava Version : "+System.getProperty("java.version")+
                    "\nJavaFX Version : "+System.getProperty("javafx.version")+"\n\n");

            readConfig();
            debug("Init dash ...");

            String hostName = getShellOutput("hostname");
            String ip = getShellOutput("hostname -I");

            initNodes();

            setPrefSize(sWidth,sHeight);
            setStyle("-fx-font-size: "+fontSize);

            hostLabel.setText("Hostname: "+hostName);
            ipLabel.setText("IP: "+ip);

            cpuGauge.setValue(cpuLoad);
            diskUsageGauge.setValue(diskUsagePercent);
            tempGauge.setValue(temp);

            queriesGauge.setBarColor(goodColour);
            queriesBlockedGauge.setBarColor(goodColour);

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
        if (currentPane == pane.topDomains) {
            Platform.runLater(() -> switchPane(pane.topClients));
        } else if (currentPane == pane.topClients) {
            Platform.runLater(() -> switchPane(pane.topAds));
        } else if(currentPane == pane.topAds) {
            Platform.runLater(() -> switchPane(pane.queriesBlocked));
        } else if(currentPane == pane.queriesBlocked) {
            Platform.runLater(() -> switchPane(pane.queries));
        } else if(currentPane == pane.queries) {
            Platform.runLater(() -> switchPane(pane.systemStats));
        } else if(currentPane == pane.systemStats) {
            Platform.runLater(() -> switchPane(pane.moreSystemStats));
        } else if(currentPane == pane.moreSystemStats) {
            Platform.runLater(() -> switchPane(pane.topDomains));
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

        topDomainsLimit = Integer.parseInt(conf[12]);
        topClientsLimit = Integer.parseInt(conf[13]);
        topAdsLimit = Integer.parseInt(conf[14]);

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
        else if(p==pane.moreSystemStats)
            moreSystemStatsVBox.toFront();
        else if(p==pane.topDomains)
            topDomainsMainVBox.toFront();
        else if(p==pane.topClients)
            topClientsMainVBox.toFront();
        else if(p==pane.topAds)
            topAdsMainVBox.toFront();

        currentPane = p;
    }
    
    pane currentPane = pane.queriesBlocked;
    enum pane{
        queriesBlocked,queries,systemStats,moreSystemStats, topDomains, topClients, topAds
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

    double temp=0;

    enum command{
        stats,topAds,topDomains,topClients
    }

    command lastCommand;

    Task<Void> piHoleStatsFetcher = new Task<>() {
        @Override
        protected Void call() {
            try {
                debug("Init Socket ...");
                s = new Socket("127.0.0.1", 4711);
                bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                debug("... Done! Ready!");

                lastCommand = command.stats;
                bw.write(">stats\r\n");
                bw.flush();

                StringBuilder output = new StringBuilder();

                while (!isQuit) {
                    int l = br.read();

                    if (output.toString().endsWith("---EOM---") && !output.toString().replace("\n","").equals("---EOM---")) {
                        switch (lastCommand) {
                            case stats:
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

                                    queriesGauge.setMaxValue(totalDnsQueries);

                                    if(totalDnsQueries>500)
                                    {
                                        queriesGauge.setMinValue(totalDnsQueries-500);
                                    }

                                    queriesPaneMoreInfoLabel.setText(queriesBlockedToday + " Blocked\n" + totalBlocked + " Domains On Blocklist");
                                });


                                Thread.sleep(piHoleStatsFetcherSleep / 4);
                                lastCommand = command.topDomains;
                                bw.write(">top-domains ("+topDomainsLimit+")\r\n");
                                bw.flush();
                                break;
                            case topDomains:
                                String[] lines = output.toString().split("\n");
                                Platform.runLater(()->topDomainsVBox.getChildren().clear());
                                for(int i = 1; i < lines.length - 1; i++)
                                {
                                    String[] r = lines[i].split(" ");
                                    Label eachLabel = new Label(i+". "+r[2]+" - "+r[1]+" Queries");
                                    eachLabel.setWrapText(true);
                                    Platform.runLater(()->topDomainsVBox.getChildren().add(eachLabel));
                                }
                                Thread.sleep(piHoleStatsFetcherSleep / 4);
                                lastCommand = command.topAds;
                                bw.write(">top-ads ("+topAdsLimit+")\r\n");
                                bw.flush();
                                break;
                            case topAds:
                                String[] lines2 = output.toString().split("\n");
                                Platform.runLater(()->topAdsVBox.getChildren().clear());
                                for(int i = 1; i < lines2.length - 1; i++)
                                {
                                    String[] r = lines2[i].split(" ");
                                    Label eachLabel = new Label(i+". "+r[2]+" - "+r[1]+" Queries");
                                    eachLabel.setWrapText(true);
                                    Platform.runLater(()->topAdsVBox.getChildren().add(eachLabel));
                                }

                                Thread.sleep(piHoleStatsFetcherSleep / 4);
                                lastCommand = command.topClients;
                                bw.write(">top-clients ("+topClientsLimit+")\r\n");
                                bw.flush();
                                break;
                            case topClients:
                                String[] lines3 = output.toString().split("\n");
                                Platform.runLater(()->topClientsVBox.getChildren().clear());
                                for(int i = 1; i < lines3.length - 1; i++)
                                {
                                    String[] r = lines3[i].split(" ");
                                    String txt;
                                    if(r.length == 4)
                                        txt = i+". "+r[2]+" - "+r[3]+" - "+r[1]+" Queries";
                                    else
                                        txt = i+". "+r[2]+" - "+r[1]+" Queries";
                                    Label eachLabel = new Label(txt);
                                    eachLabel.setWrapText(true);
                                    Platform.runLater(()->topClientsVBox.getChildren().add(eachLabel));
                                }

                                Thread.sleep(piHoleStatsFetcherSleep / 4);
                                lastCommand = command.stats;
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

                    String uptimeRawOutput = getShellOutput("uptime");

                    Platform.runLater(() -> {
                        tempGauge.setValue(temp);
                        memoryGauge.setValue((usedMem / totalMem) * 100);
                        cpuGauge.setValue(cpuLoad);
                        diskUsageGauge.setValue(diskUsagePercent);
                        loadAverageLabel.setText(uptimeRawOutput.substring(uptimeRawOutput.indexOf("load")).replace("load average:","Load Average:"));
                        uptimeLabel.setText(uptimeRawOutput.substring(uptimeRawOutput.indexOf("up"),uptimeRawOutput.indexOf(",")).replace("up","Uptime: "));
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
