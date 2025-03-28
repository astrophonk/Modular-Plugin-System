

![ChatGPT Image Mar 29, 2025, 12_38_25 AM](https://github.com/user-attachments/assets/d2e8cbf0-5bf6-40e6-b3b8-67137ad1680e)



# Modular Plugin System

A versatile and dynamic plugin framework designed for Minecraft (Paper/Spigot) servers. Created by MemoryDLR, this system allows server administrators and developers to easily manage custom modules, enabling or disabling them at runtime through an intuitive in-game GUI.

---

## 🚀 Features

- **Dynamic Module Loading:** Modules are lazily loaded, only initialized when needed.
- **Easy Management:** Enable or disable modules seamlessly via an in-game GUI.
- **Command Handling:** Modules can optionally have custom commands, automatically registered and unregistered based on their enabled state.
- **Persistent Configuration:** Module states persist across server restarts.

---

## 📦 Installation

1. Clone or download the project.
2. Navigate to the project directory.
3. Build the plugin:
   ```bash
   ./gradlew clean jar
   ```
4. Place the generated JAR file (`build/libs/YourPluginName-1.0-SNAPSHOT.jar`) into your Minecraft server's `plugins` folder.
5. Start your server to generate the default configuration.

---

## 🛠 Creating Modules

Modules follow a simple interface-based design. Here's how you create your own module:

### Step 1: Module Class

Create a new class that implements the `Module` interface:

```java
package dev.memorydealer.modules.example;

import dev.memorydealer.modules.Module;
import org.bukkit.plugin.java.JavaPlugin;

public class ExampleModule implements Module {

    private final JavaPlugin plugin;
    private boolean enabled;

    public ExampleModule(JavaPlugin plugin, boolean enabled) {
        this.plugin = plugin;
        this.enabled = enabled;
    }

    @Override
    public void start() {
        if (!enabled) return;
        // Initialization code (register listeners, tasks)
    }

    @Override
    public void stop() {
        // Cleanup code (unregister listeners, tasks)
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) start();
        else stop();
    }

    @Override
    public String getName() {
        return "Example Module";
    }

    // Optional: commands (see next section)
}
```

---

### Step 2: Optional Commands

If your module requires commands, override the optional `getCommands()` method:

```java
@Override
public Map<String, CommandExecutor> getCommands() {
    Map<String, CommandExecutor> commands = new HashMap<>();
    commands.put("example", new ExampleCommand());
    return commands;
}
```

**If no commands are needed**, simply don't override this method; the module interface provides a default empty implementation.

#### Example Command Executor:

```java
package dev.memorydealer.modules.example.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ExampleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§aExample command executed!");
        return true;
    }
}
```

---

## 🔄 Managing Modules

### In-game GUI

- Execute `/modules` in-game to open the module management GUI.
- Click modules to toggle their state (enabled/disabled).
- Changes persist through server restarts.

---

## ⚙️ Configuration

Module states are saved in a JSON file (`config.json`) inside your plugin's data folder (`plugins/YourPluginName`).

Example configuration:
```json
{
  "ExampleModule": true,
  "NetherPortalModule": false,
  "ChillZoneModule": true
}
```

---

## 🖼️ Screenshots

![image](https://github.com/user-attachments/assets/dba3edb7-7d37-49aa-9646-02d14d48735c)

![image](https://github.com/user-attachments/assets/7fd94b0b-ab04-4e97-b134-c8f3896a7911)

![image](https://github.com/user-attachments/assets/057ee431-a1b6-42af-91fa-8eb866fb428b)


---

## 📜 License

This project is open-source. See `LICENSE` for more details.

---

**Enjoy building powerful and dynamic plugins with the Modular Plugin System! 🎉**
