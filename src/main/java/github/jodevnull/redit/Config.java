package github.jodevnull.redit;

import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class Config
{
    public static final ForgeConfigSpec mSpec;
    public static final ForgeConfigSpec.Builder mBuilder = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<String> outputPath;
    public static final ForgeConfigSpec.BooleanValue deleteUnedited;

    public static boolean shouldDeleteUnedited() {
        return deleteUnedited.get();
    }

    public static Path getOutputPath() {
        final var path = Path.of(outputPath.get());

        if (path.isAbsolute()) {
            REdit.LOGGER.warn("outputPath should be relative to the minecraft instance!");
            return Path.of(outputPath.getDefault());
        }

        return path;
    }

    static {
        outputPath = mBuilder.comment(" The output directory of the edited recipes.")
            .define("outputDir", "redit/");

        deleteUnedited = mBuilder.comment(" If unedited recipes should be deleted on exit (to avoid redundancy).")
            .define("deleteUnedited", true);

        mSpec = mBuilder.build();
    }
}
