package com.github.ikkeyannick.keybindmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = KeybindMod.MODID, name = KeybindMod.NAME, version = KeybindMod.VERSION, useMetadata = true)
public class KeybindMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "keybindmod";
    public static final String NAME = "Keybind Mod";
    public static final String VERSION = "1.2.1";

    private static KeyBinding myKeybind;
    private static KeyBinding jumpKeybind;
    private static KeyBinding attackKeybind;
    private static String currentMode = "normal"; // Default mode

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Get instances of the Jump and Attack/Destroy key bindings
        myKeybind = new KeyBinding("Switch Farm/Normal Mode", Keyboard.KEY_X, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(myKeybind);
        jumpKeybind = Minecraft.getMinecraft().gameSettings.keyBindJump;       // Key binding for Jump
        attackKeybind = Minecraft.getMinecraft().gameSettings.keyBindAttack;   // Key binding for Attack/Destroy

        // Register the event handler
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (myKeybind.isPressed()) {
            // Debugging log to show current keybinds
            if ("normal".equals(currentMode)) {
                currentMode = "Garden";
                sendChatMessage("Mode switched to: §aGarden");
            } else {
                currentMode = "normal";
                sendChatMessage("Mode switched to: §9Normal");
            }

            // Validate the key codes to ensure they are valid
            if (isValidKeyCode(jumpKeybind.getKeyCode()) && isValidKeyCode(attackKeybind.getKeyCode())) {
                // Perform the keybinding swap
                int tempKey = jumpKeybind.getKeyCode();
                jumpKeybind.setKeyCode(attackKeybind.getKeyCode());
                attackKeybind.setKeyCode(tempKey);

                // Save the keybinding changes
                Minecraft.getMinecraft().gameSettings.saveOptions();
                Minecraft.getMinecraft().gameSettings.loadOptions();
            } else {
                // Error message if the key codes are invalid
                sendChatMessage("Error: Invalid key codes detected. Swap aborted.");
            }
        }
    }

    // Method to handle key actions based on the swapped key codes
    private void handleKeyActions(int originalJumpKey, int originalAttackKey) {
        // Detect the current key state and perform corresponding actions
        if (Keyboard.isKeyDown(originalJumpKey)) {
            // Trigger jump if the new jump key is pressed
            Minecraft.getMinecraft().thePlayer.jump();
        }

        if (Keyboard.isKeyDown(originalAttackKey)) {
            // Trigger attack if the new attack key is pressed
            Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().objectMouseOver.entityHit);
        }
    }

    // Helper method to validate key codes
    private boolean isValidKeyCode(int keyCode) {
        return (keyCode >= 0 && keyCode < Keyboard.KEYBOARD_SIZE) || isMouseKeyCode(keyCode);
    }

    // Helper method to check for mouse key codes
    private boolean isMouseKeyCode(int keyCode) {
        return keyCode < 0 && keyCode >= -100; // Mouse buttons range
    }

    // Helper method to get key names for display purposes
    private String getKeyName(int keyCode) {
        if (isValidKeyCode(keyCode)) {
            if (isMouseKeyCode(keyCode)) {
                switch (keyCode) {
                    case -100:
                        return "Left Mouse Button";
                    case -99:
                        return "Right Mouse Button";
                    case -98:
                        return "Middle Mouse Button";
                    default:
                        return "Mouse Button " + (-(keyCode + 100));
                }
            } else {
                return Keyboard.getKeyName(keyCode); // For keyboard keys
            }
        }
        return "Unknown";
    }

    // Helper method to send messages to chat
    private void sendChatMessage(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
    }


}
