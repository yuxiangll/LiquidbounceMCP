package net.ccbluex.liquidbounce.ui.client.altmanager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.client.altmanager.sub.*;
import net.ccbluex.liquidbounce.ui.client.altmanager.sub.altgenerator.GuiMCLeaks;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.login.LoginUtils;
import net.ccbluex.liquidbounce.utils.login.MinecraftAccount;
import net.ccbluex.liquidbounce.utils.login.UserUtils;
import net.ccbluex.liquidbounce.utils.misc.HttpUtils;
import net.ccbluex.liquidbounce.utils.misc.MiscUtils;
import net.mcleaks.MCLeaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.List;
import java.util.*;

//TODO: Restore functionality from TheAltening (who tf uses that)
public class GuiAltManager extends GuiScreen {
    private static final Map<String, Boolean> GENERATORS = new HashMap<>();
    private final GuiScreen prevGui;
    public String status = "§7Idle...";
    private GuiButton loginButton;
    private GuiButton randomButton;
    private GuiList altsList;
    private GuiTextField searchField;

    public GuiAltManager(final GuiScreen prevGui) {
        this.prevGui = prevGui;
    }

    public static void loadGenerators() {
        try {
            // Read versions json from cloud
            final JsonElement jsonElement = new JsonParser().parse(HttpUtils.get(LiquidBounce.CLIENT_CLOUD + "/generators.json"));

            // Check json is valid object
            if (jsonElement.isJsonObject()) {
                // Get json object of element
                final JsonObject jsonObject = jsonElement.getAsJsonObject();

                jsonObject.entrySet().forEach(stringJsonElementEntry -> GENERATORS.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsBoolean()));
            }
        } catch (final Throwable throwable) {
            // Print throwable to console
            ClientUtils.getLogger().error("Failed to load enabled generators.", throwable);
        }
    }

    public static String login(final MinecraftAccount minecraftAccount) {
        if (minecraftAccount == null)
            return "";

        ClientUtils.getLogger().error("Something went wrong while trying to switch alt service.");

        if (minecraftAccount.isCracked()) {
            LoginUtils.loginCracked(minecraftAccount.getName());
            MCLeaks.remove();
            return "§cYour name is now §8" + minecraftAccount.getName() + "§c.";
        }

        LoginUtils.LoginResult result = LoginUtils.login(minecraftAccount.getName(), minecraftAccount.getPassword());
        if (result == LoginUtils.LoginResult.LOGGED) {
            MCLeaks.remove();
            String userName = Minecraft.getMinecraft().getSession().getUsername();
            minecraftAccount.setAccountName(userName);
            LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.accountsConfig);
            return "§cYour name is now §f§l" + userName + "§c.";
        }

        if (result == LoginUtils.LoginResult.WRONG_PASSWORD)
            return "§cWrong password.";

        if (result == LoginUtils.LoginResult.NO_CONTACT)
            return "§cCannot contact authentication server.";

        if (result == LoginUtils.LoginResult.INVALID_ACCOUNT_DATA)
            return "§cInvalid username or password.";

        if (result == LoginUtils.LoginResult.MIGRATED)
            return "§cAccount migrated.";

        return "";
    }

    public void initGui() {
        int textFieldWidth = Math.max(width / 8, 70);

        searchField = new GuiTextField(2, (FontRenderer) LiquidBounce.fontManager.PingFang20, width - textFieldWidth - 10, 10, textFieldWidth, 20);
        searchField.setMaxStringLength(Integer.MAX_VALUE);

        altsList = new GuiList(this);
        altsList.registerScrollButtons(7, 8);

        int index = -1;

        for (int i = 0; i < LiquidBounce.fileManager.accountsConfig.getAccounts().size(); i++) {
            MinecraftAccount minecraftAccount = LiquidBounce.fileManager.accountsConfig.getAccounts().get(i);

            if (minecraftAccount != null && (((minecraftAccount.getPassword() == null || minecraftAccount.getPassword().isEmpty()) && minecraftAccount.getName() != null && minecraftAccount.getName().equals(mc.session.getUsername())) || minecraftAccount.getAccountName() != null && minecraftAccount.getAccountName().equals(mc.session.getUsername()))) {
                index = i;
                break;
            }
        }

        altsList.elementClicked(index, false, 0, 0);
        altsList.scrollBy(index * altsList.slotHeight);

        int j = 22;
        this.buttonList.add(new GuiButton(1, width - 80, j + 24, 70, 20, "Add"));
        this.buttonList.add(new GuiButton(2, width - 80, j + 24 * 2, 70, 20, "Remove"));
        this.buttonList.add(new GuiButton(7, width - 80, j + 24 * 3, 70, 20, "Import"));
        this.buttonList.add(new GuiButton(12, width - 80, j + 24 * 4, 70, 20, "Export"));
        this.buttonList.add(new GuiButton(8, width - 80, j + 24 * 5, 70, 20, "Copy"));

        this.buttonList.add(new GuiButton(0, width - 80, height - 65, 70, 20, "Back"));

        this.buttonList.add(loginButton = new GuiButton(3, 5, j + 24, 90, 20, "Login"));
        this.buttonList.add(randomButton = new GuiButton(4, 5, j + 24 * 2, 90, 20, "Random"));
        this.buttonList.add(new GuiButton(6, 5, j + 24 * 3, 90, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(88, 5, j + 24 * 4, 90, 20, "Change Name"));

        if (GENERATORS.getOrDefault("mcleaks", true))
            this.buttonList.add(new GuiButton(5, 5, j + 24 * 5 + 5, 90, 20, "MCLeaks"));
        if (GENERATORS.getOrDefault("thealtening", true))
            this.buttonList.add(new GuiButton(9, 5, j + 24 * 6 + 5, 90, 20, "TheAltening"));

        this.buttonList.add(new GuiButton(10, 5, j + 24 * 7 + 5, 90, 20, "Session Login"));
        this.buttonList.add(new GuiButton(11, 5, j + 24 * 8 + 10, 90, 20, "Cape"));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);

        altsList.drawScreen(mouseX, mouseY, partialTicks);

        LiquidBounce.fontManager.PingFang20.drawCenteredString("AltManager", width / 2.0f, 6, 0xffffff);
        LiquidBounce.fontManager.PingFang18.drawCenteredString(this.searchField.getText().isEmpty() ? (LiquidBounce.fileManager.accountsConfig.getAccounts().size() + " Alts") : this.altsList.accounts.size() + " Search Results", width / 2.0f, 18, 0xffffff);
        LiquidBounce.fontManager.PingFang18.drawCenteredString(status, width / 2.0f, 32, 0xffffff);
        LiquidBounce.fontManager.PingFang18.drawStringWithShadow("§7User: §a" + (MCLeaks.isAltActive() ? MCLeaks.getSession().getUsername() : mc.getSession().getUsername()), 6, 6, 0xffffff);
        LiquidBounce.fontManager.PingFang18.drawStringWithShadow("§7Type: §a" + (UserUtils.INSTANCE.isValidTokenOffline(mc.getSession().getToken()) ? "Premium" : "Cracked"), 6, 15, 0xffffff);

        this.searchField.drawTextBox();

        if (searchField.getText().isEmpty() && !searchField.isFocused())
            LiquidBounce.fontManager.PingFang20.drawString("§7Search...", this.searchField.xPosition + 4, 17, 0xffffff);


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) return;

        switch (button.id) {
            case 0:
                mc.displayGuiScreen(prevGui);
                break;
            case 1:
                mc.displayGuiScreen(new GuiAdd(this));
                break;
            case 2:
                if (altsList.getSelectedSlot() != -1 && altsList.getSelectedSlot() < altsList.getSize()) {
                    LiquidBounce.fileManager.accountsConfig.removeAccount(this.altsList.accounts.get(altsList.getSelectedSlot()));
                    LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.accountsConfig);
                    status = "§aThe account has been removed.";

                    this.altsList.updateAccounts(searchField.getText());
                } else
                    status = "§cSelect an account.";
                break;
            case 3:
                if (altsList.getSelectedSlot() != -1 && altsList.getSelectedSlot() < altsList.getSize()) {
                    loginButton.enabled = randomButton.enabled = false;

                    final Thread thread = new Thread(() -> {
                        final MinecraftAccount minecraftAccount = this.altsList.accounts.get(altsList.getSelectedSlot());
                        status = "§aLogging in...";
                        status = login(minecraftAccount);

                        loginButton.enabled = randomButton.enabled = true;
                    }, "AltLogin");
                    thread.start();
                } else
                    status = "§cSelect an account.";
                break;
            case 4:
                if (this.altsList.accounts.size() <= 0) {
                    status = "§cThe list is empty.";
                    return;
                }

                final int randomInteger = new Random().nextInt(this.altsList.accounts.size());

                if (randomInteger < altsList.getSize())
                    altsList.selectedSlot = randomInteger;

                loginButton.enabled = randomButton.enabled = false;

                final Thread thread = new Thread(() -> {
                    final MinecraftAccount minecraftAccount = this.altsList.accounts.get(randomInteger);
                    status = "§aLogging in...";
                    status = login(minecraftAccount);

                    loginButton.enabled = randomButton.enabled = true;
                }, "AltLogin");
                thread.start();
                break;
            case 5:
                mc.displayGuiScreen(new GuiMCLeaks(this));
                break;
            case 6:
                mc.displayGuiScreen(new GuiDirectLogin(this));
                break;
            case 7:
                final File file = MiscUtils.openFileChooser();

                if (file == null)
                    return;

                final FileReader fileReader = new FileReader(file);
                final BufferedReader bufferedReader = new BufferedReader(fileReader);

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    final String[] accountData = line.split(":", 2);

                    if (!LiquidBounce.fileManager.accountsConfig.accountExists(accountData[0])) {
                        if (accountData.length > 1)
                            LiquidBounce.fileManager.accountsConfig.addAccount(accountData[0], accountData[1]);
                        else
                            LiquidBounce.fileManager.accountsConfig.addAccount(accountData[0]);
                    }
                }

                fileReader.close();
                bufferedReader.close();

                this.altsList.updateAccounts(searchField.getText());
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.accountsConfig);
                status = "§aThe accounts were imported successfully.";
                break;
            case 8:
                if (altsList.getSelectedSlot() != -1 && altsList.getSelectedSlot() < altsList.getSize()) {
                    final MinecraftAccount minecraftAccount = this.altsList.accounts.get(altsList.getSelectedSlot());

                    if (minecraftAccount == null)
                        break;

                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(minecraftAccount.getName() + ":" + minecraftAccount.getPassword()), null);
                    status = "§aCopied account into your clipboard.";
                } else
                    status = "§cSelect an account.";
                break;
            case 88:
                mc.displayGuiScreen(new GuiChangeName(this));
                break;
            case 9:
                status = "Sorry TheAltening is unsupported :(";
                break;
            case 10:
                mc.displayGuiScreen(new GuiSessionLogin(this));
                break;
            case 11:
                mc.displayGuiScreen(new GuiDonatorCape(this));
                break;
            case 12:
                if (LiquidBounce.fileManager.accountsConfig.getAccounts().size() == 0) {
                    status = "§cThe list is empty.";
                    return;
                }

                final File selectedFile = MiscUtils.saveFileChooser();

                if (selectedFile == null || selectedFile.isDirectory())
                    return;

                try {
                    if (!selectedFile.exists())
                        selectedFile.createNewFile();

                    final FileWriter fileWriter = new FileWriter(selectedFile);

                    for (MinecraftAccount account : LiquidBounce.fileManager.accountsConfig.getAccounts()) {
                        if (account.isCracked()) {
                            fileWriter.write(account.getName() + "\r\n");
                        } else {
                            fileWriter.write(account.getName() + ":" + account.getPassword() + "\r\n");
                        }
                    }

                    fileWriter.flush();
                    fileWriter.close();
                    JOptionPane.showMessageDialog(null, "Exported successfully!", "AltManager", JOptionPane.INFORMATION_MESSAGE);
                } catch (final Exception e) {
                    e.printStackTrace();
                    MiscUtils.showErrorPopup("Error", "Exception class: " + e.getClass().getName() + "\nMessage: " + e.getMessage());
                }
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchField.isFocused()) {
            searchField.textboxKeyTyped(typedChar, keyCode);
            this.altsList.updateAccounts(searchField.getText());
        }

        switch (keyCode) {
            case Keyboard.KEY_ESCAPE:
                mc.displayGuiScreen(prevGui);
                return;
            case Keyboard.KEY_UP: {
                int i = altsList.getSelectedSlot() - 1;
                if (i < 0)
                    i = 0;
                altsList.elementClicked(i, false, 0, 0);
                break;
            }
            case Keyboard.KEY_DOWN: {
                int i = altsList.getSelectedSlot() + 1;
                if (i >= altsList.getSize())
                    i = altsList.getSize() - 1;
                altsList.elementClicked(i, false, 0, 0);
                break;
            }
            case Keyboard.KEY_RETURN: {
                altsList.elementClicked(altsList.getSelectedSlot(), true, 0, 0);
                break;
            }
            case Keyboard.KEY_NEXT: {
                altsList.scrollBy(height - 100);
                break;
            }
            case Keyboard.KEY_PRIOR: {
                altsList.scrollBy(-height + 100);
                return;
            }
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        altsList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        searchField.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        searchField.updateCursorCounter();
    }

    private class GuiList extends GuiSlot {
        private List<MinecraftAccount> accounts;
        private int selectedSlot;

        GuiList(GuiScreen prevGui) {
            super(GuiAltManager.this.mc, prevGui.width, prevGui.height, 40, prevGui.height - 40, 30);

            updateAccounts(null);
        }

        private void updateAccounts(String search) {
            if (search == null || search.isEmpty()) {
                this.accounts = LiquidBounce.fileManager.accountsConfig.getAccounts();
                return;
            }

            search = search.toLowerCase();

            this.accounts = new ArrayList<>();

            for (MinecraftAccount account : LiquidBounce.fileManager.accountsConfig.getAccounts()) {
                if (account.getName() != null && account.getName().toLowerCase().contains(search)
                        || account.getAccountName() != null && account.getAccountName().toLowerCase().contains(search)) {
                    this.accounts.add(account);
                }

            }
        }

        @Override
        protected boolean isSelected(int id) {
            return selectedSlot == id;
        }

        int getSelectedSlot() {
            if (selectedSlot > accounts.size())
                selectedSlot = -1;
            return selectedSlot;
        }

        public void setSelectedSlot(int selectedSlot) {
            this.selectedSlot = selectedSlot;
        }

        @Override
        protected int getSize() {
            return accounts.size();
        }

        @Override
        protected void elementClicked(int var1, boolean doubleClick, int var3, int var4) {
            selectedSlot = var1;

            if (doubleClick) {
                if (altsList.getSelectedSlot() != -1 && altsList.getSelectedSlot() < altsList.getSize() && loginButton.enabled) {
                    loginButton.enabled = randomButton.enabled = false;

                    new Thread(() -> {
                        MinecraftAccount minecraftAccount = accounts.get(altsList.getSelectedSlot());
                        status = "§aLogging in...";
                        status = "§c" + login(minecraftAccount);

                        loginButton.enabled = randomButton.enabled = true;
                    }, "AltManagerLogin").start();
                } else
                    status = "§cSelect an account.";
            }
        }

        @Override
        protected void drawSlot(int id, int x, int y, int var4, int var5, int var6) {
            final MinecraftAccount minecraftAccount = accounts.get(id);
            LiquidBounce.fontManager.PingFang20.drawStringWithShadow(minecraftAccount.getAccountName() == null ? minecraftAccount.getName() : minecraftAccount.getAccountName(), (width / 2), y + 2, Color.WHITE.getRGB());
            LiquidBounce.fontManager.PingFang20.drawStringWithShadow(minecraftAccount.isCracked() ? "Cracked" : (minecraftAccount.getAccountName() == null ? "Premium" : minecraftAccount.getName()), (width / 2), y + 15, minecraftAccount.isCracked() ? Color.GRAY.getRGB() : (minecraftAccount.getAccountName() == null ? Color.GREEN.getRGB() : Color.LIGHT_GRAY.getRGB()));
        }

        @Override
        protected void drawBackground() {
        }
    }

}
