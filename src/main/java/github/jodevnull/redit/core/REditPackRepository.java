package github.jodevnull.redit.core;

import github.jodevnull.redit.REdit;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.Consumer;

public class REditPackRepository implements RepositorySource
{
    private final PackSource sourceInfo = PackSource.create(packText -> packText, true);
    private final PackType packType = PackType.SERVER_DATA;

    @Override
    public void loadPacks(@NotNull Consumer<Pack> consumer) {
        final var displayName = Component.literal("Edited Recipes");
        final var pack = Pack.readMetaAndCreate("redit", displayName, true, getSupplier(), packType, Pack.Position.TOP, sourceInfo);

        if (pack != null) {
            consumer.accept(pack);
            REdit.LOGGER.info("Successfully loaded redit pack");
        } else {
            REdit.LOGGER.error("Failed loading redit pack");
        }
    }

    private Pack.ResourcesSupplier getSupplier() {
        return (name) -> new PathPackResources("redit", REdit.getOutputPath(), false);
    }
}
