package zibouliman.zibouli.bingo.commands;


import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.jspecify.annotations.NonNull;
import zibouliman.zibouli.bingo.Bingo;
import zibouliman.zibouli.bingo.helpers.Helpers;
import zibouliman.zibouli.bingo.utils.BingoObjective;
import zibouliman.zibouli.bingo.utils.ObjectiveGenerator;
import zibouliman.zibouli.bingo.utils.ObjectiveRandomizer;
import zibouliman.zibouli.bingo.utils.PlayerResetUtils;
import zibouliman.zibouli.bingo.utils.WinCondition;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.*;
import static zibouliman.zibouli.bingo.helpers.Helpers.getDisplayNameForMaterial;

public class Start implements CommandExecutor, ObjectiveRandomizer {
    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] strings) {
        int numberOfObjectives = 1; // Par défaut : 1 objectif

        if (strings.length > 0) {
            try {
                numberOfObjectives = Integer.parseInt(strings[0]);
                if (numberOfObjectives < 1) {
                    commandSender.sendMessage("§cLe nombre d'objectifs doit être au moins 1.");
                    return false;
                }
                if (numberOfObjectives > 10) {
                    commandSender.sendMessage("§cLe nombre d'objectifs ne peut pas dépasser 10.");
                    return false;
                }
            } catch (NumberFormatException e) {
                commandSender.sendMessage("§cArgument invalide. Utilisez: /start-bingo <nombre>");
                return false;
            }
        }

        startGame(numberOfObjectives);
        return true;
    }

    public Material BingoMaterial;

    public void startGame(int numberOfObjectives) {
        Bingo plugin = (Bingo) getPluginManager().getPlugin("Bingo");
        if (plugin == null) {
            Bukkit.getLogger().severe("Plugin Bingo non trouve, impossible de demarrer la partie.");
            return;
        }

        prepareFreshSessionWorlds(plugin);
        if (Bingo.BingoWorld == null) {
            Bukkit.getLogger().severe("Impossible de creer le monde de partie.");
            return;
        }

        // Reinitialiser les achievements et spawns de tous les joueurs
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(Bingo.BingoWorld.getSpawnLocation()));

        Helpers.resetScoreboard();
        Bingo.ItemsAtBeginning = new ArrayList<>();
        Bingo.BingoPlaying = true;


        // Reinitialiser la progression des joueurs
        Bingo.PlayersCompleted.clear();
        Bingo.PlayerObjectivesCompleted.clear();

        // Active le respawn automatique instantane sur tous les mondes existants.
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.IMMEDIATE_RESPAWN, true));

        Bukkit.getLogger().info("plugin initialise avec " + numberOfObjectives + " objectif(s)");
        GenerateObjectives(numberOfObjectives);
        InitScoreboardForPlayer();

        // Broadcast des objectifs
        getServer().broadcastMessage("§6========== OBJECTIFS DU DORINO'BINGO ==========");
        for (int i = 0; i < Bingo.BingoObjectives.size(); i++) {
            getServer().broadcastMessage("§e" + (i + 1) + ". §f" + GetObjectiveString(Bingo.BingoObjectives.get(i)));
        }
        PlayerResetUtils.resetForGameStart(Bingo.BingoWorld);
        getServer().broadcastMessage("§6=========================================");
    }

    private void prepareFreshSessionWorlds(Bingo plugin) {
        plugin.cleanupManagedWorlds();

        String baseName = Bingo.GAME_WORLD_PREFIX + System.currentTimeMillis();
        World overworld = createWorld(baseName, org.bukkit.World.Environment.NORMAL);
        World nether = createWorld(baseName + "_nether", org.bukkit.World.Environment.NETHER);
        World end = createWorld(baseName + "_the_end", org.bukkit.World.Environment.THE_END);

        Bingo.setActiveGameWorlds(baseName, overworld, nether, end);
        Bukkit.getLogger().info("Session Bingo creee: " + baseName);
    }

    private org.bukkit.World createWorld(String worldName, org.bukkit.World.Environment environment) {
        org.bukkit.World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.createWorld(new WorldCreator(worldName).environment(environment));
        }

        if (world != null) {
            world.setGameRule(GameRule.IMMEDIATE_RESPAWN, true);
        }

        return world;
    }

    private void InitScoreboardForPlayer(){
        getServer().getOnlinePlayers().forEach(p->{
            Bingo.ScoreBoard.registerNewTeam(p.getDisplayName());
            var obj =  Bingo.ScoreBoard.registerNewObjective(String.valueOf(new Random().nextInt(5340)), "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName("§6§lDorino'Bingo");

            // Afficher tous les objectifs dans le scoreboard
            int score = 0;
            for (BingoObjective objective : Bingo.BingoObjectives) {
                String objectiveText = GetObjectiveString(objective);
                // Limiter la longueur pour éviter les problèmes d'affichage
                if (objectiveText.length() > 40) {
                    objectiveText = objectiveText.substring(0, 37) + "...";
                }
                obj.getScore("§7" + objectiveText).setScore(score++);
            }

            p.setScoreboard(Bingo.ScoreBoard);
        });
    }

    @Override
    public List<Material> getRandomObtainableItem() {
        Bingo plugin = (Bingo) getPluginManager().getPlugin("Bingo");

        File configfile = new File(plugin.getDataFolder(), "config.yaml");

        if (plugin == null) {
            Bukkit.getLogger().severe("Plugin Bingo non trouvé!");
            ArrayList<Material> li = new ArrayList<>();
            li.add(Material.STONE);
            return li;
        }

        if (!configfile.exists()) {
            Bukkit.getLogger().severe("Fichier config.yaml introuvable à: " + configfile.getAbsolutePath());
            ArrayList<Material> li = new ArrayList<>();
            li.add(Material.STONE);
            return li;
        }
        var config = YamlConfiguration.loadConfiguration(configfile);
        List<String> itemList = config.getStringList("Settings.RandomItems.Blacklisted.List");
        Bukkit.getLogger().info("Nombre d'items dans la blacklist: " + itemList.size());

        List<Material> availableMaterials = Arrays.stream(Material.values())
                .filter(Material -> {return Material.isItem()|| Material.isBlock();})
                .filter(material -> !itemList.contains(material.name()) || getDisplayNameForMaterial(material).toLowerCase().contains("spawn"))
                .collect(Collectors.toList());



        if (availableMaterials.isEmpty()) {
            availableMaterials = Collections.singletonList(Material.STONE);
        }
        var mat = availableMaterials.get(new Random().nextInt(availableMaterials.size()));
        if (config.getStringList("Settings.RandomItems.Blacklisted.List").contains(mat.name())){
            var book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(Enchantment.SILK_TOUCH,1,true);
            book.setItemMeta(meta);
            Bingo.ItemsAtBeginning.add(book);
        }
        return CheckMaterial(availableMaterials.get(new Random().nextInt(availableMaterials.size())));
    }

    private List<Material> CheckMaterial(Material mat){
        Bingo plugin = (Bingo) getPluginManager().getPlugin("Bingo");

        File configfile = new File(plugin.getDataFolder(), "config.yaml");

        var config = YamlConfiguration.loadConfiguration(configfile);
        List<String> itemList = config.getStringList("Settings.RandomItems.Blacklisted.Silk_Touch");
        List<Material> silktouchmat = Arrays.stream(Material.values())
                .filter(Material -> {return Material.isItem()|| Material.isBlock();})
                .filter(material -> itemList.contains(material.name()))
                .collect(Collectors.toList());
        if (silktouchmat.contains(mat) && getServer().getRecipesFor(new ItemStack(mat)).isEmpty()){
            return getRandomObtainableItem(); // on rechoisis magl
        }
        if(getDisplayNameForMaterial(mat).toLowerCase().contains("egg")){
            ArrayList<Material> l = new ArrayList<>();
            l.add(Material.EGG);
            l.add(Material.BLUE_EGG);
            l.add(Material.BROWN_EGG);
            return l;
        }
        if (getDisplayNameForMaterial(mat).toLowerCase().contains("banner_pattern")){
            return getRandomObtainableItem(); // on rechoisis magl
        }
        if(getDisplayNameForMaterial(mat).toLowerCase().contains("banner")){
            getLogger().info(mat.toString());
            var color = mat.toString().split("_")[0];
            ArrayList<Material> l = new ArrayList<Material>();
            l.add(Material.getMaterial(color+"_BANNER"));
            l.add(Material.getMaterial(color+"_WALL_BANNER"));
            return l;
        }
        if(getDisplayNameForMaterial(mat).toLowerCase().contains("sign")){
            getLogger().info(mat.toString());
            var color = mat.toString().replace("_SIGN","").replace("_WALL_SIGN","");
            ArrayList<Material> l = new ArrayList<Material>();
            l.add(Material.getMaterial(color+"_SIGN"));
            l.add(Material.getMaterial(color+"_WALL_SIGN"));
            return l;
        }
        if(getDisplayNameForMaterial(mat).toLowerCase().contains("disc")){
            ArrayList<Material> l = new ArrayList<Material>();
           Arrays.stream(Material.values()).toList().forEach(x->{
                if (x.name().toLowerCase().contains("music_disc")){
                    l.add(x);
                }
            });
            return l;
        }
        return Collections.singletonList(mat);
    }

    @Override
    public EntityDamageEvent.DamageCause getRandomKillMethod() {
        Bingo plugin = (Bingo) getPluginManager().getPlugin("Bingo");
        if (plugin == null) {
            Bukkit.getLogger().severe("Plugin Bingo non trouvé!");
            return EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        }
        File configfile = new File(plugin.getDataFolder(), "config.yaml");
        if (!configfile.exists()) {
            Bukkit.getLogger().severe("Fichier config.yaml introuvable à: " + configfile.getAbsolutePath());
            return EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        }
        var config = YamlConfiguration.loadConfiguration(configfile);
        List<String> killMethodsList = config.getStringList("Settings.RandomKillMethods.Whitelisted.List");
        Bukkit.getLogger().info("Nombre de méthodes de kill: " + killMethodsList.size());
        List<EntityDamageEvent.DamageCause> availableKillMethods = Arrays.stream(EntityDamageEvent.DamageCause.values())
                .filter(damageCause -> killMethodsList.contains(damageCause.name()))
                .collect(Collectors.toList());



        if (availableKillMethods.isEmpty()) {
            return EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        }
        var index = new Random().nextInt(availableKillMethods.size());
        getLogger().info(String.valueOf(index));
        return availableKillMethods.get(index);
    }

    @Override
    public Biome getRandomBiome() {
        Bingo plugin = (Bingo) getPluginManager().getPlugin("Bingo");
        if (plugin == null) {
            Bukkit.getLogger().severe("Plugin Bingo non trouvé!");
            return Biome.PLAINS;
        }
        File configfile = new File(plugin.getDataFolder(), "config.yaml");
        if (!configfile.exists()) {
            Bukkit.getLogger().severe("Fichier config.yaml introuvable à: " + configfile.getAbsolutePath());
            return Biome.PLAINS;
        }
        var config = YamlConfiguration.loadConfiguration(configfile);
        List<String> biomeList = config.getStringList("Settings.RandomBiomes.BlackListed.List");
        Bukkit.getLogger().info("Nombre de biomes: " + biomeList.size());
        List<Biome> availableBiomes = Arrays.stream(Biome.values())
                .filter(biome -> !biomeList.contains(biome.name()))
                .collect(Collectors.toList());

        //List<Biome> availableBiomes = Arrays.stream(Biome.values()).toList();
        if (availableBiomes.isEmpty()) {
            return Biome.PLAINS;
        }
        return availableBiomes.get(new Random().nextInt(availableBiomes.size()));
    }

    public List<String> getRandomAdvancement() {
        Bingo plugin = (Bingo) getPluginManager().getPlugin("Bingo");
        if (plugin == null) {
            Bukkit.getLogger().severe("Plugin Bingo non trouvé!");
            return Arrays.asList("minecraft:story/root", "Root");
        }

        File configfile = new File(plugin.getDataFolder(), "config.yaml");
        if (!configfile.exists()) {
            Bukkit.getLogger().severe("Fichier config.yaml introuvable à: " + configfile.getAbsolutePath());
            return Arrays.asList("minecraft:story/root", "Root");
        }

        var config = YamlConfiguration.loadConfiguration(configfile);
        List<Map<?, ?>> advancementList = config.getMapList("Settings.RandomAdvancements.Whitelisted.List");

        Map<String, String> advancementMap = new LinkedHashMap<>();
        for (Map<?, ?> rawEntry : advancementList) {
            for (Map.Entry<?, ?> entry : rawEntry.entrySet()) {
                String advancementKey = String.valueOf(entry.getKey()).trim();
                String advancementDescription = String.valueOf(entry.getValue()).trim();

                if (advancementKey.isEmpty() || advancementDescription.isEmpty()) {
                    continue;
                }

                if (!advancementKey.contains(":")) {
                    advancementKey = "minecraft:" + advancementKey;
                }

                advancementMap.put(advancementKey, advancementDescription);
            }
        }

        Bukkit.getLogger().info("Nombre d'avancements valides: " + advancementMap.size());

        if (advancementMap.isEmpty()) {
            return Arrays.asList("minecraft:story/root", "Root");
        }

        var entries = new ArrayList<>(advancementMap.entrySet());
        var choix = entries.get(new Random().nextInt(entries.size()));
        return Arrays.asList(choix.getKey(), choix.getValue());
    }

    /**
     * Configuration des probabilités - MODIFIER ICI pour ajuster les chances de chaque objectif
     * La somme DOIT égaler 100
     */
    private ObjectiveGenerator.ObjectiveProbabilities getObjectiveProbabilities() {
        var probs = new ObjectiveGenerator.ObjectiveProbabilities();
        return probs;
    }

    private void GenerateObjectives(int numberOfObjectives) {
        Bingo.BingoObjectives.clear();

        // Créer le générateur avec les probabilités configurées
        ObjectiveGenerator generator = new ObjectiveGenerator(
            getObjectiveProbabilities(),
            this  // Start implémente ObjectiveRandomizer
        );

        Bukkit.getLogger().info("Probabilités des objectifs : " + generator.probabilities);

        // Générer les objectifs
        List<BingoObjective> objectives = generator.generateObjectives(numberOfObjectives);

        for (int i = 0; i < objectives.size(); i++) {
            BingoObjective objective = objectives.get(i);
            Bingo.BingoObjectives.add(objective);
            // Afficher l'objectif généré
            String description = GetObjectiveString(objective);
            Bukkit.getLogger().info("Objectif " + (i + 1) + " : " + description);
        }
    }

    private String GetObjectiveString(BingoObjective objective) {
        switch (objective.getType()) {
            case OBTAIN_ITEM:
                return "Obtenir : " + getDisplayNameForMaterial(objective.getMaterial());
            case DEATH:
                return GetDeathDescription(objective.getDamageCause());
            case BIOME:
                return "Mourir dans le biome : " + objective.getBiome().name().toLowerCase().replace("_", " ");
            case ENCHANT:
                return "Enchanter un objet";

            case ADVANCEMENT:
                return "Obtenir l'avancement : " + objective.getAdvancement().get(1);
            case RENAME:
                return "Renommer un objet";
            default:
                return "Objectif inconnu";
        }
    }

    private String GetDeathDescription(EntityDamageEvent.DamageCause cause) {
        String descr;
        switch (cause) {
            case FALL:
                descr = "Mourir en tombant";
                break;
            case LAVA:
                descr = "Mourir dans la lave";
                break;
            case DROWNING:
                descr = "Mourir noyé";
                break;
            case ENTITY_ATTACK:
                descr = "Mourir tué par une entité";
                break;
            case PROJECTILE:
                descr = "Mourir par projectile";
                break;
            case SUFFOCATION:
                descr = "Mourir étouffé";
                break;
            case FIRE:
            case FIRE_TICK:
                descr = "Mourir brûlé";
                break;
            default:
                descr = "Mourir de : " + cause.name();
                break;
        }
        return descr;
    }
}
