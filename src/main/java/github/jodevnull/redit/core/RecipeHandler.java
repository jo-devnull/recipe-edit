package github.jodevnull.redit.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import github.jodevnull.redit.REdit;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import oshi.util.tuples.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static github.jodevnull.redit.REdit.GSON;

public class RecipeHandler
{
    private static final List<Pair<Path, JsonElement>> ADDED_RECIPES = new ArrayList<>();

    public static void deleteUndeditedRecipes() throws IOException {
        for (var entry : ADDED_RECIPES) {
            final var path = entry.getA();
            final var then = entry.getB();

            if (!Files.exists(path))
                continue;

            final var after = JsonParser.parseReader(Files.newBufferedReader(path));

            // file was not edited
            if (after.equals(then)) {
                Files.deleteIfExists(path);
            }
        }
    }

    public static ResourceLocation getRecipeLocation(ResourceLocation id) {
        final var segments = new ArrayList<>(
            Arrays.stream(id.getPath().split("/")).filter(s -> !s.trim().isEmpty()).toList()
        );

        var namespace = id.getNamespace();

        if (id.getPath().startsWith("/")) {
            namespace = segments.removeFirst();
        }

        final var path = String.join("/", segments) + ".json";
        return ResourceLocation.fromNamespaceAndPath(namespace, "recipe/" + path);
    }

    public static Optional<File> getRecipeFile(ResourceLocation id) throws IOException {
        final var server = ServerLifecycleHooks.getCurrentServer();

        if (server == null)
            return Optional.empty();

        final var location = getRecipeLocation(id);
        final var datapath = REdit.getDataPath();
        final var recipe = datapath.resolve(location.getNamespace()).resolve(location.getPath());

        if (Files.exists(recipe)) {
            return Optional.of(recipe.toFile());
        }

        final var resource = server.getResourceManager().getResource(location);

        if (resource.isPresent()) {
            final var reader = new JsonReader(resource.get().openAsReader());
            reader.setLenient(true);

            final var json = JsonParser.parseReader(reader);

            Files.createDirectories(recipe.getParent());
            Files.writeString(recipe, GSON.toJson(json));

            // limit the size of the array to avoid memory leaks
            if (ADDED_RECIPES.size() > 64)
                ADDED_RECIPES.removeFirst();

            ADDED_RECIPES.add(new Pair<>(recipe, json));
            return Optional.of(recipe.toFile());
        }

        return Optional.empty();
    }

    public static boolean openRecipe(ResourceLocation id) {
        final var server = ServerLifecycleHooks.getCurrentServer();
        final var minecraft = Minecraft.getInstance();
        final var player = minecraft.player;

        if (server == null)
            return false;

        try {
            getRecipeFile(id).ifPresent(Util.getPlatform()::openFile);
            return true;
        } catch (Exception e) {
            if (player != null)
                player.displayClientMessage(
                    Component.literal("Failed to open recipe!").withStyle(style -> style.withColor(ChatFormatting.RED)), false
                );

            REdit.LOGGER.error("Failed to open recipe: {}", id);
        }

        return false;
    }
}
