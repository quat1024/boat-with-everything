# Boat with Everything

An exercise in how far you have to take a joke before it starts being funny.

## Inspiration

> *"boat with dirt block"*

- *[kingbdogz](https://twitter.com/kingbdogz/status/1513184765804961797)*

## Funny todo

currently it acts like an item frame. the boat stores "what blockstate should render in te boat" and "what itemstack was used to place that block" so that the same itemstack can be returned when you remove the block from the boat. i think this is what players expect, as opposed to vanilla block-like where if you rename stone then place and break it it loses the name

it's a leaky abstraction, though: stuff like flower pots where interacting with it can change the item stack, shulker boxes where the contents of the container should not be spilled on the ground, etc.

so ideally i would instead use the block loot table, right. except there's no actual block that's being broken! and also if you put stone in a boat and remove it you shouldn't get cobblestone so maybe simulate a silk touch tool or something, but also keeping all the itemstack data like name is still important, etc

as well as things like banners which need the block entity information to come from somewhere, and there's nice convenient methods for setting the block entity render up with... an itemstack