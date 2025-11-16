package net.pitan76.simplecables76

import net.pitan76.mcpitanlib.api.registry.v2.CompatRegistryV2
import net.pitan76.mcpitanlib.api.util.CompatIdentifier
import net.pitan76.mcpitanlib.fabric.ExtendModInitializer
import net.pitan76.simplecables76.block.BlockEntities
import net.pitan76.simplecables76.block.Blocks
import net.pitan76.simplecables76.item.Items

object SimpleCables : ExtendModInitializer() {

    const val MOD_ID: String = "simplecables76";
    const val MOD_NAME: String = "SimpleCables";

    lateinit var registry: CompatRegistryV2;

    override fun init() {
        registry = super.registry;

        Blocks.init();
        Items.init();
        BlockEntities.init();

    }

    // ----
    /**
     * @param path The path of the id
     * @return The id
     */
    @JvmStatic
    fun _id(path: String): CompatIdentifier {
        return CompatIdentifier.of(MOD_ID, path);
    }

    override fun getId(): String = MOD_ID

    override fun getName(): String = MOD_NAME
}
