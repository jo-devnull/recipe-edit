package github.jodevnull.redit;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import github.jodevnull.redit.core.REditCommand;
import github.jodevnull.redit.core.REditPackRepository;
import github.jodevnull.redit.core.RecipeHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(REdit.MODID)
public class REdit
{
    public static final String MODID = "redit";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final REditPackRepository REPOSITORY = new REditPackRepository();

    public REdit(FMLJavaModLoadingContext context)
    {
        final IEventBus modBusEvent = context.getModEventBus();

        modBusEvent.addListener(this::commonSetup);
        modBusEvent.addListener(this::addPackRepository);
        context.registerConfig(ModConfig.Type.COMMON, Config.mSpec);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopedEvent);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        final var packDir = getOutputPath();
        final var mcmeta = packDir.resolve("pack.mcmeta");

        if (Files.notExists(mcmeta)) try {
            Files.writeString(mcmeta, GSON.toJson(getMcMeta()));
        } catch (IOException e) {
            LOGGER.error("error writing to {}/{}", Config.getOutputPath(), "pack.mcmeta");
        }
    }

    private void serverStopedEvent(ServerStoppedEvent event) {
        if (Config.shouldDeleteUnedited()) try {
            RecipeHandler.deleteUndeditedRecipes();
        } catch (IOException e) {
            LOGGER.error("Error trying to deleted unedited recipes: ", e);
        }
    }

    private void registerCommand(RegisterCommandsEvent event) {
        REditCommand.register(event.getDispatcher());
    }

    private void addPackRepository(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(REPOSITORY);
        }
    }

    public static Path getOutputPath() {
        return FMLPaths.getOrCreateGameRelativePath(Config.getOutputPath());
    }

    public static Path getDataPath() {
        return getOutputPath().resolve("data");
    }

    private static JsonObject getMcMeta() {
        final var meta = new JsonObject();
        final var pack = new JsonObject();

        pack.add("pack_format", new JsonPrimitive(15));
        pack.add("description", new JsonPrimitive("REdit Edited Recipes"));
        meta.add("pack", pack);

        return meta;
    }

    public static void clientError(@Nullable Player player, String message, Object... args) {
        if (player == null)
            return;

        final var formatted = Component.literal(message.formatted(args));

        player.displayClientMessage(
            formatted.withStyle(style -> style.withColor(ChatFormatting.RED)), false
        );
    }
}
