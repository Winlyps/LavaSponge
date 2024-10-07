package winlyps.lavaSponge

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class LavaSpongeCommand(private val plugin: LavaSponge) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(ChatColor.RED.toString() + "Only players can use this command!")
            return true
        }

        if (!sender.hasPermission("sponge.lavasponge")) {
            sender.sendMessage(ChatColor.RED.toString() + "You do not have permission to use this command!")
            return true
        }

        val lavaSponge = ItemStack(Material.SPONGE)
        val meta = lavaSponge.itemMeta
        meta?.setDisplayName(ChatColor.RED.toString() + "Lava Sponge")
        lavaSponge.itemMeta = meta

        sender.inventory.addItem(lavaSponge)
        sender.sendMessage(ChatColor.GREEN.toString() + "You have received a Lava Sponge!")
        return true
    }
}