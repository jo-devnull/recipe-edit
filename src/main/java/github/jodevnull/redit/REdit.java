package github.jodevnull.redit;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import github.jodevnull.redit.core.REditCommand;
import github.jodevnull.redit.core.REditPackRepository;
import github.jodevnull.redit.core.RecipeHandler;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;

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

    public REdit(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addPackRepository);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.mSpec);

        NeoForge.EVENT_BUS.addListener(this::registerCommand);
        NeoForge.EVENT_BUS.addListener(this::serverStopedEvent);
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

        pack.add("pack_format", new JsonPrimitive(48));
        pack.add("description", new JsonPrimitive("REdit Edited Recipes"));
        meta.add("pack", pack);

        return meta;
    }
}
