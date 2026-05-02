package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider
import net.pitan76.mcpitanlib.api.block.v3.CompatBlock
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings

abstract class AbstractCable(settings: CompatibleBlockSettings) : CompatBlock(settings), ExtendBlockEntityProvider {

}
