## ForgeEnergy 2.0
#### A replacement to the current default "one size fits all" system.

The current system for Forge Energy (FE) is, for the most part, a "perfectly fine" system. It's a reasonable default and does what it should well enough.

However, several problems quickly become evident in the modding scene outside the original scope. A universal power system inevitably leads to 'power' creep; it's impossible to determine what is an acceptable scale of power generation or consumption when other mods are thrown in.

Additionally, there's strong pressure to conform *to* the established default, even more so now that such a system is included within Forge. Choosing to do something else becomes an explicit choice to break the mold, for better or worse.

### Standards for a reason
Un/fortunately, though, standards come about for a reason. Having to build three different coal generators and route them effectively gets old for players; part of why we gravitated to a simple, unified solution.

Ideally, if we want to replace this system, we need something that fits the existing desire for an easy solution, while still allowing and *encouraging* mod creativity. Enter: ForgeEnergy 2.0 (Name pending).

### How is this any different?
The biggest change with FE2 is that **there is no default energy**. This is an important change, because of the next step.

With FE2, Forge provides a registry for energy, rather than a single default source. This registry allows (and, in many ways, requires) mods to register an energy type if they want to interact with other mods.

This registered 'energy type' represents a number of useful properties about the energy system - most importantly, what the "value" of 1 unit of energy is. An easy and recommended way to think about this value is how much energy is produced from burning a single piece of coal, since this is a common generation method.

With this, two mods that are completely unrelated can exchange power at a comfortable rate for both of them. Take the two imaginary systems:

```java
//Energy represented as Joules
public static final EnergyType ALPHA = new EnergyType(EnergyType.Properties.create()
                    .energyValue(100_000));

//Energy represented as Bytes
public static final EnergyType BETA = new EnergyType(EnergyType.Properties.create()
        .energyValue(10_000));
```

If mod `ALPHA` has an energy cell storing 1MJ, mod `BETA` can 'see' that energy cell as storing 100KB.
The energy is converted on the fly, as required, between any two types provided.

If, however, mod `BETA` wants to limit how much energy it receives from outside sources, it could set an incoming ratio:

```java
//Energy represented as Bytes
public static final EnergyType BETA = new EnergyType(EnergyType.Properties.create()
        .convertFromSource(0.8f)
        .energyValue(10_000));
```

When converting energy into Bytes, mod `BETA` will only receive 80% of the energy. The other 20% will be lost in transmission.

By encoding these properties as standard parts of FE2, we can build a system that ensures mods can interact with each other, without breaking the balance of each other.

// TODO