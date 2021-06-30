package org.vivecraft.provider;

import java.util.Arrays;
import net.minecraft.client.KeyMapping;

public class HandedKeyBinding extends KeyMapping
{
    private boolean[] pressed = new boolean[ControllerType.values().length];
    private int[] pressTime = new int[ControllerType.values().length];

    public HandedKeyBinding(String p_90821_, int p_90822_, String p_90823_)
    {
        super(p_90821_, p_90822_, p_90823_);
    }

    public boolean consumeClick()
    {
        return Arrays.stream(ControllerType.values()).map(this::isPressed).reduce(false, (a, b) ->
        {
            return a || b;
        });
    }

    public boolean isDown()
    {
        return Arrays.stream(ControllerType.values()).map(this::isKeyDown).reduce(false, (a, b) ->
        {
            return a || b;
        });
    }

    public boolean isPressed(ControllerType hand)
    {
        if (this.pressTime[hand.ordinal()] > 0)
        {
            int i = this.pressTime[hand.ordinal()]--;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isKeyDown(ControllerType hand)
    {
        return this.pressed[hand.ordinal()];
    }

    public void pressKey(ControllerType hand)
    {
        this.pressed[hand.ordinal()] = true;
        int i = this.pressTime[hand.ordinal()]++;
    }

    public void unpressKey(ControllerType hand)
    {
        this.pressTime[hand.ordinal()] = 0;
        this.pressed[hand.ordinal()] = false;
    }

    public boolean isPriorityOnController(ControllerType type)
    {
        return true;
    }
}
