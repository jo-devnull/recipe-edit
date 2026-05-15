package github.jodevnull.redit.compat.emi;

import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.input.EmiBind;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;

import static com.mojang.blaze3d.platform.InputConstants.Type.MOUSE;

public class EmiExtensions
{
    @EmiConfig.Comment("Open the recipe file in your default editor")
    @EmiConfig.ConfigValue("binds.edit-recipe")
    public static EmiBind EditRecipeBinding = new EmiBind("key.emi.edit_recipe",
        new EmiBind.ModifiedKey(MOUSE.getOrCreate(2), 0)
    );

    public static Field[] addBindings(Field[] fields) throws NoSuchFieldException {
        for (int i = 0; i < fields.length; i++) {
            EmiConfig.ConfigValue annot = fields[i].getAnnotation(EmiConfig.ConfigValue.class);

            if (annot != null && annot.value().equals("binds.copy-recipe-id")) {
                return ArrayUtils.insert(i + 1, fields, EmiExtensions.class.getDeclaredField("EditRecipeBinding"));
            }
        }

        return fields;
    }
}
