package com.survivaldub.dubilets.commands;

import com.survivaldub.dubilets.DubiletConfig;
import com.survivaldub.dubilets.Dubilets;
import com.survivaldub.dubilets.handlers.DubiletHandler;
import com.survivaldub.dubilets.handlers.PlayerHandler;
import com.survivaldub.dubilets.menus.DubiletInfoMenu;
import com.survivaldub.dubilets.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class DubiletsCommand implements CommandExecutor, TabCompleter {

    private Dubilets plugin;

    public DubiletsCommand(Dubilets plugin) {
        this.plugin = plugin;
    }

    private void sendHelpMenu(CommandSender sender) {
        sender.sendMessage(ChatUtils.translateColor("&8&m--------------------------------------------------"));
        sender.sendMessage(ChatUtils.translateColor(" &b&lDubilets &7- &fMenú de Ayuda"));
        sender.sendMessage(ChatUtils.translateColor(""));
        if (sender.hasPermission("survivaldub.dubilets.create"))
            sender.sendMessage(ChatUtils.translateColor(" &e/dubilets create <nombre> &8- &7Crea un dubilet nuevo"));
        if (sender.hasPermission("survivaldub.dubilets.remove"))
            sender.sendMessage(ChatUtils.translateColor(" &e/dubilets remove <nombre> &8- &7Elimina un dubilet"));
        if (sender.hasPermission("survivaldub.dubilets.list"))
            sender.sendMessage(ChatUtils.translateColor(" &e/dubilets list &8- &7Lista todos los dubilets"));
        if (sender.hasPermission("survivaldub.dubilets.give"))
            sender.sendMessage(ChatUtils.translateColor(" &e/dubilets give <cant> [jugador] &8- &7Otorga dubets"));
        if (sender.hasPermission("survivaldub.dubilets.set"))
            sender.sendMessage(ChatUtils.translateColor(" &e/dubilets set <cant> [jugador] &8- &7Establece dubets"));
        if (sender.hasPermission("survivaldub.dubilets.balance"))
            sender.sendMessage(ChatUtils.translateColor(" &e/dubilets bal [jugador] &8- &7Consulta el balance"));
        if (sender.hasPermission("survivaldub.dubilets"))
            sender.sendMessage(ChatUtils.translateColor(" &e/dubilets info &8- &7Muestra la información y premios"));
        sender.sendMessage(ChatUtils.translateColor("&8&m--------------------------------------------------"));
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMenu(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "bal": {
                if (sender instanceof ConsoleCommandSender && args.length < 2) {
                    sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7Uso correcto: &e/dubilets bal <jugador>"));
                    break;
                }
                if (!sender.hasPermission("survivaldub.dubilets.balance")) break;
                if (args.length > 1) {
                    String target = args[1];
                    sender.sendMessage(ChatUtils.translateColor(this.plugin.getLanguageHandler().getString("dubilets.player_balance").replace("%player%", target).replace("%dubets%", String.valueOf(PlayerHandler.getInstance().getPlayerDubets(target)))));
                    break;
                }
                sender.sendMessage(ChatUtils.translateColor(this.plugin.getLanguageHandler().getString("dubilets.your_balance").replace("%dubets%", "" + PlayerHandler.getInstance().getPlayerDubets((Player) sender))));
                break;
            }
            case "list": {
                if (!sender.hasPermission("survivaldub.dubilets.list")) break;
                sender.sendMessage(ChatUtils.translateColor("&8&m--------------------------------------------------"));
                sender.sendMessage(ChatUtils.translateColor(" &a&lLista de Dubilets Activos:"));
                sender.sendMessage("");
                for (DubiletHandler dubiletHandler : DubiletConfig.getDubilets()) {
                    Location loc = dubiletHandler.getLocation();
                    sender.sendMessage(ChatUtils.translateColor(" &8» &e" + dubiletHandler.getName() + " &8(&7" + loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "&8)"));
                }
                sender.sendMessage(ChatUtils.translateColor("&8&m--------------------------------------------------"));
                break;
            }
            case "info": {
                if (!(sender instanceof Player) || !sender.hasPermission("survivaldub.dubilets")) break;
                Player player = (Player) sender;
                Dubilets.getMenuManager().setPlayerMenu(player, new DubiletInfoMenu());
                break;
            }
            case "create": {
                if (!sender.hasPermission("survivaldub.dubilets.create")) break;
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7Este comando solo puede ejecutarse en el juego."));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7Uso correcto: &e/dubilets create <nombre>"));
                    return true;
                }
                Block block = ((Player) sender).getTargetBlock((Set<Material>) null, 5);
                if (block.getType() != Material.END_PORTAL_FRAME) {
                    sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7Debes mirar directamente a un marco del portal del End."));
                    return true;
                }
                if (this.plugin.initDubiletHandler(args[1], block.getLocation()).addDubilet(block, args[1])) {
                    sender.sendMessage(ChatUtils.translateColor("&a&l¡Éxito! &8| &7Dubilet &e" + args[1] + " &7creado correctamente."));
                    break;
                }
                sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7¡Ya existe un dubilet con ese nombre!"));
                break;
            }
            case "remove": {
                if (!sender.hasPermission("survivaldub.dubilets.remove")) break;
                if (args.length < 2) {
                    sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7Uso correcto: &e/dubilets remove <nombre>"));
                    return true;
                }
                if (this.plugin.getDubiletHandler().removeDubilet(args[1])) {
                    sender.sendMessage(ChatUtils.translateColor("&a&l¡Éxito! &8| &7Has eliminado el Dubilet &e" + args[1] + " &7correctamente."));
                    break;
                }
                sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7No se encontró ningún dubilet con ese nombre."));
                break;
            }
            case "give": {
                if (!sender.hasPermission("survivaldub.dubilets.give")) break;
                if (args.length < 2) {
                    sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7Uso correcto: &e/dubilets give <cantidad> [jugador]"));
                    return true;
                }
                String nick = "";
                Player player = null;
                if (args.length > 2) {
                    nick = args[2];
                    player = Bukkit.getPlayer(args[2]);
                }
                int tempDubet;
                try {
                    tempDubet = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7La cantidad debe ser un número válido."));
                    return true;
                }
                final int dubet = tempDubet;
                final String finalNick = nick;
                final Player finalPlayer = player;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (finalNick.isEmpty()) {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7Desde consola debes especificar un jugador."));
                                return;
                            }
                            PlayerHandler.getInstance().addPlayerDubets((Player) sender, dubet);
                            sender.sendMessage(ChatUtils.translateColor(Dubilets.getInstance().getLanguageHandler().getString("dubilets.your_balance").replace("%dubets%", "" + PlayerHandler.getInstance().getPlayerDubets((Player) sender))));
                        } else {
                            if (finalPlayer == null) {
                                sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7El jugador no se encuentra en línea."));
                                return;
                            }
                            PlayerHandler.getInstance().addPlayerDubets(finalPlayer, dubet);
                            sender.sendMessage(ChatUtils.translateColor(Dubilets.getInstance().getLanguageHandler().getString("dubilets.player_balance").replace("%player%", finalPlayer.getName()).replace("%dubets%", String.valueOf(PlayerHandler.getInstance().getPlayerDubets(finalPlayer)))));
                        }
                    }
                }.runTaskLater((Plugin) this.plugin, 1L);
                break;
            }
            case "set": {
                if (!sender.hasPermission("survivaldub.dubilets.set")) break;
                if (args.length < 2) {
                    sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7Uso correcto: &e/dubilets set <cantidad> [jugador]"));
                    return true;
                }
                String nick = "";
                Player player = null;
                if (args.length > 2) {
                    nick = args[2];
                    player = Bukkit.getPlayer(args[2]);
                }
                int tempDubet;
                try {
                    tempDubet = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7La cantidad debe ser un número válido."));
                    return true;
                }
                final int dubet = tempDubet;
                final String finalNick = nick;
                final Player finalPlayer = player;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (finalNick.isEmpty()) {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7Desde consola debes especificar un jugador."));
                                return;
                            }
                            PlayerHandler.getInstance().setPlayerDubets((Player) sender, dubet);
                            sender.sendMessage(ChatUtils.translateColor(Dubilets.getInstance().getLanguageHandler().getString("dubilets.your_balance").replace("%dubets%", "" + PlayerHandler.getInstance().getPlayerDubets((Player) sender))));
                        } else {
                            if (finalPlayer == null) {
                                sender.sendMessage(ChatUtils.translateColor("&c&lError &8| &7El jugador no se encuentra en línea."));
                                return;
                            }
                            PlayerHandler.getInstance().setPlayerDubets(finalPlayer, dubet);
                            sender.sendMessage(ChatUtils.translateColor(Dubilets.getInstance().getLanguageHandler().getString("dubilets.player_balance").replace("%player%", finalPlayer.getName()).replace("%dubets%", String.valueOf(PlayerHandler.getInstance().getPlayerDubets(finalPlayer)))));
                        }
                    }
                }.runTaskLater((Plugin) this.plugin, 1L);
                break;
            }
            default: {
                sendHelpMenu(sender);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "list", "remove", "give", "set", "bal", "info").stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                return DubiletConfig.getDubilets().stream().map(DubiletHandler::getName).filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
            }
            if (Arrays.asList("give", "set", "bal").contains(args[0].toLowerCase())) {
                return null;
            }
        }
        if (args.length == 3 && Arrays.asList("give", "set").contains(args[0].toLowerCase())) {
            return null;
        }
        return new ArrayList<>();
    }
}
