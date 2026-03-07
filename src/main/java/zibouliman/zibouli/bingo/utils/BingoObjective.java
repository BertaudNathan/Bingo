package zibouliman.zibouli.bingo.utils;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;

public class BingoObjective {
    private WinCondition type;
    private Material material;
    private EntityDamageEvent.DamageCause damageCause;

    public BingoObjective(WinCondition type, Material material) {
        this.type = type;
        this.material = material;
    }

    public BingoObjective(WinCondition type, EntityDamageEvent.DamageCause damageCause) {
        this.type = type;
        this.damageCause = damageCause;
    }

    public WinCondition getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }
}

