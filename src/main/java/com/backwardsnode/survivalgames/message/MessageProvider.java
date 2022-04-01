/*
 * BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 * Copyright (C) 2019-2022 BackwardsNode/BossWasHere
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.backwardsnode.survivalgames.message;

import com.backwardsnode.survivalgames.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;

import java.nio.file.StandardCopyOption;

public class MessageProvider {

    public static final String DEFAULT_LOCALE = "en_US";

    private final Plugin PLUGIN;
    private final String CURRENT_DEFAULT;
    private final HashMap<String, Configuration> LANGUAGES;

    public MessageProvider(Plugin plugin) {
        this(plugin, DEFAULT_LOCALE, false);
    }

    public MessageProvider(Plugin plugin, String defaultLocale, boolean preloadDefault) {
        PLUGIN = plugin;
        LANGUAGES = new HashMap<>();

        CURRENT_DEFAULT = defaultLocale;

        if (preloadDefault) {
            loadLanguage(CURRENT_DEFAULT, false);
        }
    }

    public boolean loadLanguage(String language, boolean reload) {
        if (!reload && LANGUAGES.containsKey(language)) {
            return false;
        }

        Configuration languageConfig = loadLanguageInternal(language);
        LANGUAGES.put(language, languageConfig);

        return languageConfig != null;
    }

    private Configuration loadLanguageInternal(String language) {
        String langFileName = language + ".yml";
        File langFile = new File(PLUGIN.getLanguageFolder(), langFileName);

        if (!langFile.exists()) {
            try {
                PLUGIN.extractFile(langFile.getAbsoluteFile().toPath(), langFileName);
                PLUGIN.getLogger().info("Extracted language file for " + language);
            } catch (Exception e) {
                PLUGIN.getLogger().warning("Could not extract language " + language);

                return null;
            }
        }

        try {
            return YamlConfiguration.loadConfiguration(langFile);
        } catch (Exception e) {
            PLUGIN.getLogger().severe("Could not load language from " + langFileName);

            return null;
        }
    }

    public boolean reExtractLanguage(String language) {
        Configuration languageConfig = reExtractLanguageInternal(language);

        if (languageConfig != null) {
            LANGUAGES.put(language, languageConfig);
            return true;
        }

        return false;
    }

    private Configuration reExtractLanguageInternal(String language) {
        String langFileName = language + ".yml";
        File langFile = new File(PLUGIN.getLanguageFolder(), langFileName);

        try {
            PLUGIN.extractFile(langFile.getAbsoluteFile().toPath(), langFileName, StandardCopyOption.REPLACE_EXISTING);
            PLUGIN.getLogger().info("Extracted language file for " + language);
        } catch (Exception e) {
            return null;
        }

        try {
            return YamlConfiguration.loadConfiguration(langFile);
        } catch (Exception e) {
            PLUGIN.getLogger().severe("Could not load language from " + langFileName);

            return null;
        }
    }

    private Configuration selectLanguageInternal(String language) {
        Configuration languageConfig;

        if (LANGUAGES.containsKey(language)) {
            languageConfig = LANGUAGES.get(language);
        } else {
            languageConfig = loadLanguageInternal(language);
            LANGUAGES.put(language, languageConfig);
        }

        if (languageConfig == null) {
            return LANGUAGES.get(CURRENT_DEFAULT);
        }

        return languageConfig;
    }

    public void sendMessage(CommandSender sender, PluginMessage message, Object... formatVars) {
        String locale = CURRENT_DEFAULT;

        if (sender == null) {
            for (String line : compileMessage(message, CURRENT_DEFAULT, formatVars).split("\\r?\\n")) {
                PLUGIN.getLogger().info(line);
            }
            return;
        }

        if (sender instanceof Player player) {
            locale = player.getLocale();
        }

        String[] messages = compileMessage(message, locale, formatVars).split("\\r?\\n");
        sender.sendMessage(messages);
    }

    public String compileDefaultMessage(PluginMessage message, Object... formatVars) {
        return compileMessageInternal(message.getTarget(), message.getPrefix(), LANGUAGES.get(CURRENT_DEFAULT), formatVars);
    }

    public String compileMessage(PluginMessage message, String locale, Object... formatVars) {
        Configuration languageConfig = selectLanguageInternal(locale);

        return compileMessageInternal(message.getTarget(), message.getPrefix(), languageConfig, formatVars);
    }

    public String compileUnregisteredMessage(String unlocalized, String locale, Object... formatVars) {
        Configuration languageConfig = selectLanguageInternal(locale);

        return compileMessageInternal(unlocalized, false, languageConfig, formatVars);
    }

    private String compileMessageInternal(String target, boolean prefix, Configuration languageConfig, Object... formatVars) {
        if (languageConfig == null) {
            return target;
        }

        String localizedMessage = languageConfig.getString(target, target);
        if (formatVars.length > 0) {
            localizedMessage = localizedMessage.formatted(formatVars);
        }
        if (prefix) {
            String prefixString = null;
            int lastIndex = target.lastIndexOf('.');
            if (lastIndex != -1) {
                String customPrefixTarget = target.substring(0, lastIndex);
                if (customPrefixTarget.length() > 0) {
                    prefixString = languageConfig.getString(customPrefixTarget + "._prefix");
                }
            }
            if (prefixString == null) {
                prefixString = languageConfig.getString(Messages.PLUGIN.PREFIX.getTarget(), "[err]");
            }
            localizedMessage = prefixString + localizedMessage;
        }
        return ChatColor.translateAlternateColorCodes('&', localizedMessage);
    }
}
