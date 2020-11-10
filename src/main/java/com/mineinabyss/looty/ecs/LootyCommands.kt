package com.mineinabyss.looty.ecs

import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.looty.ecs.config.LootyTypes
import com.mineinabyss.mobzy.mobzy

@ExperimentalCommandDSL
object LootyCommands : IdofrontCommandExecutor() {
    override val commands = commands(mobzy) {
        "looty" {
            "item" {
                val type by optionArg(options = LootyTypes.types) {
                    parseErrorMessage = { "No such entity: $passed" }
                }

                playerAction {
//                    val lootyEntity = LootyTypes[type].instantiate()
                    player.inventory.addItem(LootyTypes[type].instantiateItemStack())
                }
            }
        }
    }
}