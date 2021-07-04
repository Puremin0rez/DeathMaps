# DeathMaps

**Compatible with Minecraft 1.17**

This is a continuation / maintained version of the original plugin created by BillyGalbreth from Pl3xcraft.

The goal of this project is to maintain the original projects fully working operation and add new features to improve upon the ideas
and philosophies of the original project.

This continuation is entirely made in mind for my own personal server - but should be fully operational and usable for any server.

## What's different?

This is a list of everything that has been altered from the original from a users perspective:
* The players name now displays in the tooltip of the map
* The coordinates of the death marker now displays in the tooltip of the map
* The date/time of death now displays in the tooltip of the map
* The coloring for the tooltip has been improved and formatted
* The map will now go into your off hand by default, and fallback to your inventory if your offhand wasn't empty (keep inv / soulbound)
* The death maker is now a red cross instead of a white cross
* The death map will only be created if you actually dropped any items when you died
* Improved compatibility with plugins that interfere with dead players items - such as death chest plugins

This project is a direct drop in replacement for the original. You can upgrade without any loss or worries.

## How do I obtain it?

You can download stable releases via Github Releases, [located here.](https://github.com/Puremin0rez/DeathMaps/releases)

You can compile it by performing the following steps:

Download [BuildTools](https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar) to any directory and run the following command:
```
java -jar BuildTools.jar --rev 1.17
```
(Note: This **WILL** take some time to complete)

Finish the process by running the following command in the project directory:
```
./gradlew clean build
```

(The jar file will be located in `/build/libs/`)

## Can I use your code?

I recommend asking [BillyGalbreth](https://github.com/BillyGalbreath) directly - as there was no license packaged with the original project.

## Acknowledgements

* Incredible thanks to [Billy](https://github.com/BillyGalbreath) for always helping out the community and coming up with great ideas. My survival inspiration <3
* [Contributors](https://github.com/Puremin0rez/DeathMaps/graphs/contributors) for helping improve the project.
* You, for reading this and checking out the project.
