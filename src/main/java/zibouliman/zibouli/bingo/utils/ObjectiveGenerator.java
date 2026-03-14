package zibouliman.zibouli.bingo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.entity.EntityDamageEvent;
import zibouliman.zibouli.bingo.Bingo;

import java.util.*;

/**
 * Gestionnaire de génération d'objectifs avec système de probabilités configurable
 */
public class ObjectiveGenerator {

    /**
     * Configuration des probabilités pour chaque type d'objectif
     * La somme doit égaler 100
     */
    public static class ObjectiveProbabilities {
        public int deathChance = 10;           // Mort par cause spécifique
        public int obtainItemChance = 52;      // Obtenir un item
        public int biomeChance = 12;            // Mourir dans un biome
        public int enchantChance = 3;          // Enchanter un item
        public int renameChance = 5;           // Renommer un item
        public int advancementChance = 18;
        /*public int deathChance = 0;           // Mort par cause spécifique
        public int obtainItemChance = 0;      // Obtenir un item
        public int biomeChance = 0;            // Mourir dans un biome
        public int enchantChance = 0;          // Enchanter un item
        public int renameChance = 0;           // Renommer un item
        public int advancementChance = 100;*/

        /**
         * Valide que les probabilités ajoutent up à 100
         */
        public boolean isValid() {
            int total = deathChance + obtainItemChance + biomeChance + enchantChance + renameChance + advancementChance;
            if (total != 100) {
                Bukkit.getLogger().warning("Les probabilités d'objectifs ne totalisent pas 100 : " + total);
                return false;
            }
            return true;
        }

        /**
         * Retourne un aperçu des probabilités
         */
        public String toString() {
            return String.format(
                "ObjectiveProbabilities{DEATH=%d%%, OBTAIN_ITEM=%d%%, BIOME=%d%%, ENCHANT=%d%%, RENAME=%d%%, ADvancement=%d%%}",
                deathChance, obtainItemChance, biomeChance, enchantChance, renameChance, advancementChance
            );
        }
    }

    private final Random random = new Random();
    public final ObjectiveProbabilities probabilities;
    private final ObjectiveRandomizer randomizer;

    public ObjectiveGenerator(ObjectiveProbabilities probabilities, ObjectiveRandomizer randomizer) {
        this.probabilities = probabilities;
        this.randomizer = randomizer;

        if (!probabilities.isValid()) {
            Bukkit.getLogger().warning("⚠️ Probabilités invalides : " + probabilities);
        }
    }

    /**
     * Génère un objectif aléatoire basé sur les probabilités
     */
    public BingoObjective generateRandomObjective() {
        int rand = random.nextInt(100);
        int cumulative = 0;

        // Mort
        cumulative += probabilities.deathChance;
        if (rand < cumulative) {
            EntityDamageEvent.DamageCause damageCause = randomizer.getRandomKillMethod();
            return new BingoObjective(WinCondition.DEATH, damageCause);
        }

        // Obtenir un item
        cumulative += probabilities.obtainItemChance;
        if (rand < cumulative) {
            List<Material> material = randomizer.getRandomObtainableItem();
            return new BingoObjective(WinCondition.OBTAIN_ITEM, material);
        }

        // Biome
        cumulative += probabilities.biomeChance;
        if (rand < cumulative) {
            Biome biome = randomizer.getRandomBiome();
            return new BingoObjective(WinCondition.BIOME, biome);
        }

        // Enchanter
        cumulative += probabilities.enchantChance;
        if (rand < cumulative) {
            return new BingoObjective(WinCondition.ENCHANT);
        }

        cumulative += probabilities.advancementChance;
        if (rand < cumulative) {
            return new BingoObjective(randomizer.getRandomAdvancement(),WinCondition.ADVANCEMENT);
        }


        // Renommer (défaut si aucun autre n'est sélectionné)
        return new BingoObjective(WinCondition.RENAME);
    }

    /**
     * Génère une liste d'objectifs
     */
    public List<BingoObjective> generateObjectives(int numberOfObjectives) {
        List<BingoObjective> objectives = new ArrayList<>();

        for (int i = 0; i < numberOfObjectives; i++) {
            objectives.add(generateRandomObjective());
        }

        return objectives;
    }
}

