package com.adam.lumberjack;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BatchHelper{
    public SpriteBatch batch;
    public Vector3 fromColor;
    public Vector3 toColor;
    public Vector3 tickChangeAmount;
    public boolean add = false;

    public int susnsetTicks = 3600;
    public int stopTicks = susnsetTicks * 2;
    public int stopTicksRemaining = stopTicks;

    public BatchHelper(SpriteBatch batch, Vector3 from) {
        this.batch = batch;
        this.fromColor = from;
        this.toColor = new Vector3(1,1,1);
        this.batch.setColor(toColor.x, toColor.y, toColor.z, 1);
        this.tickChangeAmount = new Vector3((toColor.x - fromColor.x) / susnsetTicks,(toColor.y - fromColor.y) / susnsetTicks,(toColor.z - fromColor.z) / susnsetTicks);
    }

    public BatchHelper(SpriteBatch batch, Vector3 from, Vector3 to) {
        this.batch = batch;
        this.fromColor = from;
        this.toColor = to;
        this.batch.setColor(toColor.x, toColor.y, toColor.z, 1);
        this.tickChangeAmount = new Vector3((to.x - from.x) / susnsetTicks,(to.y - from.y) / susnsetTicks,(to.z - from.z) / susnsetTicks);
    }

    public void tick() {
        if (stopTicksRemaining > 0) {
            stopTicksRemaining --;
            return;
        }
        Color currentColor = batch.getColor();
        if (currentColor.r <= fromColor.x) {
            if (!add) {
                stopTicksRemaining += stopTicks;
                add = true;
            }
        }
        else if (currentColor.r >= toColor.x) {
            if (add) {
                stopTicksRemaining += stopTicks * 2;
                add = false;
            }
        }
        if (add) {
            batch.setColor(currentColor.r + tickChangeAmount.x, currentColor.g + tickChangeAmount.y, currentColor.b + tickChangeAmount.z, currentColor.a);
        } else {
            batch.setColor(currentColor.r - tickChangeAmount.x, currentColor.g - tickChangeAmount.y, currentColor.b - tickChangeAmount.z, currentColor.a);
        }
    }
}
