# piHoleLCDStat

A [Pi-Hole](https://pi-hole.net/) + System Info dash for [Raspberry Pi](https://www.raspberrypi.org/)

Built with JavaFX, Java

**This program uses the Pi-Hole Telnet API and NOT THE JSON API**

**Raspberry Pi : It is recommended to use the `Raspbian Desktop with Recommended Software installed` image for Raspberry Pi, as I never used the lite image to test it out. You can just turn on `Console Auto-login` from `raspi-config` to avoid loading the DE**

### How to install?

1. Download the latest zip from [releases](https://github.com/dubbadhar/piHoleLCDStat/releases).
2. Run `unzip piHoleLCDStat.zip` (Make sure you're in the same director)
3. Then `cd piHoleLCDStat`
4. Run `./install`, to install necessary fonts and run config.

### How to use?

1. `cd` into the same directory where `piHoleLCDStat.zip` was extracted
2. Run `./piHoleLCDStat`

### How to change Settings

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

### Run at Startup

1. Open `/etc/rc.local` as root.
2. just before `exit 0` add the following lines 
```
cd <piHoleLCDStat extracted Directory>
./piHoleLCDStat
```
### Screenshots

[3.2" 320x240 display](https://github.com/dubbadhar/piHoleLCDStat/blob/master/screenshots/3.2_320x240/README.md)

[7" 1024x600 display](https://github.com/dubbadhar/piHoleLCDStat/blob/master/screenshots/7_1024x600/README.md)

***Screenshots may be rotated, i tried countless times to fix it, but couldn't fix***

### Libraries Used
* [Medusa](https://github.com/HanSolo/Medusa) - Gauges

### License 

[GNU GPL v3](https://github.com/dubbadhar/piHoleLCDStat/blob/master/LICENSE) 

