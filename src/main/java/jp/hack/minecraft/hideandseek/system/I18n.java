/*
 * Copyright 2021 ZOI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * */

package jp.hack.minecraft.hideandseek.system;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class I18n {
    protected static final Logger LOGGER = Logger.getLogger("HideAndSeek");
    private static I18n instance;
    private static final String MESSAGES = "messages";
    private final transient Locale defaultLocale = Locale.JAPAN;//.getDefault();
    private transient Locale currentLocale;
    private transient ResourceBundle customBundle;
    private transient ResourceBundle localeBundle;
    private final transient ResourceBundle defaultBundle;
    private transient Map<String, MessageFormat> messageFormatCache;
    private final transient JavaPlugin plugin;
    private static final Pattern NODOUBLEMARK = Pattern.compile("''");
    private static final ResourceBundle NULL_BUNDLE = new ResourceBundle() {
        public Enumeration<String> getKeys() {
            return null;
        }

        protected Object handleGetObject(String key) {
            return null;
        }
    };

    public I18n(JavaPlugin plugin) {
        this.currentLocale = this.defaultLocale;
        this.messageFormatCache = new HashMap();
        this.plugin = plugin;
        this.defaultBundle = ResourceBundle.getBundle(MESSAGES, Locale.JAPAN);
        this.localeBundle = this.defaultBundle;
        this.customBundle = NULL_BUNDLE;
    }

    public static void onEnable(I18n i18n) {
        instance = i18n;
    }

    public static void onDisable() {
        instance = null;
    }

    public Locale getCurrentLocale() {
        return this.currentLocale;
    }

    private String translate(String string) {
        try {
            try {
                return this.customBundle.getString(string);
            } catch (MissingResourceException var3) {
                return this.localeBundle.getString(string);
            }
        } catch (MissingResourceException var4) {
            LOGGER.log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file %s", var4.getKey(), this.localeBundle.getLocale().toString()), var4);
            return this.defaultBundle.getString(string);
        }
    }

    public static String tl(String string, Object... objects) {
        if (instance == null) {
            return "";
        } else {
            return objects.length == 0 ? NODOUBLEMARK.matcher(instance.translate(string)).replaceAll("'") : instance.format(string, objects);
        }
    }

    public String format(String string, Object... objects) {
        String format = this.translate(string);
        MessageFormat messageFormat = (MessageFormat)this.messageFormatCache.get(format);
        if (messageFormat == null) {
            try {
                messageFormat = new MessageFormat(format);
            } catch (IllegalArgumentException var6) {
                LOGGER.log(Level.SEVERE, "Invalid Translation key for '" + string + "': " + var6.getMessage());
                format = format.replaceAll("\\{(\\D*?)\\}", "\\[$1\\]");
                messageFormat = new MessageFormat(format);
            }

            this.messageFormatCache.put(format, messageFormat);
        }

        return messageFormat.format(objects).replace(' ', ' ');
    }

    public void updateLocale(String loc) {
        if (loc != null && !loc.isEmpty()) {
            String[] parts = loc.split("[_\\.]");
            if (parts.length == 1) {
                this.currentLocale = new Locale(parts[0]);
            }

            if (parts.length == 2) {
                this.currentLocale = new Locale(parts[0], parts[1]);
            }

            if (parts.length == 3) {
                this.currentLocale = new Locale(parts[0], parts[1], parts[2]);
            }
        }

        ResourceBundle.clearCache();
        this.messageFormatCache = new HashMap();
        LOGGER.log(Level.INFO, String.format("Using locale %s", this.currentLocale.toString()));

        try {
            this.localeBundle = ResourceBundle.getBundle("messages", this.currentLocale);
        } catch (MissingResourceException var4) {
            this.localeBundle = NULL_BUNDLE;
        }

        try {
            this.customBundle = ResourceBundle.getBundle("messages", this.currentLocale, new I18n.FileResClassLoader(I18n.class.getClassLoader(), this.plugin));
        } catch (MissingResourceException var3) {
            this.customBundle = NULL_BUNDLE;
        }

    }

    public static String capitalCase(String input) {
        return input != null && input.length() != 0 ? input.toUpperCase(Locale.ENGLISH).charAt(0) + input.toLowerCase(Locale.ENGLISH).substring(1) : input;
    }

    private static class FileResClassLoader extends ClassLoader {
        private final transient File dataFolder;

        FileResClassLoader(ClassLoader classLoader, JavaPlugin plugin) {
            super(classLoader);
            this.dataFolder = plugin.getDataFolder();
        }

        public URL getResource(String string) {
            File file = new File(this.dataFolder, string);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException var4) {
                }
            }

            return null;
        }

        public InputStream getResourceAsStream(String string) {
            File file = new File(this.dataFolder, string);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException var4) {
                }
            }

            return null;
        }
    }
}