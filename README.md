LotteryBox
==========

My plugins: [WordWarning](https://github.com/gdude2002/WordWarning) | [Painter](https://github.com/gdude2002/Painter) | **LotteryBox**

---

**This plugin was inspired by a [plugin request thread by Jduffygames](http://forums.bukkit.org/threads/votingkeys.306669/).**

LotteryBox is a chance-based rewards system. Players must somehow obtain
a chest key, and bring it to a chest designated as a lottery box, where they will
be able to use it to "open" the chest for a chance at a reward.

How these keys are obtained is up to you.

Each box can be configured separately - You may have named lottery boxes that
require named keys, boxes that are empty or have a chance at one out of several
rewards, or boxes that run commands instead of giving items - or any combination
of the above.

The latest jar is always available [here](http://bamboo.gserv.me/browse/PLUG-LOTBOX/latest/artifact/JOB1/Version-agnostic-jar/LotteryBox.jar).
Remember, your server must be running **Java 7 or later**. If you need me to compile for lower versions, let me know and I'll make it happen.

Things to do
============

* Adding chest keys to dungeon chest generation
* Probably some other stuff I forgot.

Compiling
=========

Compilation of the plugin is fairly simple.

1. Install [the JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) (version 1.7 or higher)
2. Install [Gradle](http://www.gradle.org/)
3. Ensure that the JDK and Gradle are on your system's PATH
4. Open a terminal, `cd` to the project files and `gradle clean build jar fatjar`
5. You'll find the jar in `build/libs/`

I use Gradle instead of Maven simply because I don't like Maven, and Gradle is much easier to work with.
If you need to do Maven things, you can do `gradle install`, which will generate poms and install the plugin
into your local maven repository. Poms are generated in `build/poms/`.

Buy me a soda
=============

Sometimes people ask me to accept donations. If you like what I do, you can donate [here](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=85GN242EDQSCJ).

---

My plugins: [WordWarning](https://github.com/gdude2002/WordWarning) | [Painter](https://github.com/gdude2002/Painter) | **LotteryBox**
