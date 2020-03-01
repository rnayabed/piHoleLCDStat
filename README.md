# piHoleLCDStat

A Pi-Hole + System Info dash for Raspberry Pi

Built with JavaFX, Java

### How to install?

1. Download the latest zip from [releases](https://github.com/dubbadhar/piHoleLCDStat/releases).
2. Run `unzip piHoleLCDStat.zip` (Make sure you're in the same director)
3. Then `cd piHoleLCDStat`
4. Run `./install`, to run config and install necessary fonts.

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

### Screenshots

3.2" 320x240 display :

![s](https://github.com/dubbadhar/piHoleLCDStat/blob/master/screenshots/3.2_320x240/20200302_013928.jpg)
