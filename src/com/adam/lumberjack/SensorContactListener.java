package com.adam.lumberjack;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class SensorContactListener implements ContactListener {
    //Player player;

    SensorContactListener(/*Player player*/) {
        //this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null ) return;
        if (fa.getUserData() == null || fb.getUserData() == null) return;

        universalMobBeginContact(fa, fb);
        trapPlayerBeginContact(fa, fb);
        hutPlayerBeginContact(fa, fb);
        buildSensorBeginContact(fa,fb);
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null ) return;
        if (fa.getUserData() == null || fb.getUserData() == null) return;

        universalMobEndContact(fa, fb);
        hutPlayerEndContact(fa, fb);
        buildSensorEndContact(fa, fb);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
    public void trapPlayerBeginContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof  Trap && fb.getUserData() instanceof Player && !fb.isSensor()) {
            ((Trap) fa.getUserData()).activate(100, (Player) fb.getUserData());
        } else if (fb.getUserData() instanceof  Trap && fa.getUserData() instanceof Player && !fa.isSensor()){
            ((Trap) fb.getUserData()).activate(100, (Player) fa.getUserData());
        }
    }
    public void hutPlayerBeginContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof  Player && fb.getUserData() instanceof Hut && fb.isSensor()) {
            ((Hut) fb.getUserData()).open();
        } else if (fb.getUserData() instanceof  Player && fa.getUserData() instanceof Hut && !fa.isSensor()){
            ((Hut) fb.getUserData()).open();
        }
    }
    public void hutPlayerEndContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof  Player && fb.getUserData() instanceof Hut && fb.isSensor()) {
            ((Hut) fb.getUserData()).close();
        } else if (fb.getUserData() instanceof  Player && fa.getUserData() instanceof Hut && !fa.isSensor()){
            ((Hut) fb.getUserData()).close();
        }
    }
    //actually matches on every GameObject
    public void universalMobBeginContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() == fb.getUserData()) return; //avoid sensor adding body if its from same GameObject
        System.out.println("sensor contact " + fa.getUserData() + " " + (fa.getUserData() instanceof GameObject) +  " " + fb.getUserData() + " " + (fb.getUserData() instanceof GameObject));
        //double mob collisions
        if (fa.getUserData() instanceof  Mob && fa.isSensor() && fb.getUserData() instanceof Mob && fb.isSensor()) {
            System.out.println("double mob sensor touch (maybe for blocking later)");
        } else if (fa.getUserData() instanceof  Mob && fa.isSensor() && fb.getUserData() instanceof Mob) {
            ((Mob) fa.getUserData()).addGameObjectInRange((GameObject) fb.getUserData());
        } else if (fa.getUserData() instanceof  Mob && fb.getUserData() instanceof Mob && fb.isSensor()) {
            ((Mob) fb.getUserData()).addGameObjectInRange((GameObject) fa.getUserData());
        }
        //player - item collisions
        else if (fa.getUserData() instanceof  GameObject && fb.getUserData() instanceof Player && fb.isSensor()) {
            System.out.println("THE RIGHT CONTACT");
            ((Player) fb.getUserData()).addGameObjectInRange((GameObject) fa.getUserData());
        } else if (fb.getUserData() instanceof  GameObject && fa.getUserData() instanceof Player && fa.isSensor()){
            System.out.println("THE RIGHT CONTACT");
            ((Player) fa.getUserData()).addGameObjectInRange((GameObject) fb.getUserData());
        }
    }
    public void universalMobEndContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() == fb.getUserData()) return; //avoid sensor adding body if its from same GameObject
        //System.out.println("sensor contact " + fa.getUserData() + " " + (fa.getUserData() instanceof GameObject) +  " " + fb.getUserData() + " " + (fb.getUserData() instanceof GameObject));
        //double mob collisions
        if (fa.getUserData() instanceof  Mob && fa.isSensor() && fb.getUserData() instanceof Mob && fb.isSensor()) {
            //double mob sensor touch (maybe for blocking later)
        } else if (fa.getUserData() instanceof  Mob && fa.isSensor() && fb.getUserData() instanceof Mob) {
            ((Mob) fa.getUserData()).removeGameObjectInRange((GameObject) fb.getUserData());
        } else if (fa.getUserData() instanceof  Mob && fb.getUserData() instanceof Mob && fb.isSensor()) {
            ((Mob) fb.getUserData()).removeGameObjectInRange((GameObject) fa.getUserData());
        }
        //player - item collisions
        else if (fa.getUserData() instanceof  GameObject && fb.getUserData() instanceof Player && fb.isSensor()) {
            ((Player) fb.getUserData()).removeGameObjectInRange((GameObject) fa.getUserData());
        } else if (fb.getUserData() instanceof  GameObject && fa.getUserData() instanceof Player && fa.isSensor()){
            ((Player) fa.getUserData()).removeGameObjectInRange((GameObject) fb.getUserData());
        }
    }
    /*public void treePlayerEndContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() == fb.getUserData()) return;
        if (fa.getUserData() instanceof  GameObject && fb.getUserData() instanceof Mob && fb.isSensor()) {
            ((Mob) fb.getUserData()).removeGameObjectInRange((GameObject) fa.getUserData());
        } else if (fb.getUserData() instanceof  GameObject && fa.getUserData() instanceof Mob && fa.isSensor()){
            ((Mob) fa.getUserData()).removeGameObjectInRange((GameObject) fb.getUserData());
        }
    }*/
    public void riverBuildSensorBeginContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof BuildSensor || fb.getUserData() instanceof  River) {
            ((BuildSensor) fa.getUserData()).addOverlaping((GameObject) fb.getUserData());
        } else if (fb.getUserData() instanceof BuildSensor || fa.getUserData() instanceof  River) {
            ((BuildSensor) fb.getUserData()).addOverlaping((GameObject) fa.getUserData());
        }
    }
    public void riverBuildSensorEndContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof BuildSensor || fb.getUserData() instanceof  River) {
            ((BuildSensor) fa.getUserData()).removeOverlaping((GameObject) fb.getUserData());
        } else if (fb.getUserData() instanceof BuildSensor || fa.getUserData() instanceof  River) {
            ((BuildSensor) fb.getUserData()).removeOverlaping((GameObject) fa.getUserData());
        }
    }
    public void buildSensorBeginContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof BuildSensor) {
            ((BuildSensor) fa.getUserData()).addOverlaping((GameObject) fb.getUserData());
        } else if (fb.getUserData() instanceof BuildSensor) {
            ((BuildSensor) fb.getUserData()).addOverlaping((GameObject) fa.getUserData());
        }
    }
    public void buildSensorEndContact(Fixture fa, Fixture fb) {
        if (fa.getUserData() instanceof BuildSensor) {
            ((BuildSensor) fa.getUserData()).removeOverlaping((GameObject) fb.getUserData());
        } else if (fb.getUserData() instanceof BuildSensor) {
            ((BuildSensor) fb.getUserData()).removeOverlaping((GameObject) fa.getUserData());
        }
    }
}
