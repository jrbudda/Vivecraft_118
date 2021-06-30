package org.vivecraft.gui.physical.interactables;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.utils.math.Quaternion;

public interface Interactable
{
    void render(double var1, int var3);

    Vec3 getPosition(double var1);

    Quaternion getRotation(double var1);

    Vec3 getAnchorPos(double var1);

    Quaternion getAnchorRotation(double var1);

    boolean isEnabled();

default boolean isTouchable()
    {
        return this.isEnabled();
    }

    void touch();

    void untouch();

    void click(int var1);

    void unclick(int var1);

default void update()
    {
    }

default void onDragDrop(Interactable source)
    {
    }

    AABB getBoundingBox();
}
