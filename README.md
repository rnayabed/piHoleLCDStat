# piHoleLCDStat

A [Pi-Hole](https://pi-hole.net/) + System Info dash for [Raspberry Pi](https://www.raspberrypi.org/)

Built with JavaFX, Java

**This program uses the Pi-Hole Telnet API and NOT THE JSON API**

## How to install?

1. Download the latest zip from [releases](https://github.com/dubbadhar/piHoleLCDStat/releases).
2. Run `unzip piHoleLCDStat.zip` (Make sure you're in the same director)
3. Then `cd piHoleLCDStat`
4. Run `./install`, to install necessary fonts and run config.

## How to use?

1. `cd` into the same directory where `piHoleLCDStat.zip` was extracted
2. Run `./piHoleLCDStat`

## How to change Settings

1. `cd` into the same directory where `piHoleLCDStat.zip` was extracted
2. Run `./piHoleLCDStat_edit_config`

Currently the following settings/preferences can be changed :
* Debug Mode - Just prints some debug output to the console. Use if you encounter some issues, and then create an issue if you cant get rid of the problem
* Pane Changer Mode - Changes the current screen after certain interval. **Recommended for non-touch screens.**
* Screen Height, Screen Width
* PiHole Telnet IP and Port
* Font Size
* goodColour - Colour of gauges when Pi-Hole blocking is enabled.
* badColour - Colour of gauges when Pi-Hole blocking is disabled.
* piHoleStatsFetcherSleep - Change sleep terminal between Pi-Hole data fetched.
* systemStatsFetcherSleep - Change sleep terminal between System info fetched.
* paneChangerTaskSleep - Change sleep terminal between change of Pane (Works only if Pane Changer mode is enabled).
* Top Domains List Limit - No of Top Allowed Domains to be shown
* Top Clients List Limit - No of Top Clients to be shown
* Top Ads List Limit - No of Top Blocked Advertisements to be shown

## Run at Startup

### rc.local method
1. Open `/etc/rc.local` as root.
2. Just before `exit 0` add the following lines 
```
cd <piHoleLCDStat extracted Directory>
./piHoleLCDStat
```

### bashrc method
1. Open `~/.bashrc`.
2. Add the following lines at the end of file
```
cd <piHoleLCDStat extracted Directory>
./piHoleLCDStat
```

**This method is not recommeneded as `~/.bashrc` runs every time an SSH connection is made to the Pi. You can use Ctrl+C to always quit when a new piHoleLCDStat instance is made.**

## Screenshots

[3.2" 320x240 display](https://github.com/dubbadhar/piHoleLCDStat/blob/master/screenshots/3.2_320x240/README.md)

[7" 1024x600 display](https://github.com/dubbadhar/piHoleLCDStat/blob/master/screenshots/7_1024x600/README.md)

## Libraries Used
* [Medusa](https://github.com/HanSolo/Medusa) - Gauges

## License 

[GNU GPL v3](https://github.com/dubbadhar/piHoleLCDStat/blob/master/LICENSE) 

