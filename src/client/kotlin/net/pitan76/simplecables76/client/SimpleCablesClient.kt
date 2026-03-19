package net.pitan76.simplecables76.client

import net.fabricmc.api.ClientModInitializer
import net.pitan76.mcpitanlib.api.client.registry.CompatRegistryClient
import net.pitan76.simplecables76.block.Blocks

class SimpleCablesClient : ClientModInitializer {

    override fun onInitializeClient() {
        CompatRegistryClient.registerCutoutBlock(Blocks.ENERGY_CABLE.get())
    }
}
