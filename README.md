# DeathMaps

**Compatible with Minecraft 1.16.4**

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

__Note: You must provide the Minecraft Server jar (Craftbukkit / Spigot / Paper) into the project root directory as "nms.jar"__

You can compile it yourself by using Maven and simply running the following command in the project folder:
(The jar file will be located in /targets/)

```
mvn
```

You can also download the latest pre compiled binary over on the releases section of Github, located here:

https://github.com/Puremin0rez/DeathMaps/releases

## Can I use your code?

I recommend asking BillyGalbreth directly - as there was no license packaged with the original project.

You can use my specific bits of code however you'd like. MIT.

## Acknowledgements

* Incredible thanks to Billy for always helping out the community and coming up with great ideas. My survival inspiration <3
* You, for reading this and checking out the project.
