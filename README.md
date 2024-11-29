
---

>[!IMPORTANT]
>:warning: This project is currently not finished (~90%)!
>
>This plugin currently only supports the `GERMAN 🇩🇪` language!
>
>If you encounter any bugs, feel free to open a new [Issue](https://github.com/NYC1809/ShopRotation-Remake/issues) in this repository.

---


# ShopRotation-Remake
A minecraft plugin to create Chests where the server staff can create public community item goals!

## What is the ShopRotation Plugin?

"ShopRotation-Remake" is a minecraft spigot plugin to create community item goals for every member of the server.
Please create a new [Issue](https://github.com/NYC1809/ShopRotation-Remake/issues) if you want to request new features or functions!

**I would be very happy about any feedback regarding this repository!**

## Installation

- Save the provided .jar file to your server's `/plugins` directory. Alternatively, you can compile the .jar file yourself by cloning this repository.
- Restart (⚠️ Do not use `/reload` click [here](https://gist.github.com/A248/0085b1dff1bd3f93876e435d0d10b5d4) to see why) the server to make this plugin fully functionally.
- This will create a new `/plugins/ShopRotation-Remake` folder, which will contain the SQLITE database.

## Important Notes

- This plugin does not use a `config.yml` file to modify settings, as every setting could be modified ingame due to a gui.
- Why `SQLITE` and not for example `MongoDB or MySQL`? SQLITE is a very basic and simple SQL database. Therefore it was quite easy for me personally to implement it in my project. SQLITE still provides way enough operation-speed to work as it should. This plugin does not require a highspeed and very redundant database, as it only performs a little SQL-Requests.
- It is not possible to break the chest by destroying it. Even though it is possible to change the type of the chest for example with the `default minecraft /setBlock command` or by changing it within the settings gui.
- Why `-Remake` as there are no other exisiting Projects? There has been a private `ShopRotation` project which never got completed or released.
- ⚠️ Only directly access the database when you know what you are doing. Otherwise it could potentially corrupt the database!
- Backup your database regularly to have a backup if anything breaks within the plugin.

## Commands
This plugin operates with the `/srChest` command.

- `/srChest create <name> <block>` - creates a ShopRotation chest. Within this chest staff can modify the chest's properties.
- `/srChest get` - prints a List of all existing ShopRotation chests. Simply click on the provided chestUUID to copy it to your local clipboard.
- `/srChest remove <name/uuid>` - removes the specified chest.
- `/srChest add <uuid> <material/block> <requiredAmount>` - adds the specified item to the ShopRotation chest. `requiredAmount` describes the amount this itemgoal needs to be finished.
- `/srChest addthis <uuid> <requiredAmount>` - gets the item you are currently holding and adds it to the specified ShopRotation chest. This command also copies the items Properties directly to the itemgoal!
- `/srChest adminsettings <uuid>` - opens the gui to modify the whole ShopRotation chest. In this gui server operators can modify every `setting/item/reward`!
- `/srChest help` - prints all available commands.

## About ShopRotation-Remake

## Customization


>[!IMPORTANT]
> ⚠️ This project's code may not look the best. I am currently trying to improve coding. Therefore it is the best to just practice it over and over again.
>Even though it is a fully functional and operating minecraft spigot plugin!
>
>This repository is to personally improve style of code and learn (minecraft-)coding.
>
>**Thank you very much for understanding and patience reading this note! ❤️**
