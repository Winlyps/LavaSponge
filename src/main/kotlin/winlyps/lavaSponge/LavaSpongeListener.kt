package winlyps.lavaSponge

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LavaSpongeListener(private val plugin: LavaSponge) : Listener {

    private val executorService = Executors.newSingleThreadExecutor()

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val placedBlock = event.blockPlaced
        val itemInHand = event.itemInHand

        if (itemInHand.type == Material.SPONGE && itemInHand.itemMeta?.displayName == ChatColor.RED.toString() + "Lava Sponge") {
            executorService.submit {
                drainLava(placedBlock)
                plugin.server.scheduler.runTask(plugin, Runnable {
                    placedBlock.type = Material.AIR
                    player.sendMessage(ChatColor.GREEN.toString() + "Lava Sponge has drained the lava!")
                })
            }
        }
    }

    private fun drainLava(block: Block) {
        val queue: Queue<Block> = ConcurrentLinkedQueue()
        val visited = mutableSetOf<Block>()
        val maxDistance = 50 // Adjust the maximum distance as needed

        queue.add(block)
        visited.add(block)

        val blocksToUpdate = mutableListOf<Block>()

        while (queue.isNotEmpty()) {
            val currentBlock = queue.poll()
            if (isLavaRelated(currentBlock.type)) {
                blocksToUpdate.add(currentBlock)
            }

            for (relativeBlock in currentBlock.getRelativeBlocks()) {
                if (!visited.contains(relativeBlock) && isWithinRadius(block, relativeBlock, maxDistance)) {
                    visited.add(relativeBlock)
                    queue.add(relativeBlock)
                }
            }
        }

        // Batch block updates
        plugin.server.scheduler.runTask(plugin, Runnable {
            for (blockToUpdate in blocksToUpdate) {
                blockToUpdate.type = Material.AIR
            }
        })
    }

    private fun isLavaRelated(material: Material): Boolean {
        return material == Material.LAVA || material == Material.MAGMA_BLOCK
        // Add other lava-related blocks if necessary
    }

    private fun isWithinRadius(center: Block, target: Block, radius: Int): Boolean {
        val dx = Math.abs(center.x - target.x)
        val dy = Math.abs(center.y - target.y)
        val dz = Math.abs(center.z - target.z)
        return dx + dy + dz <= radius
    }

    private fun Block.getRelativeBlocks(): List<Block> {
        return listOf(
                this.getRelative(0, 0, 1),
                this.getRelative(0, 0, -1),
                this.getRelative(1, 0, 0),
                this.getRelative(-1, 0, 0),
                this.getRelative(0, 1, 0),
                this.getRelative(0, -1, 0)
        )
    }

    fun shutdown() {
        executorService.shutdown()
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}