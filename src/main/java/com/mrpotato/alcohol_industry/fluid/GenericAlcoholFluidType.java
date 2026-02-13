package com.mrpotato.alcohol_industry.fluid;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class GenericAlcoholFluidType extends FluidType {
    
    private final String fluidName;
    private final int tintColor;
    
    public GenericAlcoholFluidType(String name, int tintColor, int density, int viscosity, int temperature) {
        super(Properties.create()
            .density(density)
            .viscosity(viscosity)
            .temperature(temperature)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));
        this.fluidName = name;
        this.tintColor = tintColor;
    }
    
    @SuppressWarnings("removal")
    @Override
    public void initializeClient(java.util.function.Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return AlcoholIndustry.id("block/" + fluidName + "_still");
            }
            
            @Override
            public ResourceLocation getFlowingTexture() {
                return AlcoholIndustry.id("block/" + fluidName + "_flow");
            }
            
            @Override
            public int getTintColor() {
                return tintColor;
            }
        });
    }
}