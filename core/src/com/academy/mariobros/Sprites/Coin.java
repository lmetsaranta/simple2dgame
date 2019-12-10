package com.academy.mariobros.Sprites;

import com.academy.mariobros.Items.ItemDef;
import com.academy.mariobros.Items.Mushroom;
import com.academy.mariobros.Items.Suitcase;
import com.academy.mariobros.Items.Sword;
import com.academy.mariobros.MarioBros;
import com.academy.mariobros.Scenes.Hud;
import com.academy.mariobros.Screens.PlayScreen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANC_COIN = 6;

    public Coin(PlayScreen screen, MapObject object, TiledMap tiledMap) {
        super(screen, object, tiledMap);
        tileSet = map.getTileSets().getTileSet("awelementit");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);

    }

    @Override
    public void onHeadHit(Mario mario) {
        if(getCell().getTile().getId() == BLANC_COIN) {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        } else if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
                MarioBros.manager.get("audio/sounds/powerupappears.wav", Sound.class).play();
            Hud.addScore(200);
            getCell().setTile(tileSet.getTile(BLANC_COIN));
            } else if (object.getProperties().containsKey("sword")){
            screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x - 8 / MarioBros.PPM, body.getPosition().y + 8 / MarioBros.PPM), Sword.class));
            Hud.addScore(200);
            getCell().setTile(tileSet.getTile(BLANC_COIN));
        } else if(object.getProperties().containsKey("suitcase")){
            screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x - 7.9f / MarioBros.PPM, body.getPosition().y + 8 / MarioBros.PPM), Suitcase.class));
            Hud.addScore(200);
            getCell().setTile(tileSet.getTile(BLANC_COIN));
        } else {
        MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
            Hud.addScore(200);
            getCell().setTile(tileSet.getTile(BLANC_COIN));
        }
    }

}
