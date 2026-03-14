package zibouliman.zibouli.bingo.utils;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Interface pour les méthodes de génération d'objectifs aléatoires
 * Permet de découpler la logique de génération des probabilités
 */
public interface ObjectiveRandomizer {

    /**
     * Retourne une méthode de mort aléatoire
     */
    EntityDamageEvent.DamageCause getRandomKillMethod();

    /**
     * Retourne un item obtenu aléatoire
     */
    List<Material> getRandomObtainableItem();

    /**
     * Retourne un biome aléatoire
     */
    Biome getRandomBiome();

    List<String> getRandomAdvancement();
}

