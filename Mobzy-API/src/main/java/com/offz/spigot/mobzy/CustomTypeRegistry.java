/*
package com.offz.spigot.mobzy;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import net.minecraft.server.v1_15_R1.*;

import java.util.Map;

public class CustomTypeRegistry {
    public static EntityTypes injectNewEntity(String name, String extendFrom, Class<? extends Entity> custom, EntityTypes.b<Entity> b) {
        name = name.toLowerCase();
        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY).types();
        dataTypes.put("minecraft:" + name, dataTypes.get(extendFrom));
        EntityTypes.a<Entity> a = EntityTypes.a.a(b, EnumCreatureType.MISC);

        return IRegistry.a(IRegistry.ENTITY_TYPE, name, a.a(name));
    }
}
*/
