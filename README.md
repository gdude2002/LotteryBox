LotteryBox
==========

My plugins: [WordWarning](https://github.com/gdude2002/WordWarning) | [Painter](https://github.com/gdude2002/Painter) | **LotteryBox**

---

LotteryBox is a chance-based rewards system. Players must somehow obtain
a chest key, and bring it to a chest designated lottery box, where they will
be able to use it to "open" the chest for a chance at a reward.

How these keys are obtained is up to you.

Each box can be configured separately - You may have named lottery boxes that
require named keys, boxes that are empty or have a chance at one out of several
rewards, or boxes that run commands instead of giving items - or any combination
of the above.

Things to do
============

* Chest data storage in json format
* Permissions support
* Ingame management
* Turning any held item into a key
* Chest-specific keys, chest-agnostic keys
* Adding chest keys to dungeon chest generation
* Optional support for item, command and soft currency (ingame money) rewards

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

---

My plugins: [WordWarning](https://github.com/gdude2002/WordWarning) | [Painter](https://github.com/gdude2002/Painter) | **LotteryBox**