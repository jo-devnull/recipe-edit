package github.jodevnull.redit.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.jodevnull.redit.REdit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;

public class REditCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            Commands.literal(REdit.MODID)
                .then(Commands.argument("id", ResourceLocationArgument.id())
                    .executes(REditCommand::openRecipe)
                )
        );
    }

    public static int openRecipe(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var recipeId = ResourceLocationArgument.getRecipe(context, "id");

        if (RecipeHandler.openRecipe(recipeId.id()))
            return 1;

        return 0;
    }
}
