# bLib

bLib is a library I have made to help ease java development, especially development of spigot/bukkit plugins.

# DO NOT USE THIS LIBRARY!

I am only providing legacy support for this library (making it work on new versions), and will not be working on it.
It contains some very bad / buggy code. Please consider using a library listed below.

Suggested replacement libraries:
Commands - [OctoPvP/Commander](https://github.com/OctoPvP/Commander) / [Incendo/cloud](https://github.com/Incendo/cloud) / [ACF](https://github.com/aikar/commands)
GUIs - [TriumphGUI](https://github.com/TriumphTeam/triumph-gui)

## Quick links

[Bukkit/Spigot/Paper Library](https://github.com/Badbird-5907/bLib/tree/master/bLib-Bukkit/src/main/java/net/badbird5907/blib) <br>
[Menu framework](https://github.com/Badbird-5907/bLib/tree/master/bLib-Bukkit/src/main/java/net/badbird5907/blib/menu)<br>
[Command framework](https://github.com/Badbird-5907/bLib/tree/master/bLib-Bukkit/src/main/java/net/badbird5907/blib/command)<br>
[Commons](https://github.com/Badbird-5907/bLib/tree/master/bLib-Common/src/main/java/net/badbird5907/blib)<br>
[Bukkit/Bungee Commons](https://github.com/Badbird-5907/bLib/tree/master/bLib-ServerCommons/src/main/java/net/badbird5907/blib/util)<br>

## Dependency

### Maven

```xml
<dependency>
	<groupId>net.badbird5907</groupId>
	<artifactId>bLib-Parent</artifactId>
	<version>2.1.11-REL</version>
</dependency>
```

### Gradle

#### Groovy DSL

```groovy
implementation 'net.badbird5907:bLib:2.1.11-REL'
```

#### Kotlin DSL

```kotlin
implementation("net.badbird5907:bLib:2.1.11-REL")
```

## Usage (Bukkit)

```java
public class YourPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		bLib.create(this); // that's it...
		// OPTIONAL:
		bLib.getCommandFramework().registerCommandsInPackage("your.commands.package.here");
		// Creating an ItemStack
		// XMaterial is for backwards compatability for versions
		ItemStack itemStack = new ItemBuilder(XMaterial.STICK.parseMaterial()).name(CC.GOLD + "KB Stick").enchant(Enchantment.KNOCKBACK, 100).build();
	}
}
```
