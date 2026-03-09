package zibouliman.zibouli.bingo.utils;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Array;
import java.util.List;

public class BingoObjective {
    private WinCondition type;
    private List<Material> material;
    private EntityDamageEvent.DamageCause damageCause;

    public BingoObjective(WinCondition type, List<Material> material) {
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

    public List<Material> getMaterial() {
        return material;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }
}

