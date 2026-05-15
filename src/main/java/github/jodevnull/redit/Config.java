package github.jodevnull.redit;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

public class Config
{
    public static final ModConfigSpec mSpec;
    public static final ModConfigSpec.Builder mBuilder = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> outputPath;
    public static final ModConfigSpec.BooleanValue deleteUnedited;

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
