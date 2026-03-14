package zibouliman.zibouli.bingo.utils;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Array;
import java.util.List;

public class BingoObjective {
    private WinCondition type;
    private List<Material> material;
    private EntityDamageEvent.DamageCause damageCause;
    private List<String> advancement;
    private Biome biome;


    public BingoObjective(WinCondition type, List<Material> material) {
        this.type = type;
        this.material = material;
    }

    public BingoObjective(WinCondition type, EntityDamageEvent.DamageCause damageCause) {
        this.type = type;
        this.damageCause = damageCause;
    }

    public BingoObjective(WinCondition type, Biome biome) {
        this.type = type;
        this.biome = biome;
    }
    public BingoObjective( List<String> advancement,WinCondition type) {
        this.type = type;
        this.advancement = advancement;
    }

    public BingoObjective(WinCondition winCondition) {
        this.type = winCondition;
    }

    public WinCondition getType() {
        return type;
    }

    public List<Material> getMaterial() {
        return material;
    }

    public List<String> getAdvancement() {
        return advancement;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }

        public Biome getBiome() {
            return biome;
        }

}

