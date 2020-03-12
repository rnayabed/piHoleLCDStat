package piHoleLCDStat;

import java.io.*;

public class configEditor {

    public static void main(String... args)
    {
        try
        {
            println("piHoleLCDStat"+
                    "\nVersion : "+programInfo.VERSION+
                    "\nAuthor : "+programInfo.AUTHOR+
                    "\nSource : "+programInfo.REPO_LINK+
                    "\nBuild Date : "+programInfo.BUILD_DATE+"\n");

            boolean debugMode,isPaneChangeTimerOn;
            long piHoleStatsFetcherSleep,systemStatsFetcherSleep,paneChangerTaskSleep;
            String telNetIP;
            int telNetPort;
            double sHeight,sWidth;
            double fontSize;

            int topDomainsLimit, topClientsLimit, topAdsLimit;

            String goodColour,badColour;

            println("Reading Current config ...");

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

            fontSize = Double.parseDouble(conf[9]);

            goodColour = conf[10];
            badColour = conf[11];

            topDomainsLimit = Integer.parseInt(conf[12]);
            topClientsLimit = Integer.parseInt(conf[13]);
            topAdsLimit = Integer.parseInt(conf[14]);

            println("... Done!");

            BufferedReader b = new BufferedReader(new InputStreamReader(System.in));

            while(true)
            {
                println("\nTurn on debug mode? (does nothing to the UI, use if something goes wrong)\n" +
                        "Current Value: '"+debugMode+"'\n" +
                        "Enter '1' for ON, '0' for OFF, '--' to not change at all.\n");

                String inputDebugMode = b.readLine();
                if(inputDebugMode.equals("1")) debugMode = true;
                else if(inputDebugMode.equals("0")) debugMode = false;
                else if(!inputDebugMode.equals("--"))
                {
                    println("Wrong input. Try Again!");
                    continue;
                }

                println("\nTurn on Pane Changer? Recommended for non-touchscreen displays. (Changes current pane after every n seconds)\n" +
                        "Current Value: '"+isPaneChangeTimerOn+"'\n" +
                        "Enter '1' for ON, '0' for OFF, '--' to not change at all.\n");

                String inputIsPaneChangeTimerOn = b.readLine();
                if(inputIsPaneChangeTimerOn.equals("1")) isPaneChangeTimerOn = true;
                else if(inputIsPaneChangeTimerOn.equals("0")) isPaneChangeTimerOn = false;
                else if(!inputIsPaneChangeTimerOn.equals("--"))
                {
                    println("Wrong input. Try Again!");
                    continue;
                }

                println("\nEnter duration in milliseconds for sleep between fetching Pi-Hole Info\n" +
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
                            println("Sleep duration cannot be less than 1. Try Again!");
                            println("Wrong input. Try Again!");
                            continue;
                        }
                        else
                        {
                            piHoleStatsFetcherSleep = sleepDur;
                        }
                    }
                    catch (Exception e)
                    {
                        println("Wrong input. Try Again!");
                        continue;
                    }
                }


                println("\nEnter duration in milliseconds for sleep between fetching System Info\n" +
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
                            println("Sleep duration cannot be less than 1. Try Again!");
                            println("Wrong input. Try Again!");
                            continue;
                        }
                        else
                        {
                            systemStatsFetcherSleep = sleepDur;
                        }
                    }
                    catch (Exception e)
                    {
                        println("Wrong input. Try Again!");
                        continue;
                    }
                }


                if(isPaneChangeTimerOn)
                {
                    println("\nEnter duration in milliseconds for sleep between chaning screens\n" +
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
                                println("Sleep duration cannot be less than 1. Try Again!");
                                println("Wrong input. Try Again!");
                                continue;
                            }
                            else
                            {
                                paneChangerTaskSleep = sleepDur;
                            }
                        }
                        catch (Exception e)
                        {
                            println("Wrong input. Try Again!");
                            continue;
                        }
                    }
                }

                println("\nEnter Pi-Hole Telnet IP (Set it to 127.0.0.1 if you're going to run this on the same device as Pi-Hole)\n" +
                        "Current Value: '"+telNetIP+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputTelNetIP = b.readLine();
                if(!inputTelNetIP.equals("--"))
                {
                    telNetIP = inputTelNetIP;
                }

                println("\nEnter Pi-Hole Telnet Port (Set it to 4711 if you're going to run this on the same device as Pi-Hole)\n" +
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
                        println("Wrong input. Try Again!");
                        continue;
                    }
                }

                println("\nEnter Screen Height in Pixels\n" +
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
                            println("Screen Height cant be less than 0. Try Again!");
                            continue;
                        }
                        else
                        {
                            sHeight = s;
                        }
                    }
                    catch (Exception e)
                    {
                        println("Wrong input. Try Again!");
                        continue;
                    }
                }

                println("\nEnter Screen Width in Pixels\n" +
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
                            println("Screen Width cant be less than 0. Try Again!");
                            continue;
                        }
                        else
                        {
                            sWidth = s;
                        }
                    }
                    catch (Exception e)
                    {
                        println("Wrong input. Try Again!");
                        continue;
                    }
                }

                println("\nEnter Font Size in Pixels (Set it to 15 if you're using a 320x240 3.2\" screen, try changing if you find it very hard to read)\n" +
                        "Current Value: '"+fontSize+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputFontSize = b.readLine();
                if(!inputFontSize.equals("--"))
                {
                    try
                    {
                        double s = Double.parseDouble(inputFontSize);
                        if(s<0)
                        {
                            println("Font size cant be less that 0. Try Again!");
                            continue;
                        }
                        else
                        {
                            fontSize = s;
                        }
                    }
                    catch (Exception e)
                    {
                        println("Wrong input. Try Again!");
                        continue;
                    }
                }

                println("\nEnter goodColour HTML Color (Color shown across Gauges when Pi-Hole is Enabled)\n"+
                        "Skip if you don't understand (Read the github readme :P)\n"+
                        "\nCurrent Value: '"+goodColour+"'\n"+
                        "Enter '--' to not change at all.\n");

                String goodColourInput = b.readLine();
                if(!goodColourInput.equals("--"))
                {
                    if(goodColourInput.startsWith("#") && goodColourInput.length()==7)
                    {
                        goodColour = goodColourInput;
                    }
                    else
                    {
                        println("Invalid HTML Color code. Skip if you dont understand, or read GitHub Readme!");
                        continue;
                    }
                }

                println("\nEnter badColour HTML Color (Color shown across Gauges when Pi-Hole is Disabled)\n"+
                        "Skip if you don't understand (Read the github readme :P)\n"+
                        "\nCurrent Value: '"+badColour+"'\n"+
                        "Enter '--' to not change at all.\n");

                String badColourInput = b.readLine();
                if(!badColourInput.equals("--"))
                {
                    if(badColourInput.startsWith("#") && badColourInput.length()==7)
                    {
                        badColour = badColourInput;
                    }
                    else
                    {
                        println("Invalid HTML Color code. Skip if you dont understand, or read GitHub Readme!");
                        continue;
                    }
                }

                println("\nEnter Top-Domains List Limit.\n" +
                        "Current Value: '"+topDomainsLimit+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputTopDomainsLimit = b.readLine();
                if(!inputTopDomainsLimit.equals("--"))
                {
                    try
                    {
                        int s = Integer.parseInt(inputTopDomainsLimit);
                        if(s<1)
                        {
                            println("Limit can't cant be less that 1. Try Again!");
                            continue;
                        }
                        else
                        {
                            topDomainsLimit = s;
                        }
                    }
                    catch (Exception e)
                    {
                        println("Wrong input. Try Again!");
                        continue;
                    }
                }

                println("\nEnter Top-Clients List Limit.\n" +
                        "Current Value: '"+topClientsLimit+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputTopClientsLimit = b.readLine();
                if(!inputTopClientsLimit.equals("--"))
                {
                    try
                    {
                        int s = Integer.parseInt(inputTopClientsLimit);
                        if(s<1)
                        {
                            println("Limit can't cant be less than 1. Try Again!");
                            continue;
                        }
                        else
                        {
                            topClientsLimit = s;
                        }
                    }
                    catch (Exception e)
                    {
                        println("Wrong input. Try Again!");
                        continue;
                    }
                }

                println("\nEnter Top-Ads List Limit.\n" +
                        "Current Value: '"+topAdsLimit+"'\n" +
                        "Enter '--' to not change at all.\n");

                String inputTopAdsLimit = b.readLine();
                if(!inputTopAdsLimit.equals("--"))
                {
                    try
                    {
                        int s = Integer.parseInt(inputTopAdsLimit);
                        if(s<1)
                        {
                            println("Limit can't cant be less that 1. Try Again!");
                            continue;
                        }
                        else
                        {
                            topAdsLimit = s;
                        }
                    }
                    catch (Exception e)
                    {
                        println("Wrong input. Try Again!");
                        continue;
                    }
                }



                println("\n\nNew Config :" +
                        "\ndebug Mode : "+debugMode+
                        "\nPane Changer Mode : "+isPaneChangeTimerOn+
                        "\nPi-Hole Fetcher Task Sleep (Milliseconds) : "+piHoleStatsFetcherSleep+
                        "\nSystem Info Fetcher Task Sleep (Milliseconds) : "+systemStatsFetcherSleep+
                        "\nPane Changer Task Sleep (Milliseconds) : "+paneChangerTaskSleep+
                        "\nPi-Hole Telnet IP : "+telNetIP+
                        "\nPi-Hole Telnet Port : "+telNetPort+
                        "\nScreen Height : "+sHeight+
                        "\nScreen Width : "+sWidth+
                        "\nFont Size : "+fontSize+
                        "\nGood Color Code : "+goodColour+
                        "\nBad Color Code : "+badColour+
                        "\nTop Domains List Limit : "+topDomainsLimit+
                        "\nTop Clients List Limit : "+topClientsLimit+
                        "\nTop Ads List Limit : "+topAdsLimit+
                        "\nAPPLY SETTINGS? [Y/N]");

                String choice = b.readLine();
                if(choice.equalsIgnoreCase("Y"))
                {
                    FileWriter f = new FileWriter(new File("config"));
                    f.write(debugMode+"::"+isPaneChangeTimerOn+"::"+piHoleStatsFetcherSleep+"::"+systemStatsFetcherSleep+"::"+paneChangerTaskSleep+"::"+telNetIP+"::"+telNetPort+"::"+sHeight+"::"+sWidth+"::"+fontSize+"::"+goodColour+"::"+badColour+"::"+topDomainsLimit+"::"+topClientsLimit+"::"+topAdsLimit+"::");
                    f.flush();
                    f.close();

                    println("Applied Settings! Now run './piHoleLCDStat' to start!");
                }
                else
                {
                    println("Abort!");
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

    private static void println(String txt)
    {
        System.out.println(txt);
    }
}
