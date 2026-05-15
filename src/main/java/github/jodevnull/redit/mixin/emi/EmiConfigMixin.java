package github.jodevnull.redit.mixin.emi;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.emi.emi.config.EmiConfig;
import github.jodevnull.redit.compat.emi.EmiExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Field;

@Mixin(EmiConfig.class)
public class EmiConfigMixin
{
    @ModifyExpressionValue(method = "loadConfig(Ldev/emi/emi/com/unascribed/qdcss/QDCSS;)V", at= @At(value = "INVOKE", target = "Ljava/lang/Class;getFields()[Ljava/lang/reflect/Field;"))
    private static Field[] insomnai$loadConfig(Field[] fields) throws NoSuchFieldException {
        return EmiExtensions.addBindings(fields);
    }

    @ModifyExpressionValue(method = "getSavedConfig", at= @At(value = "INVOKE", target = "Ljava/lang/Class;getFields()[Ljava/lang/reflect/Field;"))
    private static Field[] insomnai$getSavedConfig(Field[] fields) throws NoSuchFieldException {
        return EmiExtensions.addBindings(fields);
    }
}
