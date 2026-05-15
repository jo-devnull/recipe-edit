package github.jodevnull.redit.mixin.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.screen.EmiScreenManager;
import github.jodevnull.redit.compat.emi.EmiExtensions;
import github.jodevnull.redit.core.RecipeHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(value = EmiScreenManager.class)
public class EmiScreenManagerMixin
{
    @Inject(method = "recipeInteraction", at=@At("HEAD"), cancellable = true)
    private static void redit$editRecipe(EmiRecipe recipe, Function<EmiBind, Boolean> function, CallbackInfoReturnable<Boolean> cir) {
        if (recipe == null) {
            cir.setReturnValue(false);
        } else if (function.apply(EmiExtensions.EditRecipeBinding)) {
            final var location = recipe.getId();

            if (location != null) {
                RecipeHandler.openRecipe(location);
                return;
            }

            cir.setReturnValue(true);
        }
    }
}
