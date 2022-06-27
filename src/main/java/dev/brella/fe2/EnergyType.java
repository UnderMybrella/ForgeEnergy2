package dev.brella.fe2;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class EnergyType {
    private String descriptionId;
    private String unitId;

    private final int unitsForCoal;

    private final BiFunction<EnergyType, Integer, Integer> convertTo;

    private final BiFunction<EnergyType, Integer, Integer> convertFrom;

    public EnergyType(Properties properties) {
        this.descriptionId = properties.descriptionId;
        this.unitId = properties.unitsId;
        this.unitsForCoal = properties.unitsForCoal;
        this.convertTo = properties.convertTo;
        this.convertFrom = properties.convertFrom;
    }

    /**
     * Returns the component representing the name of the energy type.
     *
     * @return the component representing the name of the energy type
     */
    public Component getDescription() {
        return Component.translatable(this.getDescriptionId());
    }

    public Component getUnits() {
        return Component.translatable(this.getUnitId());
    }

    public int getUnitsForCoal() {
        return this.unitsForCoal;
    }

    public BiFunction<EnergyType, Integer, Integer> getConvertFrom() {
        return this.convertFrom;
    }

    public BiFunction<EnergyType, Integer, Integer> getConvertTo() {
        return this.convertTo;
    }

    public int convertFrom(EnergyType src, int amount) {
        return this.convertFrom.apply(src, amount);
    }

    public int convertTo(EnergyType dst, int amount) {
        return this.convertTo.apply(dst, amount);
    }

    /**
     * Returns the identifier representing the name of the energy type.
     * If no identifier was specified, then the identifier will be defaulted
     * to {@code energy_type.<modid>.<registry_name>}.
     *
     * @return the identifier representing the name of the energy type
     */
    public String getDescriptionId() {
        if (this.descriptionId == null)
            this.descriptionId = Util.makeDescriptionId("energy_type", FE2.REGISTRY.get().getKey(this));
        return this.descriptionId;
    }

    public String getUnitId() {
        if (this.unitId == null)
            this.unitId = Util.makeDescriptionId("energy_type.units", FE2.REGISTRY.get().getKey(this));
        return this.unitId;
    }

    @Override
    public String toString() {
        @Nullable ResourceLocation name = FE2.REGISTRY.get().getKey(this);
        return name != null ? name.toString() : "Unregistered EnergyType";
    }

    public boolean is(TagKey<EnergyType> key) {
        return FE2.getTag(key).map(tag -> tag.contains(this)).orElse(false);
    }

    public static final class Properties {
        private String descriptionId;

        private String unitsId;

        private int unitsForCoal;

        private BiFunction<EnergyType, Integer, Integer> convertTo;

        private BiFunction<EnergyType, Integer, Integer> convertFrom;

        private Properties() {
        }

        /**
         * Creates a new instance of the properties.
         *
         * @return the property holder instance
         */
        public static Properties create() {
            return new Properties();
        }

        /**
         * Sets the identifier representing the name of the energy type.
         *
         * @param descriptionId the identifier representing the name of the energy type
         * @return the property holder instance
         */
        public Properties descriptionId(String descriptionId) {
            this.descriptionId = descriptionId;
            return this;
        }

        public Properties unitsId(String unitsId) {
            this.unitsId = unitsId;
            return this;
        }

        public Properties unitsForCoal(int unitsForCoal) {
            this.unitsForCoal = unitsForCoal;
            return this;
        }

        public Properties convertFrom() {
            this.convertFrom = (src, amount) -> amount;
            return this;
        }

        public Properties convertFrom(float ratio) {
            this.convertFrom = (src, amount) -> Math.round(amount * ratio);

            return this;
        }

        public Properties convertFrom(BiFunction<EnergyType, Integer, Integer> convertFrom) {
            this.convertFrom = convertFrom;
            return this;
        }

        public Properties convertTo() {
            this.convertTo = (dst, amount) -> amount;
            return this;
        }

        public Properties convertTo(float ratio) {
            this.convertTo = (dst, amount) -> Math.round(amount * ratio);
            return this;
        }

        public Properties convertTo(BiFunction<EnergyType, Integer, Integer> convertTo) {
            this.convertTo = convertTo;
            return this;
        }
    }
}
