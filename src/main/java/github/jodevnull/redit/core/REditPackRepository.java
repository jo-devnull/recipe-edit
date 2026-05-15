package github.jodevnull.redit.core;

import github.jodevnull.redit.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class REditPackRepository implements RepositorySource
{
    private final static PackSource SOURCE = PackSource.create(packText -> packText, true);
    private final PackType packType = PackType.SERVER_DATA;

    @Override
    public void loadPacks(@NotNull Consumer<Pack> consumer) {
        final var displayName = Component.literal("Edited Recipes");
        final var locationInfo = new PackLocationInfo("redit", displayName, SOURCE, Optional.empty());
        final var selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, true);
        final var supplier = new PathPackResources.PathResourcesSupplier(Config.getOutputPath());
        consumer.accept(Pack.readMetaAndCreate(locationInfo, supplier, this.packType, selectionConfig));
    }
}
