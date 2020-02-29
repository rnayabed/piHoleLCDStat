package piHoleLCDStat;

import java.io.*;

public class confUpdater {

    public static void main(String... args)
    {
        try
        {
            debug("piHoleLCDStat v1.0\n" +
                    "By https://github.com/dubbadhar/piHoleLCDStat\n");

            boolean debugMode,isPaneChangeTimerOn;
            long piHoleStatsFetcherSleep,systemStatsFetcherSleep,paneChangerTaskSleep;
            String telNetIP;
            int telNetPort;
            double sHeight,sWidth;

            debug("Reading Current config ...");

            BufferedReader bf = new BufferedReader(new FileReader(new File("config")));
            String[] conf = bf.readLine().split("::");
            bf.close();

            debugMode = conf[0].equals("true");
            isPaneChangeTimerOn = conf[1].equals("true");

            piHoleStatsFetcherSleep = Long.parseLong(conf[2]);
            systemStatsFetcherSleep = Long.parseLong(conf[3]);
            paneChangerTaskSleep = Long.parseLong(conf[4]);

            telNetIP = conf[5];
            telNetPort = Integer.parseInt(conf[6]);

            sHeight = Double.parseDouble(conf[7]);
            sWidth = Double.parseDouble(conf[8]);

            debug("... Done!");

            BufferedReader b = new BufferedReader(new InputStreamReader(System.in));

            while(true)
            {
                debug("Turn on Debug mode? (does nothing to the UI, use if something goes wrong)\n" +
                        "Current Value: '"+debugMode+"'\n" +
                        "Enter '1' for ON, '0' for OFF, '--' to not change at all.\n");

                String inputDebugMode = b.readLine();
                if(inputDebugMode.equals("1")) debugMode = true;
                else if(inputDebugMode.equals("0")) debugMode = false;
                else if(!inputDebugMode.equals("--"))
                {
                    debug("Wrong input. Try Again!");
                    continue;
                }

                debug("Turn on Pane Changer? (Changes current pane after every X seconds)\n" +
                        "Current Value: '"+isPaneChangeTimerOn+"'\n" +
                        "Enter '1' for ON, '0' for OFF, '--' to not change at all.\n");

                String inputIsPaneChangeTimerOn = b.readLine();
                if(inputIsPaneChangeTimerOn.equals("1")) isPaneChangeTimerOn = true;
                else if(inputIsPaneChangeTimerOn.equals("0")) isPaneChangeTimerOn = false;
                else if(!inputIsPaneChangeTimerOn.equals("--"))
                {
                    debug("Wrong input. Try Again!");
                    continue;
                }

                debug("Enter duration in milliseconds for sleep between fetching Pi-Hole Info\n" +
                        "Current Value: '"+piHoleStatsFetcherSleep+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputPiHoleStatsFetcherSleep = b.readLine();
                if(!inputPiHoleStatsFetcherSleep.equals("--"))
                {
                    try
                    {
                        long sleepDur = Long.parseLong(inputPiHoleStatsFetcherSleep);
                        if(sleepDur<1)
                        {
                            debug("Sleep duration cannot be less than 1. Try Again!");
                            debug("Wrong input. Try Again!");
                            continue;
                        }
                        else
                        {
                            piHoleStatsFetcherSleep = sleepDur;
                        }
                    }
                    catch (Exception e)
                    {
                        debug("Wrong input. Try Again!");
                        continue;
                    }
                }


                debug("Enter duration in milliseconds for sleep between fetching System Info\n" +
                        "Current Value: '"+systemStatsFetcherSleep+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputSystemStatsFetcherSleep = b.readLine();
                if(!inputSystemStatsFetcherSleep.equals("--"))
                {
                    try
                    {
                        long sleepDur = Long.parseLong(inputSystemStatsFetcherSleep);
                        if(sleepDur<1)
                        {
                            debug("Sleep duration cannot be less than 1. Try Again!");
                            debug("Wrong input. Try Again!");
                            continue;
                        }
                        else
                        {
                            systemStatsFetcherSleep = sleepDur;
                        }
                    }
                    catch (Exception e)
                    {
                        debug("Wrong input. Try Again!");
                        continue;
                    }
                }


                if(isPaneChangeTimerOn)
                {
                    debug("Enter duration in milliseconds for sleep between chaning screens\n" +
                            "Current Value: '"+paneChangerTaskSleep+"'\n" +
                            "Enter '--' to not change at all.\n");

                    String inputPaneChangerTaskSleep = b.readLine();
                    if(!inputPaneChangerTaskSleep.equals("--"))
                    {
                        try
                        {
                            long sleepDur = Long.parseLong(inputPaneChangerTaskSleep);
                            if(sleepDur<1)
                            {
                                debug("Sleep duration cannot be less than 1. Try Again!");
                                debug("Wrong input. Try Again!");
                                continue;
                            }
                            else
                            {
                                paneChangerTaskSleep = sleepDur;
                            }
                        }
                        catch (Exception e)
                        {
                            debug("Wrong input. Try Again!");
                            continue;
                        }
                    }
                }

                debug("Enter Pi-Hole Telnet IP (Set it to 127.0.0.1 if you're going to run this on the same device as Pi-Hole)\n" +
                        "Current Value: '"+telNetIP+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputTelNetIP = b.readLine();
                if(!inputTelNetIP.equals("--"))
                {
                    telNetIP = inputTelNetIP;
                }

                debug("Enter Pi-Hole Telnet Port (Set it to 4711 if you're going to run this on the same device as Pi-Hole)\n" +
                        "Current Value: '"+telNetPort+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputTelNetPort = b.readLine();
                if(!inputTelNetPort.equals("--"))
                {
                    try
                    {
                        telNetPort = Integer.parseInt(inputTelNetPort);
                    }
                    catch (Exception e)
                    {
                        debug("Wrong input. Try Again!");
                        continue;
                    }
                }

                debug("Enter Screen Height in Pixels)\n" +
                        "Current Value: '"+sHeight+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputSHeight = b.readLine();
                if(!inputSHeight.equals("--"))
                {
                    try
                    {
                        double s = Double.parseDouble(inputSHeight);
                        if(s<0)
                        {
                            debug("Screen Height cant be less than 0. Try Again!");
                            continue;
                        }
                        else
                        {
                            sHeight = s;
                        }
                    }
                    catch (Exception e)
                    {
                        debug("Wrong input. Try Again!");
                        continue;
                    }
                }

                debug("Enter Screen Width in Pixels)\n" +
                        "Current Value: '"+sWidth+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputSWidth = b.readLine();
                if(!inputSWidth.equals("--"))
                {
                    try
                    {
                        double s = Double.parseDouble(inputSWidth);
                        if(s<0)
                        {
                            debug("Screen Width cant be less than 0. Try Again!");
                            continue;
                        }
                        else
                        {
                            sWidth = s;
                        }
                    }
                    catch (Exception e)
                    {
                        debug("Wrong input. Try Again!");
                        continue;
                    }
                }

                debug("\n\nNew Config :" +
                        "\nDebug Mode : "+debugMode+
                        "\nPane Changer Mode : "+isPaneChangeTimerOn+
                        "\nPi-Hole Fetcher Task Sleep (Milliseconds) : "+piHoleStatsFetcherSleep+
                        "\nSystem Info Fetcher Task Sleep (Milliseconds) : "+systemStatsFetcherSleep+
                        "\nPane Changer Task Sleep (Milliseconds) : "+paneChangerTaskSleep+
                        "\nPi-Hole Telnet IP : "+telNetIP+
                        "\nPi-Hole Telnet Port : "+telNetPort+
                        "\nAPPLY SETTINGS? [Y/N]");

                String choice = b.readLine();
                if(choice.equalsIgnoreCase("Y"))
                {
                    FileWriter f = new FileWriter(new File("config"));
                    f.write(debugMode+"::"+isPaneChangeTimerOn+"::"+piHoleStatsFetcherSleep+"::"+systemStatsFetcherSleep+"::"+paneChangerTaskSleep+"::"+telNetIP+"::"+telNetPort+"::"+sHeight+"::"+sWidth+"::");
                    f.flush();
                    f.close();

                    debug("Applied Settings! Now run 'sudo ./piHoleLCDStat' to start!");
                }
                else
                {
                    debug("Abort!");
                }
                break;
            }
            b.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void debug(String txt)
    {
        System.out.println(txt);
    }
}
