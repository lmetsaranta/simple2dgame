package com.academy.mariobros.Sprites;

import com.academy.mariobros.MarioBros;
import com.academy.mariobros.Scenes.Hud;
import com.academy.mariobros.Screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object, TiledMap tiledMap) {
        super(screen, object, tiledMap);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()){
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(100);
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        } else {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }
    }
}
